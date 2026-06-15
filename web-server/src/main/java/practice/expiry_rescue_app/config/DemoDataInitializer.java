package practice.expiry_rescue_app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.entity.*;
import practice.expiry_rescue_app.enums.InventoryStatus;
import practice.expiry_rescue_app.enums.OrderStatus;
import practice.expiry_rescue_app.repository.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Seeds a rich sample dataset for the portfolio demo. Only active when app.demo.enabled=true.
 * <ul>
 *   <li>The demo user is ensured on every startup (idempotent by email).</li>
 *   <li>Catalog + sample orders are seeded only when the DB has no cities yet, so this never
 *       duplicates data or clobbers a populated database.</li>
 *   <li>Each inventory row stores {@code expiryOffsetDays} so {@link DemoExpiryRefresher} can keep
 *       expiry dates fresh over time.</li>
 * </ul>
 * Tune the volume/variety with the constants below.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {

    private static final long DAY_MS = 86_400_000L;

    // ---- tunable dataset knobs ----
    private static final long RNG_SEED = 42L;            // deterministic output
    private static final int STOCK_FRACTION_PCT = 55;    // % of the catalog each store stocks
    private static final int TWO_BATCH_PCT = 25;         // % of stocked products that get a 2nd batch
    private static final int OUT_OF_STOCK_PCT = 8;       // % of inventory rows with 0 quantity
    private static final int MAX_QTY = 50;
    private static final int DEMO_ORDER_COUNT = 7;

    // {city, district, district, ...}
    private static final String[][] GEOGRAPHY = {
            {"Westbridge", "Riverside", "Oakdale", "Hillcrest"},
            {"Marlowe", "Eastgate", "Brookfield"},
            {"Ashford", "Kingsway", "Meadowbrook"},
    };

    // {name, address, districtName, openHour, closeHour}
    private static final String[][] SUPERMARKETS = {
            {"Evergreen Market", "12 Maple Street", "Riverside", "7", "22"},
            {"Harbor Foods", "45 Oak Avenue", "Riverside", "8", "21"},
            {"Sunrise Grocers", "88 Birch Lane", "Oakdale", "6", "12"},
            {"Maple & Main", "3 Cedar Road", "Oakdale", "9", "20"},
            {"Hillcrest Pantry", "27 Pine Street", "Hillcrest", "7", "23"},
            {"Eastgate Fresh", "5 Elm Court", "Eastgate", "8", "22"},
            {"Brook Street Market", "61 Willow Way", "Brookfield", "0", "23"},
            {"Cornerstone Foods", "14 Ash Boulevard", "Brookfield", "7", "21"},
            {"Kingsway Grocers", "99 Chestnut Street", "Kingsway", "8", "20"},
            {"The Daily Basket", "8 Hawthorn Lane", "Kingsway", "6", "14"},
            {"Meadowbrook Market", "42 Sycamore Drive", "Meadowbrook", "9", "22"},
            {"Greenleaf Grocery", "17 Magnolia Road", "Meadowbrook", "7", "19"},
    };

    // {category, product, product, ...}
    private static final String[][] CATALOG = {
            {"Dairy", "Whole Milk 1L", "Greek Yogurt 500g", "Cheddar Block 250g", "Salted Butter 200g",
                    "Mozzarella 150g", "Cream Cheese 200g", "Sour Cream 300ml", "Cottage Cheese 250g",
                    "Oat Milk 1L", "Parmesan 100g"},
            {"Bakery", "Sourdough Loaf", "Butter Croissant", "Baguette", "Whole Wheat Bread",
                    "Cinnamon Roll", "Bagel 4pk", "Chocolate Muffin", "Dinner Rolls 6pk",
                    "Focaccia", "Banana Bread"},
            {"Produce", "Gala Apples 1kg", "Bananas 1kg", "Roma Tomatoes 500g", "Baby Spinach 200g",
                    "Carrots 1kg", "Avocado 2pk", "Strawberries 250g", "Broccoli 500g",
                    "Red Grapes 500g", "Cucumber 3pk"},
            {"Meat & Seafood", "Chicken Breast 500g", "Ground Beef 500g", "Pork Chops 600g",
                    "Salmon Fillet 300g", "Bacon 250g", "Sausages 400g", "Shrimp 300g",
                    "Turkey Mince 500g", "Lamb Chops 500g", "Cod Fillet 300g"},
            {"Beverages", "Orange Juice 1L", "Cola 1.5L", "Sparkling Water 1L", "Iced Tea 500ml",
                    "Apple Juice 1L", "Energy Drink 250ml", "Lemonade 1L", "Cold Brew Coffee 750ml",
                    "Tonic Water 1L", "Coconut Water 500ml"},
            {"Snacks", "Potato Chips 150g", "Salted Pretzels 200g", "Dark Chocolate 100g",
                    "Trail Mix 250g", "Granola Bars 6pk", "Popcorn 100g", "Rice Crackers 150g",
                    "Mixed Nuts 200g", "Cookies 300g", "Tortilla Chips 200g"},
            {"Frozen", "Frozen Peas 1kg", "Vanilla Ice Cream 1L", "Frozen Pizza", "Fish Fingers 400g",
                    "Frozen Berries 500g", "French Fries 1kg", "Frozen Dumplings 500g",
                    "Frozen Lasagne 400g", "Frozen Corn 1kg", "Ice Cream Sandwiches 6pk"},
            {"Pantry", "Spaghetti 500g", "Olive Oil 500ml", "Canned Tomatoes 400g", "Basmati Rice 1kg",
                    "Peanut Butter 350g", "Honey 250g", "Cereal 500g", "Tomato Ketchup 500ml",
                    "Strawberry Jam 340g", "Black Beans 400g"},
    };

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final SupermarketRepository supermarketRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductMasterRepository productMasterRepository;
    private final ProductInventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    @Value("${app.demo.email}")
    private String demoEmail;

    private final Random rng = new Random(RNG_SEED);

    @Override
    @Transactional
    public void run(String... args) {
        User demoUser = ensureDemoUser();

        if (cityRepository.count() > 0) {
            log.info("Demo: catalog already present, skipping catalog/order seeding.");
            return;
        }

        log.info("Demo: seeding sample catalog and orders.");
        seedCatalogAndOrders(demoUser);
    }

    private User ensureDemoUser() {
        String email = demoEmail.trim().toLowerCase();
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setUsername(email);
            user.setEmail(email);
            user.setFullName("Demo User");
            user.setProvider("email");
            user.setIsActive(true);
            log.info("Demo: created demo user {}", email);
            return userRepository.save(user);
        });
    }

    private void seedCatalogAndOrders(User demoUser) {
        // --- Geography ---
        Map<String, District> districts = new HashMap<>();
        for (String[] row : GEOGRAPHY) {
            City city = saveCity(row[0]);
            for (int i = 1; i < row.length; i++) {
                districts.put(row[i], saveDistrict(city, row[i]));
            }
        }

        List<Supermarket> supermarkets = new ArrayList<>();
        for (String[] s : SUPERMARKETS) {
            supermarkets.add(saveSupermarket(s[0], s[1], districts.get(s[2]),
                    LocalTime.of(Integer.parseInt(s[3]), 0), LocalTime.of(Integer.parseInt(s[4]), 0)));
        }

        // --- Categories + products ---
        List<ProductMaster> products = new ArrayList<>();
        for (String[] row : CATALOG) {
            String categoryName = row[0];
            ProductCategory category = saveCategory(categoryName, defaultDays(categoryName));
            for (int i = 1; i < row.length; i++) {
                products.add(saveProduct(row[i], category, unitFor(categoryName)));
            }
        }

        // --- Inventory: each store stocks a random subset, with varied expiry/discount/stock ---
        List<ProductInventory> inStock = new ArrayList<>();
        for (Supermarket store : supermarkets) {
            for (ProductMaster product : products) {
                if (rng.nextInt(100) >= STOCK_FRACTION_PCT) continue;
                int batches = rng.nextInt(100) < TWO_BATCH_PCT ? 2 : 1;
                for (int b = 0; b < batches; b++) {
                    ProductInventory inv = generateInventory(product, store);
                    if (inv.getQuantityAvailable() > 0) inStock.add(inv);
                }
            }
        }
        log.info("Demo: created {} inventory rows across {} stores", inStock.size(), supermarkets.size());

        // --- Sample orders for the demo user (mix of confirmed + cancelled) ---
        for (int i = 1; i <= DEMO_ORDER_COUNT && !inStock.isEmpty(); i++) {
            int itemCount = 1 + rng.nextInt(4);
            List<OrderItem> items = new ArrayList<>();
            for (int j = 0; j < itemCount; j++) {
                ProductInventory inv = inStock.get(rng.nextInt(inStock.size()));
                items.add(item(inv, inv.getProductMaster(), 1 + rng.nextInt(3)));
            }
            OrderStatus status = (i % 3 == 0) ? OrderStatus.CANCELLED : OrderStatus.CONFIRMED;
            saveOrder(demoUser, String.format("DEMO-%04d", i), status, items);
        }

        log.info("Demo: seeding complete.");
    }

    // ---- inventory generation ----

    private ProductInventory generateInventory(ProductMaster product, Supermarket store) {
        int offsetDays = pickOffsetDays();
        int quantity = pickQuantity();
        int[] range = priceRangeCents(product.getCategory().getName());
        int originalCents = range[0] + rng.nextInt(range[1] - range[0] + 1);
        int discountPct = pickDiscountPct(offsetDays);
        int sellingCents = (int) Math.round(originalCents * (100 - discountPct) / 100.0);

        ProductInventory inv = new ProductInventory();
        inv.setProductMaster(product);
        inv.setSupermarket(store);
        inv.setOriginalPrice(BigDecimal.valueOf(originalCents, 2));
        inv.setSellingPrice(BigDecimal.valueOf(sellingCents, 2));
        inv.setQuantityAvailable(quantity);
        inv.setExpiryOffsetDays(offsetDays);
        inv.setExpiryDate(System.currentTimeMillis() + (long) offsetDays * DAY_MS);
        inv.setStatus(quantity > 0 ? InventoryStatus.AVAILABLE : InventoryStatus.NOT_AVAILABLE);
        return inventoryRepository.save(inv);
    }

    // ~25% ending soon (1-3d), ~45% mid (4-14d), ~30% longer (15-30d)
    private int pickOffsetDays() {
        double r = rng.nextDouble();
        if (r < 0.25) return 1 + rng.nextInt(3);
        if (r < 0.70) return 4 + rng.nextInt(11);
        return 15 + rng.nextInt(16);
    }

    private int pickQuantity() {
        if (rng.nextInt(100) < OUT_OF_STOCK_PCT) return 0;
        return 1 + rng.nextInt(MAX_QTY);
    }

    // Steeper discounts the closer to expiry, so near-expiry deals stand out.
    private int pickDiscountPct(int offsetDays) {
        if (offsetDays <= 3) return 40 + rng.nextInt(21);   // 40-60%
        if (offsetDays <= 14) return 20 + rng.nextInt(21);  // 20-40%
        return 10 + rng.nextInt(16);                        // 10-25%
    }

    // ---- per-category attributes ----

    private int defaultDays(String category) {
        return switch (category) {
            case "Bakery" -> 3;
            case "Meat & Seafood" -> 5;
            case "Produce" -> 7;
            case "Dairy" -> 14;
            case "Beverages" -> 30;
            case "Snacks" -> 60;
            case "Frozen" -> 120;
            case "Pantry" -> 180;
            default -> 14;
        };
    }

    private String unitFor(String category) {
        return switch (category) {
            case "Bakery" -> "loaf";
            case "Beverages" -> "bottle";
            case "Produce" -> "bag";
            default -> "pack";
        };
    }

    private int[] priceRangeCents(String category) {
        return switch (category) {
            case "Dairy" -> new int[]{150, 650};
            case "Bakery" -> new int[]{120, 600};
            case "Produce" -> new int[]{100, 700};
            case "Meat & Seafood" -> new int[]{400, 1600};
            case "Beverages" -> new int[]{100, 500};
            case "Snacks" -> new int[]{150, 600};
            case "Frozen" -> new int[]{250, 900};
            case "Pantry" -> new int[]{150, 800};
            default -> new int[]{100, 500};
        };
    }

    // ---- builders ----

    private City saveCity(String name) {
        City c = new City();
        c.setName(name);
        c.setIsActive(true);
        return cityRepository.save(c);
    }

    private District saveDistrict(City city, String name) {
        District d = new District();
        d.setCity(city);
        d.setName(name);
        d.setIsActive(true);
        return districtRepository.save(d);
    }

    private Supermarket saveSupermarket(String name, String address, District district,
            LocalTime from, LocalTime to) {
        Supermarket s = new Supermarket();
        s.setName(name);
        s.setAddress(address);
        s.setDistrict(district);
        s.setOperatingHoursFrom(from);
        s.setOperatingHoursTo(to);
        s.setIsActive(true);
        return supermarketRepository.save(s);
    }

    private ProductCategory saveCategory(String name, int defaultDaysBeforeExpiry) {
        ProductCategory c = new ProductCategory();
        c.setName(name);
        c.setDefaultDaysBeforeExpiry(defaultDaysBeforeExpiry);
        return categoryRepository.save(c);
    }

    private ProductMaster saveProduct(String name, ProductCategory category, String unit) {
        ProductMaster p = new ProductMaster();
        p.setName(name);
        p.setCategory(category);
        p.setUnit(unit);
        p.setDescription(name);
        p.setDaysBeforeExpiry(category.getDefaultDaysBeforeExpiry());
        p.setIsActive(true);
        return productMasterRepository.save(p);
    }

    private OrderItem item(ProductInventory inv, ProductMaster product, int quantity) {
        OrderItem oi = new OrderItem();
        oi.setProductInventoryId(inv.getId());
        oi.setProductMasterId(product.getId());
        oi.setProductName(product.getName());
        oi.setQuantity(quantity);
        oi.setUnit(product.getUnit());
        oi.setOriginalPrice(inv.getOriginalPrice());
        oi.setPrice(inv.getSellingPrice());
        oi.setSubtotal(inv.getSellingPrice().multiply(BigDecimal.valueOf(quantity)));
        oi.setSupermarketId(inv.getSupermarket().getId());
        oi.setSupermarketName(inv.getSupermarket().getName());
        oi.setExpiryDate(inv.getExpiryDate());
        return oi;
    }

    private void saveOrder(User user, String orderNumber, OrderStatus status, List<OrderItem> items) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setStatus(status);
        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        items.forEach(order::addItem);
        orderRepository.save(order);
    }
}
