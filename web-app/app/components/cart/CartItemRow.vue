<template>
  <div class="p-4 transition hover:bg-gray-50">
    <div class="flex items-start gap-4">
      <!-- Checkbox -->
      <div class="pt-1">
        <input
          type="checkbox"
          :checked="item.selected"
          class="h-5 w-5 rounded border-gray-300 text-green-600 focus:ring-green-500"
          @change="emit('toggle-selection', item.inventoryId)"
        />
      </div>

      <!-- Column 1: Product Info -->
      <div class="flex-1">
        <h3 class="text-lg font-semibold text-gray-800">{{ item.productName }}</h3>
        <p class="mb-2 text-sm text-gray-500">{{ item.categoryName }}</p>

        <!-- Expiry Info -->
        <div class="mb-2">
          <ExpiryBadge :expiry-date="item.expiryDate" />
        </div>

        <!-- Price -->
        <div>
          <PriceBlock :original-price="item.originalPrice" :selling-price="item.sellingPrice" />
        </div>
      </div>

      <!-- Column 2: Quantity + Delete -->
      <div class="flex items-center gap-3">
        <div class="flex flex-col items-center gap-1">
          <QuantityCounter
            :model-value="item.quantity"
            :max="item.quantityAvailable"
            @update:model-value="
              emit('update-quantity', { inventoryId: item.inventoryId, quantity: $event })
            "
          />
          <span class="text-sm text-gray-500"> ({{ item.quantityAvailable }} available) </span>
        </div>
        <button
          class="cursor-pointer text-sm font-semibold text-red-600 hover:text-red-800"
          @click="emit('remove', item.inventoryId)"
        >
          Delete
        </button>
      </div>

      <!-- Column 3: Subtotal -->
      <div class="text-right">
        <div class="text-xl font-bold text-gray-800">
          {{ formatPrice(item.sellingPrice * item.quantity) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import ExpiryBadge from '~/components/ui/ExpiryBadge.vue'
import PriceBlock from '~/components/ui/PriceBlock.vue'
import QuantityCounter from '~/components/ui/QuantityCounter.vue'
import { formatPrice } from '~/utils/price'

defineProps({
  item: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['toggle-selection', 'update-quantity', 'remove'])
</script>
