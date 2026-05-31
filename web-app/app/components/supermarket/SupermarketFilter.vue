<template>
  <div class="relative mb-6 pb-4">
    <div class="flex items-center gap-3 mb-2 flex-wrap">
      <!-- Search bar -->
      <SearchBar v-model="searchQuery" placeholder="Search supermarkets..." />

      <!-- City dropdown -->
      <DropdownSelect v-model="cityId" :options="cityOptions" @change="emit('city-change')" />

      <!-- District dropdown (searchable) -->
      <SearchableDropdown
        v-model="districtId"
        :options="districtOptions"
        :disabled="cityId === 'all'"
        tooltip="Select a city first"
        @change="emit('district-change')"
      />

      <!-- Open/Closed status filter pills -->
      <StatusButtonGroup v-model="status" />

      <!-- Reset filters -->
      <button
        v-if="isFiltered"
        type="button"
        @click="reset"
        class="flex items-center gap-1 text-sm text-[#bc4747] hover:text-red-700 hover:scale-104 transition-all duration-300 cursor-pointer"
      >
        <SvgIcon name="icon-reset" class="w-3.5 h-3.5" />
        Reset Filter
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import DropdownSelect from '@/components/ui/DropdownSelect.vue'
import SearchableDropdown from '@/components/ui/SearchableDropdown.vue'
import StatusButtonGroup from '@/components/ui/StatusButtonGroup.vue'
import SearchBar from '@/components/ui/SearchBar.vue'

defineProps({
  cityOptions: {
    type: Array,
    required: true,
  },
  districtOptions: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['city-change', 'district-change'])

const cityId = defineModel('cityId')
const districtId = defineModel('districtId')
const status = defineModel('status')
const searchQuery = defineModel('searchQuery')

const isFiltered = computed(
  () =>
    cityId.value !== 'all' ||
    districtId.value !== 'all' ||
    status.value !== 'all' ||
    searchQuery.value.trim() !== ''
)

const reset = () => {
  cityId.value = 'all'
  districtId.value = 'all'
  status.value = 'all'
  searchQuery.value = ''
  emit('city-change')
}
</script>
