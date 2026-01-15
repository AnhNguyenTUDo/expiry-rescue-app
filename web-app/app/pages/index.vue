<template>
  <div>
    <!-- Controls -->
    <div
      class="bg-white p-6 rounded-xl shadow flex flex-wrap gap-4 items-center"
    >
      <div class="flex flex-col md:flex-row gap-2 md:items-center">
        <label for="category" class="font-semibold">Category:</label>
        <select
          id="category"
          v-model="selectedCategory"
          class="border border-gray-300 rounded px-3 py-2 focus:ring-green-600 focus:border-green-600"
        >
          <option value="all">All Categories</option>
          <option value="food">🍞 Food Products</option>
          <option value="spices">🌶️ Spices</option>
          <option value="beverages">🥤 Beverages</option>
          <option value="cosmetics">💄 Cosmetics</option>
          <option value="dairy">🧀 Dairy</option>
          <option value="bakery">🥐 Bakery</option>
        </select>
      </div>

      <div class="flex flex-col md:flex-row gap-2 md:items-center">
        <label for="district" class="font-semibold">District:</label>
        <select
          id="district"
          v-model="selectedDistrict"
          @change="onDistrictChange"
          class="border border-gray-300 rounded px-3 py-2 focus:ring-green-600 focus:border-green-600 min-w-[200px]"
        >
          <option value="all">All Districts</option>
          <option v-for="district in districts" :key="district" :value="district">
            {{ district }}
          </option>
        </select>
      </div>
    </div>

    <!-- Supermarkets Section - Show All Districts -->
    <div class="mt-8">
      <!-- Show single district when selected -->
      <div v-if="selectedDistrict !== 'all' && supermarkets.length > 0">
        <h2 class="text-2xl font-bold mb-4">Supermarkets in {{ selectedDistrict }}</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <NuxtLink
            v-for="supermarket in supermarkets"
            :key="supermarket.id"
            :to="`/supermarkets/${supermarket.id}`"
            class="bg-white p-4 rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer block"
          >
            <h3 class="text-lg font-semibold text-gray-800">{{ supermarket.name }}</h3>
            <p class="text-sm text-gray-600 mt-2">
              <span class="font-medium">Address:</span> {{ supermarket.address }}
            </p>
            <p v-if="supermarket.phone" class="text-sm text-gray-600 mt-1">
              <span class="font-medium">Phone:</span> {{ supermarket.phone }}
            </p>
            <p v-if="supermarket.contactPerson" class="text-sm text-gray-600 mt-1">
              <span class="font-medium">Contact:</span> {{ supermarket.contactPerson }}
            </p>
            <div v-if="supermarket.operatingHoursFrom && supermarket.operatingHoursTo" class="text-sm text-gray-600 mt-1">
              <span class="font-medium">Hours:</span>
              {{ supermarket.operatingHoursFrom }} - {{ supermarket.operatingHoursTo }}
            </div>
            <span
              :class="[
                'inline-block mt-2 px-2 py-1 text-xs rounded-full',
                supermarket.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
              ]"
            >
              {{ supermarket.isActive ? 'Active' : 'Inactive' }}
            </span>
          </NuxtLink>
        </div>
      </div>

      <!-- Show all districts when "All Districts" is selected -->
      <div v-else-if="selectedDistrict === 'all' && Object.keys(supermarketsByDistrict).length > 0">
        <h2 class="text-2xl font-bold mb-6">All Supermarkets by District</h2>
        <div
          v-for="(supermarkets, district) in supermarketsByDistrict"
          :key="district"
          class="mb-8"
        >
          <h3 class="text-xl font-semibold mb-4 text-green-700 border-b-2 border-green-200 pb-2">
            {{ district }} ({{ supermarkets.length }})
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <NuxtLink
              v-for="supermarket in supermarkets"
              :key="supermarket.id"
              :to="`/supermarkets/${supermarket.id}`"
              class="bg-white p-4 rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer block"
            >
              <h4 class="text-lg font-semibold text-gray-800">{{ supermarket.name }}</h4>
              <p class="text-sm text-gray-600 mt-2">
                <span class="font-medium">Address:</span> {{ supermarket.address }}
              </p>
              <p v-if="supermarket.phone" class="text-sm text-gray-600 mt-1">
                <span class="font-medium">Phone:</span> {{ supermarket.phone }}
              </p>
              <p v-if="supermarket.contactPerson" class="text-sm text-gray-600 mt-1">
                <span class="font-medium">Contact:</span> {{ supermarket.contactPerson }}
              </p>
              <div v-if="supermarket.operatingHoursFrom && supermarket.operatingHoursTo" class="text-sm text-gray-600 mt-1">
                <span class="font-medium">Hours:</span>
                {{ supermarket.operatingHoursFrom }} - {{ supermarket.operatingHoursTo }}
              </div>
              <span
                :class="[
                  'inline-block mt-2 px-2 py-1 text-xs rounded-full',
                  supermarket.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                ]"
              >
                {{ supermarket.isActive ? 'Active' : 'Inactive' }}
              </span>
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading products...</p>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mt-8"
    >
      <p><strong>Error:</strong> {{ error }}</p>
      <button @click="fetchProducts" class="mt-2 text-sm underline">
        Retry
      </button>
    </div>

    <!-- Products Grid -->
    <div
      v-else-if="filteredProducts.length > 0"
      class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mt-8"
    >
      <ProductCard
        v-for="(p, i) in filteredProducts"
        :key="p.id || i"
        :product="p"
      />
    </div>

    <!-- Empty State -->
    <!-- <div v-else class="text-center py-12 bg-white rounded-lg shadow mt-8">
      <p class="text-gray-500 text-lg">No products available</p>
    </div> -->

    <!-- Pagination -->
    <div class="flex justify-center items-center gap-2 mt-12">
      <button class="btn btn-outline" disabled>← Previous</button>
      <button class="btn bg-green-600 text-white">1</button>
      <button class="btn">2</button>
      <button class="btn">3</button>
      <button class="btn">4</button>
      <button class="btn btn-outline">Next →</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import ProductCard from "@/components/ui/ProductCard.vue";
import ProductInventoryService from "~/services/product-inventory.service";
import SupermarketService from "~/services/supermarket.service";
import { useSupermarketStore } from "~/stores/supermarket";

// Store
const supermarketStore = useSupermarketStore();

// State
const products = ref([]);
const loading = ref(false);
const error = ref(null);
const selectedCategory = ref("all");
const districts = ref([]);
const selectedDistrict = ref("all");
const supermarkets = ref([]);

// Fetch products from API
const fetchProducts = async () => {
  loading.value = true;
  error.value = null;

  const response = await ProductInventoryService.getAllProductInventory(
    (err) => {
      console.error("Error fetching products:", err);
      error.value = err.response?.data?.message || "Failed to load products";
    }
  );

  if (response && response.data) {
    // Group products by product_master_id
    const productMap = new Map();

    for (const item of response.data) {
      const key = item.productMasterId;

      if (productMap.has(key)) {
        // Product master already exists, combine quantities
        const existingProduct = productMap.get(key);
        existingProduct.quantityAvailable += item.quantityAvailable;

        // Use the earliest expiry date
        if (item.expiryDate < existingProduct.expiryTimestamp) {
          existingProduct.expiryTimestamp = item.expiryDate;
          existingProduct.sellUntil = formatDate(item.expiryDate);
          existingProduct.expire = formatDate(item.expiryDate);
          existingProduct.sellDays = calculateDaysUntil(item.expiryDate);
          existingProduct.expireDays = calculateDaysUntil(item.expiryDate);
        }

        // Update availability
        existingProduct.availability = calculateAvailability(
          existingProduct.expiryTimestamp,
          existingProduct.quantityAvailable,
          "AVAILABLE"
        );
      } else {
        // First occurrence of this product master
        productMap.set(key, {
          id: item.id,
          emoji: "🛒",
          category: item.categoryName || item.productName,
          name: item.productName,
          location: item.supermarketName,
          sellUntil: formatDate(item.expiryDate),
          expire: formatDate(item.expiryDate),
          sellDays: calculateDaysUntil(item.expiryDate),
          expireDays: calculateDaysUntil(item.expiryDate),
          oldPrice: `${item.originalPrice.toLocaleString()}₫`,
          newPrice: `${item.sellingPrice.toLocaleString()}₫`,
          discount: calculateDiscount(item.originalPrice, item.sellingPrice),
          availability: calculateAvailability(
            item.expiryDate,
            item.quantityAvailable,
            item.status
          ),
          quantityAvailable: item.quantityAvailable,
          productMasterId: item.productMasterId,
          supermarketId: item.supermarketId,
          expiryTimestamp: item.expiryDate,
          createdAt: item.createdAt,
          updatedAt: item.updatedAt,
        });
      }
    }

    // Convert map to array
    products.value = Array.from(productMap.values());
  }
  // }
  // catch (err) {
  //   console.error("Error:", err);
  //   error.value = "Failed to load products";
  // } finally {
  //   loading.value = false;
  // }
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

// Fetch districts from API
const fetchDistricts = async () => {
  try {
    const response = await SupermarketService.getDistinctDistricts((err) => {
      console.error("Error fetching districts:", err);
    });

    if (response && response.data) {
      districts.value = response.data;
    }
  } catch (err) {
    console.error("Error fetching districts:", err);
  }
};

// Fetch all supermarkets from API
const fetchAllSupermarkets = async () => {
  try {
    const response = await SupermarketService.getActiveSupermarkets((err) => {
      console.error("Error fetching supermarkets:", err);
    });

    if (response && response.data) {
      supermarketStore.setSupermarkets(response.data);
    }
  } catch (err) {
    console.error("Error fetching supermarkets:", err);
  }
};

// Handle district change
const onDistrictChange = () => {
  if (selectedDistrict.value === "all") {
    supermarkets.value = [];
  } else {
    supermarkets.value = supermarketStore.supermarkets.filter(
      (s) => s.district === selectedDistrict.value
    );
  }
};

// Group supermarkets by district
const supermarketsByDistrict = computed(() => {
  const grouped = {};

  for (const supermarket of supermarketStore.supermarkets) {
    if (supermarket.district) {
      if (!grouped[supermarket.district]) {
        grouped[supermarket.district] = [];
      }
      grouped[supermarket.district].push(supermarket);
    }
  }

  // Sort districts alphabetically
  return Object.keys(grouped)
    .sort()
    .reduce((acc, key) => {
      acc[key] = grouped[key];
      return acc;
    }, {});
});

// Filter products by category (can be implemented later)
const filteredProducts = computed(() => {
  if (selectedCategory.value === "all") {
    return products.value;
  }
  return products.value.filter((p) =>
    p.category.toLowerCase().includes(selectedCategory.value.toLowerCase())
  );
});

// Load data on mount
onMounted(() => {
  fetchDistricts();
  fetchAllSupermarkets();
});
</script>

<style scoped>
@reference "tailwindcss";
.btn {
  @apply px-4 py-2 rounded-lg border transition;
}
.btn-outline {
  @apply border-gray-300 hover:bg-green-600 hover:text-white hover:border-green-600;
}
</style>
