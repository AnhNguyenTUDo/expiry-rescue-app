<template>
  <div>
    <!-- Supermarket Header -->
    <div class="flex items-center justify-between px-2 py-3">
      <div class="flex items-center gap-2">
        <SvgIcon name="icon-store" class="h-5 w-5 text-gray-500" />
        <h2 class="text-lg font-semibold text-gray-800">{{ group.supermarketName }}</h2>
      </div>
      <span v-if="groupSubtotal > 0" class="text-sm font-semibold text-gray-600">
        {{ formatPrice(groupSubtotal) }}
      </span>
    </div>

    <hr class="border border-gray-400/70" />

    <!-- Products List -->
    <div class="divide-y divide-gray-300">
      <CartItemRow
        v-for="item in group.items"
        :key="item.inventoryId"
        :item="item"
        @toggle-selection="emit('toggle-selection', $event)"
        @update-quantity="emit('update-quantity', $event)"
        @remove="emit('remove', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import CartItemRow from '~/components/cart/CartItemRow.vue'
import { formatPrice } from '~/utils/price'

const props = defineProps({
  group: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['toggle-selection', 'update-quantity', 'remove'])

const groupSubtotal = computed(() =>
  props.group.items
    .filter((item) => item.selected)
    .reduce((sum, item) => sum + item.sellingPrice * item.quantity, 0)
)
</script>
