<template>
  <div class="flex items-start gap-3 px-2 py-4 sm:gap-4 xl:items-center">
    <!-- Checkbox -->
    <input
      type="checkbox"
      :checked="item.selected"
      :aria-label="`Select ${item.productName}`"
      class="mt-1 h-4.5 w-4.5 shrink-0 rounded-sm border-gray-300 text-green-700 focus:ring-green-600 xl:mt-0"
      @change="emit('toggle-selection', item.inventoryId)"
    />

    <!-- Image -->
    <div
      class="flex h-20 w-20 shrink-0 items-center justify-center rounded-lg border border-gray-200 bg-gray-100 sm:h-25 sm:w-25"
    >
      <SvgIcon name="icon-products" class="h-7 w-7 text-gray-300" />
    </div>

    <!-- Body: details + price + controls (stacked until xl, row from xl) -->
    <div class="flex min-w-0 flex-1 flex-col gap-3 xl:flex-row xl:items-center xl:gap-4">
      <!-- Top line when stacked: product details + line total side by side -->
      <div class="flex items-start justify-between gap-3 xl:contents">
        <!-- Products: name + category + expiry -->
        <div class="min-w-0 xl:flex-1">
          <h3 class="line-clamp-2 font-semibold text-gray-800">{{ item.productName }}</h3>
          <p class="truncate text-sm text-gray-600">{{ item.categoryName }}</p>
          <div class="mt-1">
            <ExpiryBadge :expiry-date="item.expiryDate" />
          </div>
        </div>

        <!-- Total: top-right when stacked, rightmost column from xl -->
        <div class="shrink-0 text-right font-bold text-gray-800 xl:order-last xl:w-24">
          {{ formatPrice(item.sellingPrice * item.quantity) }}
        </div>
      </div>

      <!-- Price -->
      <div class="xl:w-32 xl:shrink-0">
        <PriceBlock
          :original-price="item.originalPrice"
          :selling-price="item.sellingPrice"
          size="sm"
        />
      </div>

      <!-- Quantity + delete -->
      <div class="flex items-start gap-3 xl:justify-end">
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
