<template>
  <div class="mx-auto max-w-4xl">
    <LoadingState v-if="orderStore.loading" message="Loading order details..." />
    <ErrorAlert v-else-if="orderStore.error && !order" :error="orderStore.error" class="mb-4">
      <NuxtLink to="/orders" class="mt-2 inline-block text-red-800 underline">
        ← Back to Orders
      </NuxtLink>
    </ErrorAlert>

    <!-- Order Details -->
    <div v-else-if="order">
      <!-- Breadcrumb navigation -->
      <nav class="mb-4 flex items-center gap-1.5 text-sm text-gray-500">
        <NuxtLink to="/" class="transition hover:text-green-700">Home</NuxtLink>
        <span>/</span>
        <NuxtLink to="/orders" class="transition hover:text-green-700">My Orders</NuxtLink>
        <span>/</span>
        <span class="font-medium text-gray-800">Order #{{ order.orderNumber }}</span>
      </nav>

      <OrderDetailCard
        :order="order"
        @cancel="showCancelConfirm = true"
        @delete="showDeleteConfirm = true"
      />
    </div>

    <ConfirmModal
      :show="showCancelConfirm"
      title="Cancel this order?"
      message="This will cancel your order and can't be undone."
      confirm-label="Cancel Order"
      cancel-label="Keep Order"
      variant="danger"
      @confirm="confirmCancel"
      @cancel="showCancelConfirm = false"
    />

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
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import OrderDetailCard from '~/components/order/OrderDetailCard.vue'
import ConfirmModal from '~/components/ui/ConfirmModal.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import { useNotify } from '~/composables/useNotify'
import { useOrderStore } from '~/stores/order'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()
const notify = useNotify()

const orderId = route.params.id

const order = computed(() => orderStore.currentOrder)

const showCancelConfirm = ref(false)
const showDeleteConfirm = ref(false)

onMounted(async () => {
  try {
    await orderStore.fetchOrderById(orderId)
  } catch (error) {
    console.error('Failed to load order:', error)
  }
})

const confirmCancel = async () => {
  showCancelConfirm.value = false

  try {
    await orderStore.cancelOrder(orderId)
    notify.success('Order cancelled.')
  } catch {
    notify.error('Could not cancel the order. Please try again.')
  }
}

const confirmDelete = async () => {
  showDeleteConfirm.value = false

  try {
    await orderStore.deleteOrder(orderId)
    notify.success('Order removed from your history.')
    router.push('/orders')
  } catch {
    notify.error('Could not delete the order. Please try again.')
  }
}
</script>
