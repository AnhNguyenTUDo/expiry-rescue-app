<template>
  <div class="flex items-center gap-2">
    <span class="text-sm font-medium text-gray-500">Sort by </span>
    <DropdownSelect v-model="model" :options="SORT_OPTIONS" />
  </div>
</template>

<script setup>
import DropdownSelect from '~/components/ui/DropdownSelect.vue'

const SORT_OPTIONS = [
  { value: 'expiry', label: 'Expiry (soonest)' },
  { value: 'discount', label: 'Discount (highest)' },
  { value: 'price_asc', label: 'Price (low → high)' },
  { value: 'price_desc', label: 'Price (high → low)' },
]

const model = defineModel({ type: String, required: true })
</script>
