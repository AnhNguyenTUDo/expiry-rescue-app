<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center">
    <div class="absolute inset-0 bg-gray-900/50"></div>

    <div class="relative flex max-h-[90vh] w-full max-w-lg flex-col p-4">
      <div
        class="relative flex min-h-0 flex-col rounded-lg border border-gray-200 bg-white p-4 shadow-sm md:p-6"
      >
        <!-- Header -->
        <div class="mb-4 shrink-0 border-b border-gray-200 pb-4">
          <h3 class="text-lg font-semibold text-gray-900">Welcome to Expiry Rescue</h3>
          <p class="mt-1 text-sm text-gray-500">
            Please select the area you'd like to browse stores in.
          </p>
        </div>

        <!-- Search bar -->
        <div class="mb-3 shrink-0">
          <SearchBar v-model="searchQuery" placeholder="Search city or district..." />
        </div>

        <!-- City + district list -->
        <div v-if="loading" class="flex h-64 items-center justify-center text-sm text-gray-500">
          Loading...
        </div>
        <div v-else class="h-64 space-y-1 overflow-y-auto">
          <template v-for="city in filteredCities" :key="city.id">
            <!-- City row (toggle) -->
            <button
              type="button"
              class="flex w-full cursor-pointer items-center justify-between rounded-lg px-3 py-2.5 transition-colors duration-150 hover:bg-gray-50"
              @click="toggle(city.id)"
            >
              <span class="font-medium text-gray-800">{{ city.name }}</span>
              <SvgIcon
                name="icon-chevron-down"
                class="h-3 w-3 text-gray-500 transition-transform duration-200"
                :class="isExpanded(city.id) ? 'rotate-180' : ''"
              />
            </button>

            <!-- Districts -->
            <div v-show="isExpanded(city.id)" class="flex flex-wrap gap-2 px-3 pt-1 pb-3">
              <button
                v-if="!searchQuery.trim()"
                type="button"
                :class="
                  isSelected(city.id, 'all')
                    ? 'border-green-500 bg-green-50 font-medium text-green-700'
                    : 'border-gray-300 text-gray-700 hover:border-green-500 hover:bg-green-50'
                "
                class="cursor-pointer rounded-[10px] border px-3 py-1.5 text-sm transition-colors duration-150"
                @click="select(city, 'all', 'All Districts')"
              >
                All Districts
              </button>
              <button
                v-for="district in city.filteredDistricts"
                :key="district.id"
                type="button"
                :class="
                  isSelected(city.id, district.id)
                    ? 'border-green-500 bg-green-50 font-medium text-green-700'
                    : 'border-gray-300 text-gray-700 hover:border-green-500 hover:bg-green-50'
                "
                class="cursor-pointer rounded-[10px] border px-3 py-1.5 text-sm transition-colors duration-150"
                @click="select(city, district.id, district.name)"
              >
                {{ district.name }}
              </button>
            </div>
          </template>
          <p v-if="filteredCities.length === 0" class="py-6 text-center text-sm text-gray-400">
            No results found.
          </p>
        </div>

        <!-- Footer -->
        <div class="mt-4 flex shrink-0 justify-end border-t border-gray-200 pt-4">
          <button
            type="button"
            :disabled="!selection"
            class="rounded-[10px] px-4 py-2 text-sm font-medium text-white transition-colors duration-150"
            :class="
              selection
                ? 'cursor-pointer bg-green-600 hover:bg-green-700'
                : 'cursor-not-allowed bg-gray-300'
            "
            @click="confirm"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import SearchBar from '@/components/ui/SearchBar.vue'
import CityService from '@/services/city.service'

const props = defineProps({
  show: { type: Boolean, required: true },
  cities: { type: Array, required: true },
})

const emit = defineEmits(['confirm'])

const loading = ref(false)
const citiesWithDistricts = ref([])
const searchQuery = ref('')
const selection = ref(null) // { cityId, cityName, districtId, districtName }
const expanded = ref(null)

const toggle = (cityId) => {
  expanded.value = expanded.value === cityId ? null : cityId
}

const isExpanded = (cityId) => {
  return searchQuery.value.trim() ? true : expanded.value === cityId
}

watch(searchQuery, (q) => {
  if (!q.trim()) expanded.value = null
})

const loadAll = async () => {
  if (!props.cities.length) return
  loading.value = true
  const results = await Promise.all(
    props.cities.map(async (city) => {
      const response = await CityService.getDistrictsByCity(city.id, (err) => {
        console.error('Error fetching districts for', city.name, err)
      })
      return { ...city, districts: response?.data ?? [] }
    })
  )
  citiesWithDistricts.value = results
  loading.value = false
}

watch(
  () => props.show,
  (visible) => {
    if (visible && !citiesWithDistricts.value.length) loadAll()
  }
)

const filteredCities = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return citiesWithDistricts.value.map((c) => ({ ...c, filteredDistricts: c.districts }))
  return citiesWithDistricts.value
    .map((city) => {
      const cityMatches = city.name.toLowerCase().includes(q)
      const filteredDistricts = city.districts.filter((d) => d.name.toLowerCase().includes(q))
      if (!cityMatches && filteredDistricts.length === 0) return null
      return { ...city, filteredDistricts: cityMatches ? city.districts : filteredDistricts }
    })
    .filter(Boolean)
})

const select = (city, districtId, districtName) => {
  selection.value = { cityId: city.id, cityName: city.name, districtId, districtName }
}

const isSelected = (cityId, districtId) =>
  selection.value?.cityId === cityId && selection.value?.districtId === districtId

const confirm = () => {
  if (!selection.value) return
  emit('confirm', selection.value)
}
</script>
