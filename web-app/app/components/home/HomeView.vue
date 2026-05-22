<template>
  <div>
    <!-- Filters -->
    <HomeFilter
      v-model:cityId="selectedCityId"
      v-model:districtId="selectedDistrictId"
      v-model:status="selectedStatus"
      :city-options="cityOptions"
      :district-options="districtOptions"
      @city-change="onCityChange"
      @district-change="onDistrictChange"
    />

    <!-- Supermarkets Section -->
    <SupermarketSection
      :district-sections="districtSections"
      :selected-district-id="selectedDistrictId"
      :selected-status="selectedStatus"
      :current-district-label="currentDistrictLabel"
      :has-loaded-data="supermarketStore.supermarkets.length > 0"
    />

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
import { ref, onMounted, computed } from "vue";
import HomeFilter from "@/components/home/HomeFilter.vue";
import SupermarketSection from "@/components/supermarket/SupermarketSection.vue";
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
  await loadDistricts(selectedCityId.value);
  await loadSupermarkets();
};

const onDistrictChange = async () => {
  await loadSupermarkets();
};

// Grouping for views
const visibleSupermarkets = computed(() =>
  supermarketStore.supermarkets.filter(passesStatusFilter)
);

// Flat normalized list of sections for the unified render loop.
// Each item is one of:
//   { type: 'city-heading', key, label, isFirst }
//   { type: 'district',    key, districtName, stores, showDivider }
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
