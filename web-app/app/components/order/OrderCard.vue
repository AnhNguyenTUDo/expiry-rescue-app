<template>
  <div class="rounded-xl bg-white px-5 py-4 shadow">
    <div class="flex items-start justify-between">
      <div class="flex items-center gap-2">
        <SvgIcon name="icon-cart-order" class="h-9 w-9 text-gray-800" />
        <div>
          <h3 class="text-lg font-semibold">
            <NuxtLink
              :to="`/orders/${order.id}`"
              class="text-gray-800 hover:text-green-700 hover:underline"
            >
              Order #{{ order.orderNumber }}
            </NuxtLink>
          </h3>
          <span class="inline-flex items-center gap-1 text-sm text-gray-500">
            <SvgIcon name="icon-calendar-outline" class="h-4 w-4" />
            {{ formatDate(order.createdAt) }}
          </span>
        </div>
      </div>

      <OrderStatusTag :status="order.status" />
    </div>

    <OrderItemList :items="order.items" class="mt-4" />

    <div class="mt-4 flex items-center justify-between border-t border-gray-300 pt-4">
      <span class="text-sm">
        Total:
        <span class="text-base font-bold">{{ formatPrice(order.totalAmount) }}</span>
        <span class="ml-2"
          >·<span class="ml-2"
            >{{ order.itemCount }} {{ order.itemCount === 1 ? 'item' : 'items' }}</span
          ></span
        >
      </span>
      <NuxtLink
        :to="`/orders/${order.id}`"
        class="rounded-[8px] border border-gray-200 bg-gray-100 px-4 py-1.5 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-200"
      >
        Details
      </NuxtLink>
    </div>
  </div>
</template>

<script setup>
import OrderItemList from '~/components/order/OrderItemList.vue'
import OrderStatusTag from '~/components/ui/OrderStatusTag.vue'
import { formatDate } from '~/utils/date'
import { formatPrice } from '~/utils/price'

defineProps({
  order: {
    type: Object,
    required: true,
  },
})
</script>
