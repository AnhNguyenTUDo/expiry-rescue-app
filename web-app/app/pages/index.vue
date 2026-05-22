<template>
  <div>
    <!-- Filters -->
    <div class="relative mb-6 pb-4">
      <div class="flex items-center justify-end gap-3 mb-2 flex-wrap">
        <!-- City dropdown -->
        <DropdownSelect
          v-model="selectedCityId"
          :options="cityOptions"
          @change="onCityChange"
        />

        <!-- District dropdown (depends on city) -->
        <DropdownSelect
          v-model="selectedDistrictId"
          :options="districtOptions"
          @change="onDistrictChange"
        />

        <!-- Open/Closed status filter pills -->
        <StatusButtonGroup v-model="selectedStatus" />
      </div>
    </div>

    <!-- Supermarkets Section -->
    <div>
      <!-- Single district: empty state -->
      <div
        v-if="selectedDistrictId !== 'all' && supermarketStore.supermarkets.length > 0 && districtSections.length === 0"
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
          <span class="font-semibold">{{ currentDistrictLabel }}</span>.
        </p>
        <p class="text-sm text-gray-500 mt-1">Try switching to <strong>All</strong> to see all stores.</p>
      </div>

      <!-- All districts: empty state (status filter has no matches) -->
      <div
        v-else-if="selectedDistrictId === 'all' && selectedStatus !== 'all' && supermarketStore.supermarkets.length > 0 && districtSections.length === 0"
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

      <!-- Unified district loop -->
      <template v-for="section in districtSections" :key="section.key">
        <!-- City heading (only in all-cities view) -->
        <h2
          v-if="section.type === 'city-heading'"
          class="text-2xl font-bold mb-4 pb-2 border-b-2 border-emerald-500 inline-block"
          :class="section.isFirst ? '' : 'mt-8'"
        >
          {{ section.label }}
        </h2>

        <!-- District block -->
        <div v-else class="mb-8">
          <hr v-if="section.showDivider" class="w-60 border-t-8 border-dotted border-gray-300 mx-auto mb-7 mt-2" />
          <h3 class="text-xl font-semibold mb-2 pb-2">
            {{ section.districtName }} ({{ section.stores.length }})
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            <SupermarketCard
              v-for="supermarket in section.stores.slice(0, getVisibleCount(section.paginationKey))"
              :key="supermarket.id"
              :supermarket="supermarket"
            />
          </div>
          <div
            v-if="section.stores.length > getVisibleCount(section.paginationKey)"
            class="mt-6 text-center"
          >
            <button @click="showMore(section.paginationKey)" class="show-more-btn">
              Show more
              <span class="text-sm opacity-70">
                ({{ section.stores.length - getVisibleCount(section.paginationKey) }} more)
              </span>
            </button>
          </div>
        </div>
      </template>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading...</p>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mt-8"
    >
      <p><strong>Error:</strong> {{ error }}</p>
      <button @click="loadSupermarkets" class="mt-2 text-sm underline">
        Retry
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import SupermarketCard from "@/components/ui/SupermarketCard.vue";
import DropdownSelect from "@/components/ui/DropdownSelect.vue";
import StatusButtonGroup from "@/components/ui/StatusButtonGroup.vue";
import SupermarketService from "~/services/supermarket.service";
import CityService from "~/services/city.service";
import { useSupermarketStore } from "~/stores/supermarket";

const supermarketStore = useSupermarketStore();

// Filter state
const cities = ref([]);
const districts = ref([]);
const selectedCityId = ref("all");
const selectedDistrictId = ref("all");
const selectedStatus = ref("all"); // 'all' | 'open' | 'closed'

const loading = ref(false);
const error = ref(null);

// Pagination
const PAGE_SIZE = 6;
const districtVisibleCount = ref({});
const getVisibleCount = (key) => districtVisibleCount.value[key] ?? PAGE_SIZE;
const showMore = (key) => {
  districtVisibleCount.value[key] = getVisibleCount(key) + PAGE_SIZE;
};
const resetPagination = () => {
  districtVisibleCount.value = {};
};

// Dropdown options
const cityOptions = computed(() => [
  { value: "all", label: "All Cities" },
  ...cities.value.map((c) => ({ value: c.id, label: c.name })),
]);

const districtOptions = computed(() => {
  if (selectedCityId.value === "all") {
    return [{ value: "all", label: "All Districts" }];
  }
  return [
    { value: "all", label: "All Districts" },
    ...districts.value.map((d) => ({ value: d.id, label: d.name })),
  ];
});

const currentDistrictLabel = computed(() => {
  const d = districts.value.find((d) => d.id === selectedDistrictId.value);
  return d ? d.name : "";
});

// Helpers
const isOpen = (from, to) => {
  if (!from || !to) return false;
  const now = new Date();
  const currentMinutes = now.getHours() * 60 + now.getMinutes();
  const [fh, fm] = from.split(":").map(Number);
  const [th, tm] = to.split(":").map(Number);
  const fromMinutes = fh * 60 + fm;
  const toMinutes = th * 60 + tm;
  if (fromMinutes <= toMinutes) {
    return currentMinutes >= fromMinutes && currentMinutes < toMinutes;
  }
  return currentMinutes >= fromMinutes || currentMinutes < toMinutes;
};

const passesStatusFilter = (supermarket) => {
  if (selectedStatus.value === "all") return true;
  const open = isOpen(supermarket.operatingHoursFrom, supermarket.operatingHoursTo);
  return selectedStatus.value === "open" ? open : !open;
};

// Data loaders
const loadCities = async () => {
  const response = await CityService.getAllCities((err) => {
    console.error("Error fetching cities:", err);
  });
  if (response && response.data) cities.value = response.data;
};

const loadDistricts = async (cityId) => {
  if (!cityId || cityId === "all") {
    districts.value = [];
    return;
  }
  const response = await CityService.getDistrictsByCity(cityId, (err) => {
    console.error("Error fetching districts:", err);
  });
  if (response && response.data) districts.value = response.data;
};

const loadSupermarkets = async () => {
  loading.value = true;
  error.value = null;
  const filters = {};
  if (selectedCityId.value !== "all") filters.cityId = selectedCityId.value;
  if (selectedDistrictId.value !== "all") filters.districtId = selectedDistrictId.value;

  const response = await SupermarketService.getActiveSupermarkets(filters, (err) => {
    console.error("Error fetching supermarkets:", err);
    error.value = err.response?.data?.message || "Failed to load supermarkets";
  });
  if (response && response.data) supermarketStore.setSupermarkets(response.data);
  loading.value = false;
};

// Filter handlers
const onCityChange = async () => {
  selectedDistrictId.value = "all";
  resetPagination();
  await loadDistricts(selectedCityId.value);
  await loadSupermarkets();
};

const onDistrictChange = async () => {
  resetPagination();
  await loadSupermarkets();
};

watch(selectedStatus, () => {
  resetPagination();
});

// Grouping for views
const visibleSupermarkets = computed(() =>
  supermarketStore.supermarkets.filter(passesStatusFilter)
);

// Flat normalized list of sections for the unified render loop.
// Each item is one of:
//   { type: 'city-heading', key, label, isFirst }
//   { type: 'district',    key, districtName, stores, paginationKey, showDivider }
const districtSections = computed(() => {
  const sections = [];

  // Case 1: a specific district is selected
  if (selectedDistrictId.value !== "all") {
    const stores = visibleSupermarkets.value.filter(
      (s) => s.districtId === selectedDistrictId.value
    );
    if (stores.length > 0) {
      sections.push({
        type: "district",
        key: currentDistrictLabel.value,
        districtName: currentDistrictLabel.value,
        stores,
        paginationKey: currentDistrictLabel.value,
        showDivider: false,
      });
    }
    return sections;
  }

  // Case 2: a city is selected — group by district
  if (selectedCityId.value !== "all") {
    const grouped = {};
    for (const s of visibleSupermarkets.value) {
      const key = s.districtName || "Unknown";
      if (!grouped[key]) grouped[key] = [];
      grouped[key].push(s);
    }
    const districtNames = Object.keys(grouped).sort();
    districtNames.forEach((districtName, idx) => {
      sections.push({
        type: "district",
        key: districtName,
        districtName,
        stores: grouped[districtName],
        paginationKey: districtName,
        showDivider: idx > 0,
      });
    });
    return sections;
  }

  // Case 3: all cities — group by city then district
  const grouped = {};
  for (const s of visibleSupermarkets.value) {
    const city = s.cityName || "Unknown";
    const district = s.districtName || "Unknown";
    if (!grouped[city]) grouped[city] = {};
    if (!grouped[city][district]) grouped[city][district] = [];
    grouped[city][district].push(s);
  }
  const cityNames = Object.keys(grouped).sort();
  cityNames.forEach((cityName, cityIdx) => {
    sections.push({
      type: "city-heading",
      key: `city:${cityName}`,
      label: cityName,
      isFirst: cityIdx === 0,
    });
    const districtNames = Object.keys(grouped[cityName]).sort();
    districtNames.forEach((districtName, dIdx) => {
      sections.push({
        type: "district",
        key: `${cityName}:${districtName}`,
        districtName,
        stores: grouped[cityName][districtName],
        paginationKey: `${cityName}:${districtName}`,
        showDivider: dIdx > 0,
      });
    });
  });
  return sections;
});

onMounted(async () => {
  await loadCities();
  await loadSupermarkets();
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
.show-more-btn {
  @apply px-6 py-2 rounded-full border-2 border-emerald-500 text-emerald-600 font-medium
    transition-all duration-200 hover:bg-emerald-500 hover:text-white hover:shadow-md
    active:scale-95;
}
</style>
