<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Loading State -->
    <div v-if="orderStore.loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading order details...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="orderStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
      <p><strong>Error:</strong> {{ orderStore.error }}</p>
      <NuxtLink to="/orders" class="text-red-800 underline mt-2 inline-block">
        ← Back to Orders
      </NuxtLink>
    </div>

    <!-- Order Details -->
    <div v-else-if="order" class="space-y-6">
      <!-- Header -->
      <div class="bg-white p-6 rounded-lg shadow">
        <div class="flex justify-between items-start mb-4">
          <div>
            <h1 class="text-3xl font-bold text-gray-800">Order #{{ order.orderNumber }}</h1>
            <p class="text-gray-600">Placed on {{ formatDate(order.createdAt) }}</p>
          </div>
          <span
            class="px-4 py-2 rounded-full text-sm font-semibold"
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
              <p class="text-2xl font-bold text-green-600">{{ order.totalAmount.toLocaleString() }}₫</p>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex gap-3 mt-4">
          <button
            v-if="order.status === 'CONFIRMED'"
            @click="handleCancelOrder"
            class="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition cursor-pointer"
          >
            Cancel Order
          </button>
          <button
            @click="handleDeleteOrder"
            class="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition cursor-pointer"
          >
            Delete Order
          </button>
        </div>
      </div>

      <!-- Order Items -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h2 class="text-2xl font-bold mb-4">Order Items</h2>
        <div class="space-y-4">
          <div
            v-for="item in order.items"
            :key="item.id"
            class="border-b pb-4 last:border-b-0"
          >
            <div class="flex justify-between items-start">
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-800">{{ item.productName }}</h3>
                <p class="text-sm text-gray-600">{{ item.supermarketName }}</p>
                <p class="text-sm text-gray-500">Expires: {{ formatDate(item.expiryDate) }}</p>
              </div>
              <div class="text-right">
                <p class="text-gray-600">{{ item.quantity }} × {{ item.unitPrice.toLocaleString() }}₫</p>
                <p class="text-lg font-bold text-green-600">{{ item.subtotal.toLocaleString() }}₫</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Back Button -->
      <div class="text-center">
        <NuxtLink to="/orders" class="inline-block px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition cursor-pointer">
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

definePageMeta({
  middleware: 'auth'
})

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

const getStatusClass = (status) => {
  const classes = {
    CONFIRMED: 'bg-green-100 text-green-800',
    CANCELLED: 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getStatusLabel = (status) => {
  const labels = {
    CONFIRMED: '✅ Confirmed',
    CANCELLED: '❌ Cancelled'
  }
  return labels[status] || status
}

const formatDate = (timestamp) => {
  if (!timestamp) return 'N/A'
  return new Date(timestamp).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
