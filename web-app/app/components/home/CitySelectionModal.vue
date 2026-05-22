<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center">
    <div class="absolute inset-0 bg-gray-900/50"></div>

    <div class="relative p-4 w-full max-w-lg max-h-[90vh] flex flex-col">
      <div class="relative bg-white border border-gray-200 rounded-lg shadow-sm p-4 md:p-6 flex flex-col min-h-0">

        <!-- Header -->
        <div class="border-b border-gray-200 pb-4 mb-4 shrink-0">
          <h3 class="text-lg font-semibold text-gray-900">Welcome to Expiry Rescue</h3>
          <p class="text-sm text-gray-500 mt-1">Please select the area you'd like to browse stores in.</p>
        </div>

        <!-- Search bar -->
        <div class="mb-3 shrink-0">
          <SearchBar v-model="searchQuery" placeholder="Search city or district..." />
        </div>

        <!-- City + district list -->
        <div v-if="loading" class="h-64 flex items-center justify-center text-gray-500 text-sm">Loading...</div>
        <div v-else class="overflow-y-auto space-y-1 h-64">
          <template v-for="city in filteredCities" :key="city.id">
            <!-- City row (toggle) -->
            <button
              type="button"
              @click="toggle(city.id)"
              class="w-full flex items-center justify-between px-3 py-2.5 rounded-lg hover:bg-gray-50 transition-colors duration-150 cursor-pointer"
            >
              <span class="font-medium text-gray-800">{{ city.name }}</span>
              <svg
                class="w-4 h-4 text-gray-400 transition-transform duration-200"
                :class="isExpanded(city.id) ? 'rotate-180' : ''"
                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
              >
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m19 9-7 7-7-7"/>
              </svg>
            </button>

            <!-- Districts -->
            <div v-show="isExpanded(city.id)" class="px-3 pb-3 pt-1 flex flex-wrap gap-2">
              <button
                v-if="!searchQuery.trim()"
                type="button"
                @click="select(city, 'all', 'All Districts')"
                :class="isSelected(city.id, 'all')
                  ? 'border-green-500 bg-green-50 text-green-700 font-medium'
                  : 'border-gray-300 text-gray-700 hover:border-green-500 hover:bg-green-50'"
                class="px-3 py-1.5 rounded-[10px] border text-sm transition-colors duration-150 cursor-pointer"
              >
                All Districts
              </button>
              <button
                v-for="district in city.filteredDistricts"
                :key="district.id"
                type="button"
                @click="select(city, district.id, district.name)"
                :class="isSelected(city.id, district.id)
                  ? 'border-green-500 bg-green-50 text-green-700 font-medium'
                  : 'border-gray-300 text-gray-700 hover:border-green-500 hover:bg-green-50'"
                class="px-3 py-1.5 rounded-[10px] border text-sm transition-colors duration-150 cursor-pointer"
              >
                {{ district.name }}
              </button>
            </div>
          </template>
          <p v-if="filteredCities.length === 0" class="text-center text-gray-400 text-sm py-6">No results found.</p>
        </div>

        <!-- Footer -->
        <div class="flex justify-end border-t border-gray-200 pt-4 mt-4 shrink-0">
          <button
            type="button"
            :disabled="!selection"
            @click="confirm"
            class="px-4 py-2 rounded-[10px] text-sm font-medium text-white transition-colors duration-150"
            :class="selection
              ? 'bg-green-600 hover:bg-green-700 cursor-pointer'
              : 'bg-gray-300 cursor-not-allowed'"
          >
            Confirm
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<!--
PREVIOUS IMPLEMENTATION — expand/collapse per city (no search bar)

<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center">
    <div class="absolute inset-0 bg-gray-900/50"></div>
    <div class="relative p-4 w-full max-w-lg max-h-[90vh] flex flex-col">
      <div class="relative bg-white border border-gray-200 rounded-lg shadow-sm p-4 md:p-6 flex flex-col min-h-0">
        <div class="border-b border-gray-200 pb-4 mb-4 shrink-0">
          <h3 class="text-lg font-semibold text-gray-900">Welcome to Expiry Rescue</h3>
          <p class="text-sm text-gray-500 mt-1">Please select the area you'd like to browse stores in.</p>
        </div>
        <div v-if="loading" class="text-center py-8 text-gray-500 text-sm">Loading...</div>
        <div v-else class="overflow-y-auto space-y-1 flex-1 min-h-0">
          <div v-for="city in citiesWithDistricts" :key="city.id">
            <button type="button" @click="toggle(city.id)"
              class="w-full flex items-center justify-between px-3 py-2.5 rounded-lg hover:bg-gray-50 transition-colors duration-150 cursor-pointer">
              <span class="font-medium text-gray-800">{{ city.name }}</span>
              <svg class="w-4 h-4 text-gray-400 transition-transform duration-200"
                :class="expanded === city.id ? 'rotate-180' : ''"
                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m19 9-7 7-7-7"/>
              </svg>
            </button>
            <div v-show="expanded === city.id" class="px-3 pb-3 pt-1 flex flex-wrap gap-2">
              <button type="button" @click="select(city, 'all', 'All Districts')"
                :class="isSelected(city.id, 'all') ? 'border-green-500 bg-green-50 text-green-700 font-medium' : 'border-gray-300 text-gray-700 hover:border-green-500 hover:bg-green-50'"
                class="px-3 py-1.5 rounded-[10px] border text-sm transition-colors duration-150 cursor-pointer">
                All Districts
              </button>
              <button v-for="district in city.districts" :key="district.id" type="button"
                @click="select(city, district.id, district.name)"
                :class="isSelected(city.id, district.id) ? 'border-green-500 bg-green-50 text-green-700 font-medium' : 'border-gray-300 text-gray-700 hover:border-green-500 hover:bg-green-50'"
                class="px-3 py-1.5 rounded-[10px] border text-sm transition-colors duration-150 cursor-pointer">
                {{ district.name }}
              </button>
            </div>
          </div>
        </div>
        <div class="flex justify-end border-t border-gray-200 pt-4 mt-4 shrink-0">
          <button type="button" :disabled="!selection" @click="confirm"
            class="px-4 py-2 rounded-[10px] text-sm font-medium text-white transition-colors duration-150"
            :class="selection ? 'bg-green-600 hover:bg-green-700 cursor-pointer' : 'bg-gray-300 cursor-not-allowed'">
            Confirm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
-->

<script setup>
import { ref, computed, watch } from "vue";
import CityService from "@/services/city.service";
import SearchBar from "@/components/ui/SearchBar.vue";

const props = defineProps({
  show: { type: Boolean, required: true },
  cities: { type: Array, required: true },
});

const emit = defineEmits(["confirm"]);

const loading = ref(false);
const citiesWithDistricts = ref([]);
const searchQuery = ref("");
const selection = ref(null); // { cityId, cityName, districtId, districtName }
const expanded = ref(null);

const toggle = (cityId) => {
  expanded.value = expanded.value === cityId ? null : cityId;
};

const isExpanded = (cityId) => {
  return searchQuery.value.trim() ? true : expanded.value === cityId;
};

watch(searchQuery, (q) => {
  if (!q.trim()) expanded.value = null;
});

const loadAll = async () => {
  if (!props.cities.length) return;
  loading.value = true;
  const results = await Promise.all(
    props.cities.map(async (city) => {
      const response = await CityService.getDistrictsByCity(city.id, (err) => {
        console.error("Error fetching districts for", city.name, err);
      });
      return { ...city, districts: response?.data ?? [] };
    })
  );
  citiesWithDistricts.value = results;
  loading.value = false;
};

watch(
  () => props.show,
  (visible) => {
    if (visible && !citiesWithDistricts.value.length) loadAll();
  }
);

const filteredCities = computed(() => {
  const q = searchQuery.value.trim().toLowerCase();
  if (!q) return citiesWithDistricts.value.map((c) => ({ ...c, filteredDistricts: c.districts }));
  return citiesWithDistricts.value
    .map((city) => {
      const cityMatches = city.name.toLowerCase().includes(q);
      const filteredDistricts = city.districts.filter((d) => d.name.toLowerCase().includes(q));
      if (!cityMatches && filteredDistricts.length === 0) return null;
      return { ...city, filteredDistricts: cityMatches ? city.districts : filteredDistricts };
    })
    .filter(Boolean);
});

const select = (city, districtId, districtName) => {
  selection.value = { cityId: city.id, cityName: city.name, districtId, districtName };
};

const isSelected = (cityId, districtId) =>
  selection.value?.cityId === cityId && selection.value?.districtId === districtId;

const confirm = () => {
  if (!selection.value) return;
  emit("confirm", selection.value);
};
</script>
