<template>
  <div class="container mx-auto px-4 py-8">
    <LoadingState v-if="orderStore.loading" message="Loading order details..." />
    <ErrorAlert v-else-if="orderStore.error" :error="orderStore.error" class="mb-4">
      <NuxtLink to="/orders" class="mt-2 inline-block text-red-800 underline">
        ← Back to Orders
      </NuxtLink>
    </ErrorAlert>

    <!-- Order Details -->
    <div v-else-if="order" class="space-y-6">
      <!-- Header -->
      <div class="rounded-lg bg-white p-6 shadow">
        <div class="mb-4 flex items-start justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-800">Order #{{ order.orderNumber }}</h1>
            <p class="text-gray-600">Placed on {{ formatDate(order.createdAt) }}</p>
          </div>
          <span
            class="rounded-full px-4 py-2 text-sm font-semibold"
            :class="getStatusClass(order.status)"
          >
            {{ getStatusLabel(order.status) }}
          </span>
        </div>

        <div class="border-t pt-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-gray-600">Total Items</p>
              <p class="text-lg font-semibold">{{ order.itemCount }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Total Amount</p>
              <p class="text-2xl font-bold text-green-600">
                {{ formatPrice(order.totalAmount ?? 0) }}
              </p>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="mt-4 flex gap-3">
          <button
            v-if="order.status === 'CONFIRMED'"
            @click="handleCancelOrder"
            class="cursor-pointer rounded-lg bg-red-600 px-6 py-2 text-white transition hover:bg-red-700"
          >
            Cancel Order
          </button>
          <button
            @click="handleDeleteOrder"
            class="cursor-pointer rounded-lg bg-gray-500 px-6 py-2 text-white transition hover:bg-gray-600"
          >
            Delete Order
          </button>
        </div>
      </div>

      <!-- Order Items -->
      <div class="rounded-lg bg-white p-6 shadow">
        <h2 class="mb-4 text-2xl font-bold">Order Items</h2>
        <div class="space-y-4">
          <div v-for="item in order.items" :key="item.id" class="border-b pb-4 last:border-b-0">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-800">{{ item.productName }}</h3>
                <p class="text-sm text-gray-600">{{ item.supermarketName }}</p>
                <p class="text-sm text-gray-500">Expires: {{ formatDate(item.expiryDate) }}</p>
              </div>
              <div class="text-right">
                <p class="text-gray-600">
                  {{ item.quantity }} × {{ formatPrice(item.price ?? 0) }}
                </p>
                <p class="text-lg font-bold text-green-600">
                  {{ formatPrice(item.subtotal ?? 0) }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Back Button -->
      <div class="text-center">
        <NuxtLink
          to="/orders"
          class="inline-block cursor-pointer rounded-lg bg-gray-500 px-6 py-3 text-white transition hover:bg-gray-600"
        >
          ← Back to Orders
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '~/stores/order'
import { useAuthStore } from '~/stores/auth'
import LoadingState from '~/components/ui/LoadingState.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

const orderId = route.params.id

const order = computed(() => orderStore.currentOrder)

onMounted(async () => {
  try {
    await orderStore.fetchOrderById(orderId)
  } catch (error) {
    console.error('Failed to load order:', error)
  }
})

const handleCancelOrder = async () => {
  if (!confirm('Are you sure you want to cancel this order?')) return

  try {
    await orderStore.cancelOrder(orderId)
    alert('Order cancelled successfully')
  } catch (error) {
    alert('Failed to cancel order')
  }
}

const handleDeleteOrder = async () => {
  if (!confirm('Are you sure you want to delete this order? This action cannot be undone.')) return

  try {
    await orderStore.deleteOrder(orderId)
    alert('Order deleted successfully')
    router.push('/orders')
  } catch (error) {
    alert('Failed to delete order')
  }
}

// Shared helpers
import { formatDateTime } from '~/utils/date'
import { formatPrice } from '~/utils/price'
import { getStatusClass, getStatusLabel } from '~/utils/order'

// Use formatDateTime for orders (includes time)
const formatDate = formatDateTime
</script>
