<template>
  <div>
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading supermarket details...</p>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded"
    >
      <p><strong>Error:</strong> {{ error }}</p>
    </div>

    <!-- Supermarket Details -->
    <div v-else-if="supermarket">
      <!-- Supermarket Header -->
      <div class="bg-white p-6 rounded-xl shadow mb-6">
        <h1 class="text-3xl font-bold text-gray-800">{{ supermarket.name }}</h1>
        <div class="mt-4 space-y-2">
          <p class="text-gray-600">
            <span class="font-medium">Address:</span> {{ supermarket.address }}
          </p>
          <p v-if="supermarket.district" class="text-gray-600">
            <span class="font-medium">District:</span> {{ supermarket.district }}
          </p>
          <p v-if="supermarket.phone" class="text-gray-600">
            <span class="font-medium">Phone:</span> {{ supermarket.phone }}
          </p>
          <p v-if="supermarket.contactPerson" class="text-gray-600">
            <span class="font-medium">Contact Person:</span> {{ supermarket.contactPerson }}
          </p>
          <p v-if="supermarket.operatingHoursFrom && supermarket.operatingHoursTo" class="text-gray-600">
            <span class="font-medium">Operating Hours:</span>
            {{ supermarket.operatingHoursFrom }} - {{ supermarket.operatingHoursTo }}
          </p>
        </div>
      </div>

      <!-- Category Filter Dropdown -->
      <div class="bg-white p-4 rounded-xl shadow mb-6">
        <div class="flex items-center gap-4">
          <label for="categoryFilter" class="font-semibold">Filter by Category:</label>
          <select
            id="categoryFilter"
            v-model="selectedCategoryFilter"
            class="border border-gray-300 rounded-lg px-4 py-2 focus:ring-green-600 focus:border-green-600 min-w-[250px]"
          >
            <option value="all">All Categories</option>
            <option
              v-for="category in allCategories"
              :key="category.id"
              :value="category.id"
            >
              {{ category.name }}
            </option>
          </select>
        </div>
      </div>

      <!-- Products by Category -->
      <div>
        <h2 class="text-2xl font-bold mb-6">Products by Category</h2>

        <div
          v-for="category in filteredCategoriesWithProducts"
          :key="category.id"
          class="mb-8"
        >
          <!-- Category Header -->
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-xl font-semibold text-green-700">
              {{ category.name }} ({{ category.products.length }})
            </h3>
          </div>

          <!-- No Products Message -->
          <div v-if="category.products.length === 0" class="text-center py-8 bg-gray-50 rounded-lg">
            <p class="text-gray-500">No available products in this category</p>
          </div>

          <!-- Products Grid - 4 per row -->
          <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            <ProductCard
              v-for="product in getVisibleProducts(category.id, category.products)"
              :key="product.id"
              :product="product"
              :supermarket-id="supermarketId"
            />
          </div>

          <!-- Show More Button -->
          <div
            v-if="category.products.length > 4 && !expandedCategories[category.id]"
            class="text-center mt-4"
          >
            <button
              @click="toggleCategory(category.id)"
              class="btn bg-green-600 text-white hover:bg-green-700"
            >
              Show More ({{ category.products.length - 4 }} more)
            </button>
          </div>

          <!-- Show Less Button -->
          <div
            v-if="expandedCategories[category.id]"
            class="text-center mt-4"
          >
            <button
              @click="toggleCategory(category.id)"
              class="btn bg-gray-500 text-white hover:bg-gray-600"
            >
              Show Less
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import SupermarketService from "~/services/supermarket.service";
import ProductCategoryService from "~/services/product-category.service";
import ProductCard from "@/components/ui/ProductCard.vue";

const route = useRoute();
const supermarketId = route.params.id;

// State
const supermarket = ref(null);
const products = ref([]);
const allCategories = ref([]);
const selectedCategoryFilter = ref("all");
const loading = ref(true);
const error = ref(null);
const expandedCategories = ref({});

// Fetch supermarket details with products
const fetchSupermarketWithProducts = async () => {
  try {
    const response = await SupermarketService.getSupermarketWithProducts(
      supermarketId,
      (err) => {
        console.error("Error fetching supermarket:", err);
        error.value = err.response?.data?.message || "Failed to load supermarket";
      }
    );

    if (response && response.data) {
      supermarket.value = response.data;

      // Process products from the combined response
      // Group products by product_master_id
      const productMap = new Map();

      for (const item of response.data.products) {
        const key = item.productMasterId;

        if (productMap.has(key)) {
          // Product master already exists, combine quantities
          const existingProduct = productMap.get(key);
          existingProduct.quantityAvailable += item.quantityAvailable;
          existingProduct.inventoryItems.push(item);

          // Use the earliest expiry date
          if (item.expiryDate < existingProduct.earliestExpiryDate) {
            existingProduct.earliestExpiryDate = item.expiryDate;
            existingProduct.sellUntil = formatDate(item.expiryDate);
            existingProduct.expire = formatDate(item.expiryDate);
            existingProduct.sellDays = calculateDaysUntil(item.expiryDate);
            existingProduct.expireDays = calculateDaysUntil(item.expiryDate);
          }

          // Recalculate availability based on combined data
          const totalQuantity = existingProduct.quantityAvailable;
          const earliestExpiry = existingProduct.earliestExpiryDate;
          const hasAvailableStatus = existingProduct.inventoryItems.some(
            (inv) => inv.status === "AVAILABLE"
          );

          existingProduct.availability = calculateAvailability(
            earliestExpiry,
            totalQuantity,
            hasAvailableStatus ? "AVAILABLE" : "NOT_AVAILABLE"
          );
        } else {
          // First occurrence of this product master
          const availability = calculateAvailability(
            item.expiryDate,
            item.quantityAvailable,
            item.status
          );

          productMap.set(key, {
            id: item.id,
            productMasterId: item.productMasterId,
            categoryId: item.categoryId,
            categoryName: item.categoryName,
            emoji: getCategoryEmoji(item.categoryName),
            category: item.categoryName,
            name: item.productName,
            location: item.supermarketName,
            sellUntil: formatDate(item.expiryDate),
            expire: formatDate(item.expiryDate),
            sellDays: calculateDaysUntil(item.expiryDate),
            expireDays: calculateDaysUntil(item.expiryDate),
            oldPrice: `${item.originalPrice.toLocaleString()}₫`,
            newPrice: `${item.sellingPrice.toLocaleString()}₫`,
            discount: calculateDiscount(item.originalPrice, item.sellingPrice),
            availability: availability,
            quantityAvailable: item.quantityAvailable,
            earliestExpiryDate: item.expiryDate,
            inventoryItems: [item], // Store all inventory items for this product
          });
        }
      }

      // Convert map to array
      products.value = Array.from(productMap.values());
    }
  } catch (err) {
    console.error("Error:", err);
    error.value = "Failed to load supermarket details";
  } finally {
    loading.value = false;
  }
};

// Fetch all categories
const fetchCategories = async () => {
  try {
    const response = await ProductCategoryService.getAllCategories((err) => {
      console.error("Error fetching categories:", err);
    });

    if (response && response.data) {
      allCategories.value = response.data;
    }
  } catch (err) {
    console.error("Error fetching categories:", err);
  }
};

// Helper functions
const formatDate = (timestamp) => {
  if (!timestamp) return "N/A";
  const date = new Date(timestamp);
  return date.toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
};

const calculateDaysUntil = (timestamp) => {
  if (!timestamp) return "N/A";
  const now = new Date().getTime();
  const diff = timestamp - now;
  const days = Math.ceil(diff / (1000 * 60 * 60 * 24));

  if (days < 0) return "Expired";
  if (days === 0) return "Today";
  if (days === 1) return "1 day";
  return `${days} days`;
};

const calculateDiscount = (originalPrice, sellingPrice) => {
  if (!originalPrice || !sellingPrice) return "";
  const discount = Math.round(
    ((originalPrice - sellingPrice) / originalPrice) * 100
  );
  return `-${discount}%`;
};

// Calculate availability based on expiry date and quantity
const calculateAvailability = (expiryDate, quantityAvailable, status) => {
  // If status is not AVAILABLE, mark as out of stock
  if (status !== "AVAILABLE") {
    return "out of stock";
  }

  // Check if product has expired
  const now = new Date().getTime();
  if (expiryDate <= now) {
    return "out of stock";
  }

  // Check if product is expiring soon (within 3 days)
  const daysUntilExpiry = Math.ceil((expiryDate - now) / (1000 * 60 * 60 * 24));
  if (daysUntilExpiry <= 3) {
    return "limited";
  }

  // Check quantity
  if (quantityAvailable === 0) {
    return "out of stock";
  } else if (quantityAvailable <= 10) {
    return "limited";
  }

  return "available";
};

// Get emoji for category
const getCategoryEmoji = (categoryName) => {
  if (!categoryName) return "🛒";

  const lowerName = categoryName.toLowerCase();
  const emojiMap = {
    "dairy": "🧀",
    "bakery": "🥐",
    "beverages": "🥤",
    "spices": "🌶️",
    "cosmetics": "💄",
    "meat": "🍖",
    "seafood": "🦐",
    "produce": "🥬",
    "fruits": "🍎",
    "vegetables": "🥕",
  };

  // Check if category name contains any of the keywords
  for (const [keyword, emoji] of Object.entries(emojiMap)) {
    if (lowerName.includes(keyword)) {
      return emoji;
    }
  }

  return "🛒";
};

// Get filtered categories with their products
const filteredCategoriesWithProducts = computed(() => {
  let categoriesToShow = allCategories.value;

  // Filter by selected category if not "all"
  if (selectedCategoryFilter.value !== "all") {
    categoriesToShow = categoriesToShow.filter(
      (cat) => cat.id === selectedCategoryFilter.value
    );
  }

  // Map categories to include their products
  return categoriesToShow.map((category) => ({
    ...category,
    products: products.value.filter((p) => p.categoryId === category.id),
  }));
});

// Get visible products (4 initially, all when expanded)
const getVisibleProducts = (categoryId, products) => {
  if (expandedCategories.value[categoryId]) {
    return products;
  }
  return products.slice(0, 4);
};

// Toggle category expansion
const toggleCategory = (categoryId) => {
  expandedCategories.value[categoryId] = !expandedCategories.value[categoryId];
};

// Load data on mount
onMounted(async () => {
  await Promise.all([
    fetchSupermarketWithProducts(),
    fetchCategories(),
  ]);
});
</script>

<style scoped>
@reference "tailwindcss";
.btn {
  @apply px-4 py-2 rounded-lg border transition;
}
</style>
