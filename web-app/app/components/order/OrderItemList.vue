<template>
  <ul class="space-y-2">
    <li v-for="item in visibleItems" :key="item.id" class="flex items-center gap-3">
      <div
        class="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg border border-gray-200 bg-gray-100"
      >
        <SvgIcon name="icon-products" class="h-5 w-5 text-gray-300" />
      </div>
      <div class="min-w-0 flex-1">
        <p class="truncate text-sm font-medium text-gray-800">{{ item.productName }}</p>
        <p class="text-xs text-gray-500">×{{ item.quantity }}</p>
      </div>
    </li>

    <li v-if="remainingCount > 0" class="pl-14 text-sm text-gray-500">
      +{{ remainingCount }} more {{ remainingCount === 1 ? 'item' : 'items' }}
    </li>
  </ul>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  },
  // How many item rows to show before collapsing the rest into "+N more"
  max: {
    type: Number,
    default: 3,
  },
})

const visibleItems = computed(() => props.items.slice(0, props.max))
const remainingCount = computed(() => Math.max(0, props.items.length - props.max))
</script>
