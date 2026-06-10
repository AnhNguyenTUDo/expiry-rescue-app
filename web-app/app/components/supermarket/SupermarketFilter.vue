<template>
  <div class="relative mb-6 pb-4">
    <div class="mb-2 flex flex-wrap items-center gap-3">
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
      <ResetFilterButton v-if="isFiltered" @click="reset" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import DropdownSelect from '@/components/ui/DropdownSelect.vue'
import ResetFilterButton from '@/components/ui/ResetFilterButton.vue'
import SearchableDropdown from '@/components/ui/SearchableDropdown.vue'
import SearchBar from '@/components/ui/SearchBar.vue'
import StatusButtonGroup from '@/components/ui/StatusButtonGroup.vue'

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

const cityId = defineModel('cityId', { type: String, default: 'all' })
const districtId = defineModel('districtId', { type: String, default: 'all' })
const status = defineModel('status', { type: String, default: 'all' })
const searchQuery = defineModel('searchQuery', { type: String, default: '' })

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
