<template>
  <div>
    <!-- City selection modal (first visit) -->
    <CitySelectionModal
      :show="showCityModal"
      :cities="cities"
      @confirm="onLocationConfirmed"
    />

    <!-- Filters -->
    <HomeFilter
      v-model:cityId="selectedCityId"
      v-model:districtId="selectedDistrictId"
      v-model:status="selectedStatus"
      v-model:searchQuery="searchQuery"
      :city-options="cityOptions"
      :district-options="districtOptions"
      @city-change="onCityChange"
      @district-change="onDistrictChange"
    />

    <!-- Supermarkets Section -->
    <SupermarketSection
      :district-sections="districtSections"
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
import { ref, onMounted, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import HomeFilter from "@/components/home/HomeFilter.vue";
import SupermarketSection from "@/components/supermarket/SupermarketSection.vue";
import CitySelectionModal from "@/components/home/CitySelectionModal.vue";
import SupermarketService from "~/services/supermarket.service";
import CityService from "~/services/city.service";
import { useSupermarketStore } from "~/stores/supermarket";

const route = useRoute();
const router = useRouter();

const LOCATION_KEY = "expiry_rescue_location";

const supermarketStore = useSupermarketStore();

const showCityModal = ref(false);

// Filter state
const cities = ref([]);
const districts = ref([]);
const selectedCityId = ref("all");
const selectedDistrictId = ref("all");
const selectedStatus = ref("all"); // 'all' | 'open' | 'closed'
const searchQuery = ref("");

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

// All filters combined — used for store lists within each district
const visibleSupermarkets = computed(() => {
  const q = searchQuery.value.trim().toLowerCase();
  return supermarketStore.supermarkets.filter(
    (s) => passesStatusFilter(s) && (!q || s.name.toLowerCase().includes(q))
  );
});

// Flat normalized list of sections for the unified render loop.
// Each item is one of:
//   { type: 'city-heading', key, label, isFirst }
//   { type: 'district',    key, districtName, stores, showDivider }
const districtSections = computed(() => {
  const sections = [];

  // Case 1: a specific district is selected
  if (selectedDistrictId.value !== "all") {
    const cityName = cities.value.find((c) => c.id === selectedCityId.value)?.name || "";
    if (cityName) {
      sections.push({ type: "city-heading", key: `city:${cityName}`, label: cityName, isFirst: true });
    }
    sections.push({
      type: "district",
      key: currentDistrictLabel.value,
      districtName: currentDistrictLabel.value,
      stores: visibleSupermarkets.value.filter((s) => s.districtId === selectedDistrictId.value),
      showDivider: false,
    });
    return sections;
  }

  // Case 2: a city is selected — show city heading then group by district
  if (selectedCityId.value !== "all") {
    const cityName = cities.value.find((c) => c.id === selectedCityId.value)?.name || "";
    if (cityName) {
      sections.push({ type: "city-heading", key: `city:${cityName}`, label: cityName, isFirst: true });
    }
    // Use raw supermarkets for district keys so districts persist through all filters
    const districtKeys = [...new Set(supermarketStore.supermarkets.map((s) => s.districtName || "Unknown"))].sort();
    const grouped = {};
    for (const s of visibleSupermarkets.value) {
      const key = s.districtName || "Unknown";
      if (!grouped[key]) grouped[key] = [];
      grouped[key].push(s);
    }
    districtKeys.forEach((districtName, idx) => {
      sections.push({
        type: "district",
        key: districtName,
        districtName,
        stores: grouped[districtName] ?? [],
        showDivider: idx > 0,
      });
    });
    return sections;
  }

  // Case 3: all cities — use raw supermarkets for structure, visibleSupermarkets for store lists
  const allGrouped = {};
  for (const s of supermarketStore.supermarkets) {
    const city = s.cityName || "Unknown";
    const district = s.districtName || "Unknown";
    if (!allGrouped[city]) allGrouped[city] = new Set();
    allGrouped[city].add(district);
  }
  const visibleGrouped = {};
  for (const s of visibleSupermarkets.value) {
    const city = s.cityName || "Unknown";
    const district = s.districtName || "Unknown";
    if (!visibleGrouped[city]) visibleGrouped[city] = {};
    if (!visibleGrouped[city][district]) visibleGrouped[city][district] = [];
    visibleGrouped[city][district].push(s);
  }
  const cityNames = Object.keys(allGrouped).sort();
  cityNames.forEach((cityName, cityIdx) => {
    sections.push({
      type: "city-heading",
      key: `city:${cityName}`,
      label: cityName,
      isFirst: cityIdx === 0,
    });
    const districtNames = [...allGrouped[cityName]].sort();
    districtNames.forEach((districtName, dIdx) => {
      sections.push({
        type: "district",
        key: `${cityName}:${districtName}`,
        districtName,
        stores: visibleGrouped[cityName]?.[districtName] ?? [],
        showDivider: dIdx > 0,
      });
    });
  });
  return sections;
});

// Sync filters → URL (silent replace, no new history entry)
const syncUrl = () => {
  const query = {};
  if (selectedCityId.value !== "all") query.city = selectedCityId.value;
  if (selectedDistrictId.value !== "all") query.district = selectedDistrictId.value;
  if (selectedStatus.value !== "all") query.status = selectedStatus.value;
  if (searchQuery.value.trim()) query.search = searchQuery.value.trim();
  router.replace({ query });
};

watch([selectedCityId, selectedDistrictId, selectedStatus, searchQuery], syncUrl);

// Apply URL params to filter state and load data
const applyUrlParams = async (query) => {
  const cityId = query.city || "all";
  const districtId = query.district || "all";
  const status = query.status || "all";
  const search = query.search || "";

  selectedCityId.value = cityId;
  selectedDistrictId.value = districtId;
  selectedStatus.value = status;
  searchQuery.value = search;

  if (cityId !== "all") await loadDistricts(cityId);
  await loadSupermarkets();
};

// Reload when user edits the URL directly in the browser
watch(() => route.query, (query) => {
  applyUrlParams(query);
}, { deep: true });

const onLocationConfirmed = async ({ cityId, cityName, districtId, districtName }) => {
  localStorage.setItem(LOCATION_KEY, JSON.stringify({ cityId, cityName, districtId, districtName }));
  showCityModal.value = false;
  selectedCityId.value = cityId;
  selectedDistrictId.value = districtId;
  await loadDistricts(cityId);
  await loadSupermarkets();
};

onMounted(async () => {
  await loadCities();

  // URL params take priority over localStorage
  const hasUrlParams = route.query.city || route.query.district || route.query.status || route.query.search;
  if (hasUrlParams) {
    await applyUrlParams(route.query);
    return;
  }

  const saved = localStorage.getItem(LOCATION_KEY);
  if (saved) {
    const { cityId, districtId } = JSON.parse(saved);
    selectedCityId.value = cityId;
    selectedDistrictId.value = districtId;
    await loadDistricts(cityId);
    await loadSupermarkets();
  } else {
    showCityModal.value = true;
  }
});
</script>
