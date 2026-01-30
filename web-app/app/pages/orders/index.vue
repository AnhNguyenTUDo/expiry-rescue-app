<template>
  <div class="container mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-3xl font-bold text-gray-800">My Orders</h1>
      <NuxtLink to="/supermarkets" class="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition cursor-pointer">
        + New Order
      </NuxtLink>
    </div>

    <!-- Search & Filters -->
    <div class="bg-white p-4 rounded-lg shadow mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <!-- Search -->
        <input
          v-model="searchQuery"
          type="text"
          placeholder="🔍 Search order number, product, or supermarket..."
          class="border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-green-500 focus:border-green-500"
          @input="handleSearch"
        />
        
        <!-- Status Filter -->
        <select 
          v-model="statusFilter" 
          class="border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-green-500 focus:border-green-500 cursor-pointer"
          @change="handleSearch"
        >
          <option value="">All Statuses</option>
          <option value="CONFIRMED">✅ Confirmed</option>
          <option value="CANCELLED">❌ Cancelled</option>
        </select>
        
        <!-- Clear Filters -->
        <button
          v-if="searchQuery || statusFilter"
          @click="clearFilters"
          class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition cursor-pointer"
        >
          Clear Filters
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="orderStore.loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading orders...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="orderStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
      <p><strong>Error:</strong> {{ orderStore.error }}</p>
    </div>

    <!-- Orders List -->
    <div v-else-if="orderStore.orders.length > 0" class="space-y-4">
      <div
        v-for="order in orderStore.orders"
        :key="order.id"
        class="bg-white p-6 rounded-lg shadow hover:shadow-md transition cursor-pointer"
        @click="navigateTo(`/orders/${order.id}`)"
      >
        <div class="flex justify-between items-start mb-2">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">Order #{{ order.orderNumber }}</h3>
            <p class="text-gray-600 text-sm">{{ formatDate(order.createdAt) }}</p>
          </div>
          <span
            class="px-3 py-1 rounded-full text-sm font-semibold"
            :class="getStatusClass(order.status)"
          >
            {{ getStatusLabel(order.status) }}
          </span>
        </div>
        
        <div class="flex justify-between items-center text-sm text-gray-600 mb-2">
          <span>{{ order.itemCount }} {{ order.itemCount === 1 ? 'item' : 'items' }}</span>
        </div>
        
        <div class="text-xl font-bold text-green-600">
          {{ order.totalAmount.toLocaleString() }}₫
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-12 bg-white rounded-lg shadow">
      <p class="text-gray-500 text-lg mb-4">
        {{ searchQuery || statusFilter ? 'No orders found matching your filters' : 'You haven\'t placed any orders yet' }}
      </p>
      <p v-if="!searchQuery && !statusFilter" class="text-sm text-gray-400 mb-6">
        Browse expiring products at discounted prices!
      </p>
      <NuxtLink to="/supermarkets" class="inline-block px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition cursor-pointer">
        🛒 Start Shopping
      </NuxtLink>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useOrderStore } from '~/stores/order'

definePageMeta({
  middleware: 'auth'
})

const orderStore = useOrderStore()

const searchQuery = ref('')
const statusFilter = ref('')

// Fetch orders on mount
onMounted(async () => {
  await orderStore.fetchUserOrders()
})

// Handle search with debounce
let searchTimeout
const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(async () => {
    await orderStore.searchOrders(
      statusFilter.value || null,
      searchQuery.value || null
    )
  }, 300)
}

// Clear all filters
const clearFilters = async () => {
  searchQuery.value = ''
  statusFilter.value = ''
  await orderStore.fetchUserOrders()
}

// Helper functions
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
