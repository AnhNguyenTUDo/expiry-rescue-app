<template>
  <div>
    <!-- Filters -->
    <div class="relative mb-6 pb-4">
      <div class="flex items-center justify-end gap-3 mb-2">
        <!-- District dropdown -->
        <DropdownSelect
          v-model="selectedDistrict"
          :options="districtOptions"
          @change="onDistrictChange"
        />

        <!-- Open/Closed status filter pills -->
        <StatusButtonGroup v-model="selectedStatus" />
      </div>
      <!-- Gradient Border -->
      <!-- <div class="absolute bottom-0 left-0 w-full h-[5px] bg-gradient-to-r from-green-700 to-green-500 rounded-full"></div> -->
    </div>

    <!-- Supermarkets Section - Show All Districts -->
    <div class="">
      <!-- Show single district when selected -->
      <div v-if="selectedDistrict !== 'all' && filteredSupermarkets.length > 0">
          <h3 class="text-xl font-semibold mb-2 pb-2">
            {{ selectedDistrict }} ({{ filteredSupermarkets.length }})
          </h3>        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <SupermarketCard
            v-for="supermarket in filteredSupermarkets"
            :key="supermarket.id"
            :supermarket="supermarket"
          />
        </div>
      </div>

      <!-- Single district: empty state -->
      <div
        v-else-if="selectedDistrict !== 'all' && filteredSupermarkets.length === 0 && supermarkets.length > 0"
        class="flex flex-col items-center justify-center py-16 text-center"
      >
        <div class="text-4xl mb-3">
          <svg xmlns="http://www.w3.org/2000/svg" width="2.5em" height="2.5em" viewBox="0 0 24 24">
            <path d="M0 0h24v24H0z" fill="none" />
            <path fill="#5ce259" d="M18 4H2V2h16zm-4.96 6h-10l.6-3h12.72l.4 2c.74.04 1.45.19 2.13.46L18 5H2l-1 5v2h1v6h8.5c-.33-.8-.5-1.65-.5-2.5v.5H4v-4h6v3.5c0-1.66.64-3.33 1.9-4.6q.54-.525 1.14-.9m10.35 11L22 22.39l-3.12-3.07c-.69.43-1.51.68-2.38.68c-2.5 0-4.5-2-4.5-4.5s2-4.5 4.5-4.5s4.5 2 4.5 4.5c0 .88-.25 1.71-.69 2.4zM19 15.5a2.5 2.5 0 0 0-5 0a2.5 2.5 0 0 0 5 0" />
          </svg>
        </div>
        <p class="text-gray-700 font-medium">
          No <span class="font-semibold">{{ selectedStatus }}</span> supermarkets in
          <span class="font-semibold">{{ selectedDistrict }}</span>.
        </p>
        <p class="text-sm text-gray-500 mt-1">Try switching to <strong>All</strong> to see all stores.</p>
      </div>

      <div v-else-if="selectedDistrict === 'all' && Object.keys(supermarketsByDistrict).length > 0">
        <!-- <h2 class="text-2xl font-bold mb-6">All Supermarkets by District</h2> -->
        <div
          v-for="(supermarkets, district, index) in supermarketsByDistrict"
          :key="district"
          class="mb-8"
        >
          <hr v-if="index > 0" class="w-60 border-t-8 border-dotted border-gray-300 mx-auto mb-7 mt-2" />
          <h3 class="text-xl font-semibold mb-2 pb-2">
            {{ district }} ({{ supermarkets.length }})
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <SupermarketCard
              v-for="supermarket in supermarkets"
              :key="supermarket.id"
              :supermarket="supermarket"
            />
          </div>
        </div>
      </div>

      <!-- All districts: empty state (status filter has no matches) -->
      <div
        v-else-if="selectedDistrict === 'all' && selectedStatus !== 'all' && supermarketStore.supermarkets.length > 0"
        class="flex flex-col items-center justify-center py-16 text-center"
      >
        <div class="text-4xl mb-3">
          <svg xmlns="http://www.w3.org/2000/svg" width="2.5em" height="2.5em" viewBox="0 0 24 24">
            <path d="M0 0h24v24H0z" fill="none" />
            <path fill="#5ce259" d="M18 4H2V2h16zm-4.96 6h-10l.6-3h12.72l.4 2c.74.04 1.45.19 2.13.46L18 5H2l-1 5v2h1v6h8.5c-.33-.8-.5-1.65-.5-2.5v.5H4v-4h6v3.5c0-1.66.64-3.33 1.9-4.6q.54-.525 1.14-.9m10.35 11L22 22.39l-3.12-3.07c-.69.43-1.51.68-2.38.68c-2.5 0-4.5-2-4.5-4.5s2-4.5 4.5-4.5s4.5 2 4.5 4.5c0 .88-.25 1.71-.69 2.4zM19 15.5a2.5 2.5 0 0 0-5 0a2.5 2.5 0 0 0 5 0" />
          </svg>
        </div>
        <p class="text-gray-700 font-medium">
          No <span class="font-semibold">{{ selectedStatus }}</span> supermarkets right now.
        </p>
        <p class="text-sm text-gray-500 mt-1">Try switching to <strong>All</strong> to see all stores.</p>
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
    <!-- <div class="flex justify-center items-center gap-2 mt-12">
      <button class="btn btn-outline" disabled>← Previous</button>
      <button class="btn bg-green-600 text-white">1</button>
      <button class="btn">2</button>
      <button class="btn">3</button>
      <button class="btn">4</button>
      <button class="btn btn-outline">Next →</button>
    </div> -->
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import ProductCard from "@/components/ui/ProductCard.vue";
import SupermarketCard from "@/components/ui/SupermarketCard.vue";
import DropdownSelect from "@/components/ui/DropdownSelect.vue";
import StatusButtonGroup from "@/components/ui/StatusButtonGroup.vue";
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
const selectedStatus = ref("all"); // 'all' | 'open' | 'closed'

// Options for the district dropdown
const districtOptions = computed(() => [
  { value: "all", label: "All Districts" },
  ...districts.value.map((d) => ({ value: d, label: d })),
]);

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

const isOpen = (from, to) => {
  if (!from || !to) return false;
  const now = new Date();
  const currentMinutes = now.getHours() * 60 + now.getMinutes();
  const [fh, fm] = from.split(":").map(Number);
  const [th, tm] = to.split(":").map(Number);
  const fromMinutes = fh * 60 + fm;
  const toMinutes = th * 60 + tm;
  // Handle overnight ranges (e.g. 22:00 - 02:00)
  if (fromMinutes <= toMinutes) {
    return currentMinutes >= fromMinutes && currentMinutes < toMinutes;
  } else {
    return currentMinutes >= fromMinutes || currentMinutes < toMinutes;
  }
};

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

// Group supermarkets by district, filtered by status
const supermarketsByDistrict = computed(() => {
  const grouped = {};

  for (const supermarket of supermarketStore.supermarkets) {
    if (!supermarket.district) continue;

    // Apply status filter
    if (selectedStatus.value === 'open' && !isOpen(supermarket.operatingHoursFrom, supermarket.operatingHoursTo)) continue;
    if (selectedStatus.value === 'closed' && isOpen(supermarket.operatingHoursFrom, supermarket.operatingHoursTo)) continue;

    if (!grouped[supermarket.district]) {
      grouped[supermarket.district] = [];
    }
    grouped[supermarket.district].push(supermarket);
  }

  // Sort districts alphabetically
  return Object.keys(grouped)
    .sort()
    .reduce((acc, key) => {
      acc[key] = grouped[key];
      return acc;
    }, {});
});

// Supermarkets for the single-district view, filtered by status
const filteredSupermarkets = computed(() => {
  if (selectedStatus.value === 'open') {
    return supermarkets.value.filter((s) => isOpen(s.operatingHoursFrom, s.operatingHoursTo));
  }
  if (selectedStatus.value === 'closed') {
    return supermarkets.value.filter((s) => !isOpen(s.operatingHoursFrom, s.operatingHoursTo));
  }
  return supermarkets.value;
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
