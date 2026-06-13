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
import java.util.List;

/**
 * Seeds sample data for the portfolio demo. Only active when app.demo.enabled=true.
 * - The demo user is ensured on every startup (idempotent by email).
 * - Catalog + sample orders are seeded only when the DB has no cities yet, so this
 * never duplicates data or clobbers a populated database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {

    private static final long DAY_MS = 86_400_000L;

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
        // --- Location ---
        City city = saveCity("Westbridge");
        District riverside = saveDistrict(city, "Riverside");
        District oakdale = saveDistrict(city, "Oakdale");

        Supermarket evergreen = saveSupermarket("Evergreen Market", "12 Maple Street", riverside,
                LocalTime.of(7, 0), LocalTime.of(22, 0));
        Supermarket harborFoods = saveSupermarket("Harbor Foods", "45 Oak Avenue", riverside,
                LocalTime.of(8, 0), LocalTime.of(21, 0));
        Supermarket sunrise = saveSupermarket("Sunrise Grocers", "88 Birch Lane", oakdale,
                LocalTime.of(6, 0), LocalTime.of(12, 0));

        // --- Categories ---
        ProductCategory dairy = saveCategory("Dairy", 14);
        ProductCategory bakery = saveCategory("Bakery", 3);
        ProductCategory produce = saveCategory("Produce", 7);
        ProductCategory meat = saveCategory("Meat", 5);

        // --- Products ---
        ProductMaster milk = saveProduct("Fresh Milk 1L", dairy, "carton", "Whole milk, pasteurised");
        ProductMaster yogurt = saveProduct("Greek Yogurt 500g", dairy, "tub", "Plain Greek yogurt");
        ProductMaster bread = saveProduct("Sourdough Loaf", bakery, "loaf", "Artisan sourdough");
        ProductMaster croissant = saveProduct("Butter Croissant", bakery, "piece", "All-butter croissant");
        ProductMaster apples = saveProduct("Gala Apples 1kg", produce, "bag", "Crisp Gala apples");
        ProductMaster chicken = saveProduct("Chicken Breast 500g", meat, "pack", "Skinless chicken breast");

        // --- Inventory (varied expiry/discount/stock across stores; prices in USD) ---
        // Evergreen Market
        ProductInventory milkFresh = saveInventory(milk, evergreen, "3.20", "1.90", 12, 2);   // ending soon
        saveInventory(yogurt, evergreen, "4.50", "2.90", 8, 5);
        saveInventory(bread, evergreen, "5.50", "3.50", 4, 1);                                 // ending soon
        ProductInventory croissantFresh = saveInventory(croissant, evergreen, "1.80", "1.00", 20, 3);

        // Harbor Foods
        saveInventory(apples, harborFoods, "6.00", "4.20", 15, 6);
        saveInventory(milk, harborFoods, "3.20", "2.20", 0, 2);                                 // out of stock
        saveInventory(croissant, harborFoods, "1.80", "1.20", 6, 2);                            // ending soon

        // Sunrise Grocers
        saveInventory(chicken, sunrise, "9.00", "5.90", 10, 4);
        saveInventory(apples, sunrise, "6.00", "3.90", 9, 7);
        saveInventory(yogurt, sunrise, "4.50", "3.10", 0, 8);                                // out of stock

        // --- Sample orders for the demo user ---
        saveOrder(demoUser, "DEMO-0001", OrderStatus.CONFIRMED, List.of(
                item(milkFresh, milk, 2),
                item(croissantFresh, croissant, 3)));
        saveOrder(demoUser, "DEMO-0002", OrderStatus.CANCELLED, List.of(
                item(croissantFresh, croissant, 1)));

        log.info("Demo: seeding complete.");
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

    private ProductMaster saveProduct(String name, ProductCategory category, String unit, String description) {
        ProductMaster p = new ProductMaster();
        p.setName(name);
        p.setCategory(category);
        p.setUnit(unit);
        p.setDescription(description);
        p.setDaysBeforeExpiry(category.getDefaultDaysBeforeExpiry());
        p.setIsActive(true);
        return productMasterRepository.save(p);
    }

    private ProductInventory saveInventory(ProductMaster product, Supermarket supermarket,
            String originalPrice, String sellingPrice, int quantity, int expiryDaysFromNow) {
        ProductInventory inv = new ProductInventory();
        inv.setProductMaster(product);
        inv.setSupermarket(supermarket);
        inv.setOriginalPrice(new BigDecimal(originalPrice));
        inv.setSellingPrice(new BigDecimal(sellingPrice));
        inv.setQuantityAvailable(quantity);
        inv.setExpiryDate(System.currentTimeMillis() + (long) expiryDaysFromNow * DAY_MS);
        inv.setStatus(quantity > 0 ? InventoryStatus.AVAILABLE : InventoryStatus.NOT_AVAILABLE);
        return inventoryRepository.save(inv);
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
