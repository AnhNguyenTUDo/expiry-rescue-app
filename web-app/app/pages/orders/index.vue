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
      @update:model-value="onTabChange"
    />

    <LoadingState v-if="orderStore.loading" message="Loading orders..." />
    <ErrorAlert v-else-if="orderStore.error" :error="orderStore.error" class="mb-4" />

    <!-- Orders List -->
    <div v-else-if="orderStore.orders.length > 0" class="space-y-4">
      <OrderCard
        v-for="order in orderStore.orders"
        :key="order.id"
        :order="order"
        @delete="requestDelete"
      />
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

    <ConfirmModal
      :show="showDeleteConfirm"
      title="Delete this order?"
      message="This will remove the order from your history."
      confirm-label="Delete Order"
      cancel-label="Keep Order"
      variant="danger"
      @confirm="confirmDelete"
      @cancel="showDeleteConfirm = false"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import OrderCard from '~/components/order/OrderCard.vue'
import OrderStatusTabs from '~/components/order/OrderStatusTabs.vue'
import ConfirmModal from '~/components/ui/ConfirmModal.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import { useOrderStore } from '~/stores/order'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

const statusTabs = [
  { value: 'CONFIRMED', label: 'Confirmed' },
  { value: 'CANCELLED', label: 'Cancelled' },
]

// Initialize the active tab from ?status=, falling back to Confirmed.
const isValidStatus = (s) => statusTabs.some((t) => t.value === s)
const activeStatus = ref(isValidStatus(route.query.status) ? route.query.status : 'CONFIRMED')

const loadOrders = () => orderStore.searchOrders(activeStatus.value, null)

// Delete (cancelled orders only)
const showDeleteConfirm = ref(false)
const pendingDelete = ref(null)

const requestDelete = (order) => {
  pendingDelete.value = order
  showDeleteConfirm.value = true
}

const confirmDelete = async () => {
  showDeleteConfirm.value = false
  if (!pendingDelete.value) return

  try {
    await orderStore.deleteOrder(pendingDelete.value.id)
  } catch (error) {
    console.error('Failed to delete order:', error)
  } finally {
    pendingDelete.value = null
  }
}

// Reflect the active tab in the URL so refresh/share keeps it.
const onTabChange = () => {
  router.replace({ query: { ...route.query, status: activeStatus.value } })
  loadOrders()
}

onMounted(loadOrders)
</script>
