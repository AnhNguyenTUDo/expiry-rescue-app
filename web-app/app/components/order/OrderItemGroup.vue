<template>
  <div>
    <!-- Store header -->
    <div class="flex items-center justify-between py-3">
      <div class="flex items-center gap-2">
        <SvgIcon name="icon-store" class="h-5 w-5 text-gray-500" />
        <h2 class="text-base font-semibold text-gray-800">{{ group.supermarketName }}</h2>
      </div>
      <span class="text-sm font-semibold text-gray-600">{{ formatPrice(groupSubtotal) }}</span>
    </div>

    <hr class="border border-gray-400/70" />

    <!-- Items in this store -->
    <div class="divide-y divide-gray-300">
      <OrderItemRow v-for="item in group.items" :key="item.id" :item="item" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import OrderItemRow from '~/components/order/OrderItemRow.vue'
import { formatPrice } from '~/utils/price'

const props = defineProps({
  // { supermarketId, supermarketName, items: [...] }
  group: {
    type: Object,
    required: true,
  },
})

const groupSubtotal = computed(() =>
  props.group.items.reduce((sum, item) => sum + Number(item.subtotal ?? 0), 0)
)
</script>
