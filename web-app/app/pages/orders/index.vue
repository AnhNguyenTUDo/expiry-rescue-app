<template>
  <div class="mx-auto max-w-4xl">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-800">My Orders</h1>
    </div>

    <!-- Status tabs -->
    <OrderStatusTabs
      v-model="activeStatus"
      :options="statusTabs"
      class="mb-6"
      @update:model-value="loadOrders"
    />

    <LoadingState v-if="orderStore.loading" message="Loading orders..." />
    <ErrorAlert v-else-if="orderStore.error" :error="orderStore.error" class="mb-4" />

    <!-- Orders List -->
    <div v-else-if="orderStore.orders.length > 0" class="space-y-4">
      <OrderCard v-for="order in orderStore.orders" :key="order.id" :order="order" />
    </div>

    <!-- Empty State -->
    <div v-else class="rounded-xl bg-white py-12 text-center shadow">
      <p class="mb-4 text-lg text-gray-500">
        {{
          activeStatus === 'CANCELLED' ? 'No cancelled orders' : "You haven't placed any orders yet"
        }}
      </p>
      <template v-if="activeStatus !== 'CANCELLED'">
        <p class="mb-6 text-sm text-gray-400">Browse expiring products at discounted prices!</p>
        <NuxtLink
          to="/"
          class="inline-flex items-center gap-1.5 rounded-[11px] bg-green-700 px-6 py-3 font-semibold text-white transition hover:bg-green-800"
        >
          <SvgIcon name="icon-shopping-cart" class="h-4 w-4" />
          Start Shopping
        </NuxtLink>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import OrderCard from '~/components/order/OrderCard.vue'
import OrderStatusTabs from '~/components/order/OrderStatusTabs.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import { useOrderStore } from '~/stores/order'

definePageMeta({ middleware: 'auth' })

const orderStore = useOrderStore()

const statusTabs = [
  { value: 'CONFIRMED', label: 'Confirmed' },
  { value: 'CANCELLED', label: 'Cancelled' },
]

const activeStatus = ref('CONFIRMED')

const loadOrders = () => orderStore.searchOrders(activeStatus.value, null)

onMounted(loadOrders)
</script>
