<template>
  <div class="mx-auto max-w-7xl">
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-3xl font-bold text-gray-800">My Orders</h1>
      <NuxtLink
        to="/"
        class="cursor-pointer rounded-lg bg-green-600 px-6 py-3 text-white transition hover:bg-green-700"
      >
        + New Order
      </NuxtLink>
    </div>

    <!-- Search & Filters -->
    <div class="mb-6 rounded-xl bg-white p-4 shadow">
      <div class="flex flex-wrap items-center gap-4">
        <!-- Search -->
        <SearchBar
          v-model="searchQuery"
          placeholder="Search order number, product, or supermarket"
          class="w-64"
          @update:model-value="handleSearch"
        />

        <!-- Status Filter -->
        <DropdownSelect
          v-model="statusFilter"
          :options="statusOptions"
          prefix="Status:"
          @update:model-value="handleSearch"
        />

        <!-- Reset Filters -->
        <ResetFilterButton v-if="searchQuery || statusFilter" @click="clearFilters" />
      </div>
    </div>

    <LoadingState v-if="orderStore.loading" message="Loading orders..." />
    <ErrorAlert v-else-if="orderStore.error" :error="orderStore.error" class="mb-4" />

    <!-- Orders List -->
    <div v-else-if="orderStore.orders.length > 0" class="space-y-4">
      <div
        v-for="order in orderStore.orders"
        :key="order.id"
        class="cursor-pointer rounded-xl bg-white p-6 shadow transition hover:shadow-md"
        @click="navigateTo(`/orders/${order.id}`)"
      >
        <div class="mb-2 flex items-start justify-between">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">Order #{{ order.orderNumber }}</h3>
            <p class="text-sm text-gray-600">{{ formatDate(order.createdAt) }}</p>
          </div>
          <OrderStatusTag :status="order.status" />
        </div>

        <div class="mb-2 flex items-center justify-between text-sm text-gray-600">
          <span>{{ order.itemCount }} {{ order.itemCount === 1 ? 'item' : 'items' }}</span>
        </div>

        <div class="text-xl font-bold text-green-600">
          {{ formatPrice(order.totalAmount) }}
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="rounded-xl bg-white py-12 text-center shadow">
      <p class="mb-4 text-lg text-gray-500">
        {{
          searchQuery || statusFilter
            ? 'No orders found matching your filters'
            : "You haven't placed any orders yet"
        }}
      </p>
      <p v-if="!searchQuery && !statusFilter" class="mb-6 text-sm text-gray-400">
        Browse expiring products at discounted prices!
      </p>
      <NuxtLink
        to="/"
        class="inline-block cursor-pointer rounded-lg bg-green-600 px-6 py-3 text-white transition hover:bg-green-700"
      >
        🛒 Start Shopping
      </NuxtLink>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import DropdownSelect from '~/components/ui/DropdownSelect.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import OrderStatusTag from '~/components/ui/OrderStatusTag.vue'
import ResetFilterButton from '~/components/ui/ResetFilterButton.vue'
import SearchBar from '~/components/ui/SearchBar.vue'
import { useOrderStore } from '~/stores/order'
import { formatDateTime } from '~/utils/date'
import { formatPrice } from '~/utils/price'

definePageMeta({ middleware: 'auth' })

const orderStore = useOrderStore()

const searchQuery = ref('')
const statusFilter = ref('')

const statusOptions = [
  { value: '', label: 'All' },
  { value: 'CONFIRMED', label: 'Confirmed' },
  { value: 'CANCELLED', label: 'Cancelled' },
]

// Fetch orders on mount
onMounted(async () => {
  await orderStore.fetchUserOrders()
})

// Handle search with debounce
let searchTimeout
const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(async () => {
    await orderStore.searchOrders(statusFilter.value || null, searchQuery.value || null)
  }, 300)
}

// Clear all filters
const clearFilters = async () => {
  searchQuery.value = ''
  statusFilter.value = ''
  await orderStore.fetchUserOrders()
}

// Use formatDateTime for orders (includes time)
const formatDate = formatDateTime
</script>
