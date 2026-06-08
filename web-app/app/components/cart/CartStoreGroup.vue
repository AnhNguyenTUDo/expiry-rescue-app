<template>
  <div class="rounded-xl bg-white shadow">
    <!-- Supermarket Header -->
    <div class="rounded-t-xl bg-green-600 p-4 text-white">
      <div class="flex items-center justify-between">
        <h2 class="text-xl font-bold">{{ group.supermarketName }}</h2>
        <span v-if="groupSubtotal > 0" class="text-sm font-semibold opacity-90">
          {{ formatPrice(groupSubtotal) }}
        </span>
      </div>
    </div>

    <!-- Products List -->
    <div class="divide-y divide-gray-200">
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
