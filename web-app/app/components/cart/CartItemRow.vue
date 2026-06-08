<template>
  <div class="grid grid-cols-[1.5rem_1fr_8rem_9rem_7rem] items-center gap-4 px-2 py-4">
    <!-- Checkbox -->
    <input
      type="checkbox"
      :checked="item.selected"
      :aria-label="`Select ${item.productName}`"
      class="h-4.5 w-4.5 rounded-sm border-gray-300 text-lg text-green-700 focus:ring-green-600"
      @change="emit('toggle-selection', item.inventoryId)"
    />

    <!-- Products: image + name -->
    <div class="flex items-center gap-3">
      <div
        class="flex h-25 w-25 shrink-0 items-center justify-center rounded-lg border border-gray-200 bg-gray-100"
      >
        <SvgIcon name="icon-products" class="h-7 w-7 text-gray-300" />
      </div>
      <div class="min-w-0">
        <h3 class="line-clamp-2 font-semibold text-gray-800">{{ item.productName }}</h3>
        <p class="truncate text-sm text-gray-600">{{ item.categoryName }}</p>
        <div class="mt-1">
          <ExpiryBadge :expiry-date="item.expiryDate" />
        </div>
      </div>
    </div>

    <!-- Price -->
    <PriceBlock :original-price="item.originalPrice" :selling-price="item.sellingPrice" size="sm" />

    <!-- Quantity: counter + remaining below, delete on the right -->
    <div class="flex items-start gap-3">
      <div class="flex flex-col items-center gap-1">
        <QuantityCounter
          :model-value="item.quantity"
          :max="item.quantityAvailable"
          size="sm"
          @update:model-value="
            emit('update-quantity', { inventoryId: item.inventoryId, quantity: $event })
          "
        />
        <span class="text-xs text-gray-700">{{ item.quantityAvailable }} available</span>
      </div>
      <button class="group cursor-pointer" @click="emit('remove', item.inventoryId)">
        <SvgIcon
          name="icon-trash-circle"
          class="h-7 w-7 group-hover:text-red-700 group-hover:[--icon-bg:#ef4444]"
        />
      </button>
    </div>

    <!-- Total -->
    <div class="text-right font-bold text-gray-800">
      {{ formatPrice(item.sellingPrice * item.quantity) }}
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
