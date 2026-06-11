<template>
  <div class="rounded-xl bg-white px-5 py-4 shadow">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <SvgIcon name="icon-cart-order" class="h-6 w-6 text-gray-800" />
        <h1 class="text-lg font-semibold text-gray-800">Order #{{ order.orderNumber }}</h1>
        <OrderStatusTag :status="order.status" />
      </div>
      <button
        v-if="order.status === 'CONFIRMED'"
        class="cursor-pointer rounded-[8px] border border-red-300 px-3.5 py-1.5 text-sm font-medium text-red-700 transition-colors hover:bg-red-50"
        @click="emit('cancel')"
      >
        Cancel Order
      </button>
      <button
        v-else-if="order.status === 'CANCELLED'"
        class="cursor-pointer rounded-[8px] border border-red-300 px-3.5 py-1.5 text-sm font-medium text-red-700 transition-colors hover:bg-red-50"
        @click="emit('delete')"
      >
        Delete Order
      </button>
    </div>

    <!-- Summary -->
    <dl class="mt-3 space-y-1.5 text-sm">
      <div class="flex">
        <dt class="w-28 shrink-0 text-gray-500">Date created</dt>
        <dd class="text-gray-800">{{ formatDate(order.createdAt) }}</dd>
      </div>
      <div class="flex">
        <dt class="w-28 shrink-0 text-gray-500">Products</dt>
        <dd class="text-gray-800">{{ order.itemCount }}</dd>
      </div>
      <div v-if="savings > 0" class="flex">
        <dt class="w-28 shrink-0 text-gray-500">You save</dt>
        <dd class="font-semibold text-green-700">{{ formatPrice(savings) }}</dd>
      </div>
      <div class="flex items-baseline">
        <dt class="w-28 shrink-0 font-semibold text-gray-800">Total</dt>
        <dd class="text-base font-bold">{{ formatPrice(order.totalAmount ?? 0) }}</dd>
      </div>
    </dl>

    <!-- Items grouped by store -->
    <div class="-mx-5 mt-4 border-t border-gray-300 px-5 pt-4">
      <p class="text-xs font-semibold tracking-wide text-gray-600 uppercase">Products</p>
      <div class="mt-5 space-y-4">
        <OrderItemGroup v-for="group in itemGroups" :key="group.supermarketId" :group="group" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import OrderItemGroup from '~/components/order/OrderItemGroup.vue'
import OrderStatusTag from '~/components/ui/OrderStatusTag.vue'
import { formatDate } from '~/utils/date'
import { formatPrice } from '~/utils/price'

const props = defineProps({
  order: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['cancel', 'delete', 'reorder'])

// Total saved vs. original prices across all items (the rescue value).
const savings = computed(() =>
  (props.order.items ?? []).reduce(
    (sum, item) =>
      sum + (Number(item.originalPrice ?? 0) - Number(item.price ?? 0)) * (item.quantity ?? 0),
    0
  )
)

// Group the order's items by supermarket (matches the cart's store grouping).
const itemGroups = computed(() => {
  const groups = new Map()
  for (const item of props.order.items ?? []) {
    if (!groups.has(item.supermarketId)) {
      groups.set(item.supermarketId, {
        supermarketId: item.supermarketId,
        supermarketName: item.supermarketName,
        items: [],
      })
    }
    groups.get(item.supermarketId).items.push(item)
  }
  return [...groups.values()]
})
</script>
