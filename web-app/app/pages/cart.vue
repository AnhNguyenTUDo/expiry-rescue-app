<template>
  <div class="mx-auto max-w-7xl">
    <h1 class="mb-6 text-2xl font-bold text-gray-800">
      Cart
      <span v-if="cartStore.cartItems.length" class="text-base font-medium text-gray-500">
        ({{ cartStore.totalItems }} items)
      </span>
    </h1>

    <!-- Empty Cart State -->
    <EmptyCart v-if="cartStore.cartItems.length === 0" />

    <!-- Cart Items -->
    <div v-else class="flex flex-col gap-6 lg:flex-row lg:items-start">
      <!-- Left: select all + grouped items -->
      <div class="min-w-0 flex-1 rounded-xl bg-white p-4">
        <!-- Select all -->
        <label class="flex cursor-pointer items-center gap-3 px-2">
          <input
            type="checkbox"
            :checked="cartStore.allSelected"
            aria-label="Select all items"
            class="h-4.5 w-4.5 rounded-sm border-gray-300 text-green-700 focus:ring-green-600"
            @change="cartStore.toggleSelectAll()"
          />
          <span class="font-semibold text-gray-700">Select all</span>
        </label>

        <!-- Products Grouped by Supermarket -->
        <div class="mt-2 space-y-6">
          <CartStoreGroup
            v-for="group in cartStore.itemsBySupermarket"
            :key="group.supermarketId"
            :group="group"
            @toggle-selection="cartStore.toggleItemSelection($event)"
            @update-quantity="cartStore.updateQuantity($event.inventoryId, $event.quantity)"
            @remove="requestRemove($event)"
          />
        </div>
      </div>

      <!-- Right: summary -->
      <div class="w-full lg:w-80 lg:shrink-0">
        <CartSummary
          :total-items="cartStore.totalSelectedItems"
          :total-price="cartStore.totalPrice"
          :total-savings="cartStore.totalSavings"
          :checkout-disabled="cartStore.selectedItems.length === 0"
          :checking-out="isCheckingOut"
          :error="checkoutError"
          @checkout="handleCheckout"
        />
      </div>
    </div>

    <!-- Processing overlay while an order is being placed -->
    <div
      v-if="isCheckingOut"
      class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/50"
    >
      <div class="flex flex-col items-center gap-3 rounded-xl bg-white px-8 py-6 shadow-lg">
        <SvgIcon name="icon-loading" class="h-8 w-8 animate-spin text-green-700" />
        <p class="font-medium text-gray-700">Placing your order…</p>
      </div>
    </div>

    <!-- Confirm removal of a single item -->
    <ConfirmModal
      :show="pendingRemoveId !== null"
      title="Remove item?"
      message="This item will be removed from your cart."
      confirm-label="Remove"
      variant="danger"
      @confirm="confirmRemove"
      @cancel="cancelRemove"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import CartStoreGroup from '~/components/cart/CartStoreGroup.vue'
import CartSummary from '~/components/cart/CartSummary.vue'
import EmptyCart from '~/components/cart/EmptyCart.vue'
import ConfirmModal from '~/components/ui/ConfirmModal.vue'
import { useNotify } from '~/composables/useNotify'
import { useCartStore } from '~/stores/cart'
import { useOrderStore } from '~/stores/order'

const cartStore = useCartStore()
const orderStore = useOrderStore()
const router = useRouter()
const notify = useNotify()

// Reconcile the persisted cart against live inventory when the page opens.
// initCart is idempotent, so this safely hydrates even though the page mounts before the layout.
onMounted(async () => {
  cartStore.initCart()
  const { removed } = await cartStore.refreshAvailability()
  if (removed > 0) {
    notify.info(
      `${removed} item${removed > 1 ? 's are' : ' is'} no longer available and ${
        removed > 1 ? 'were' : 'was'
      } removed from your cart.`
    )
  }
})

// Item pending removal confirmation (inventoryId, or null when no prompt)
const pendingRemoveId = ref(null)

const requestRemove = (inventoryId) => {
  pendingRemoveId.value = inventoryId
}

const confirmRemove = () => {
  cartStore.removeFromCart(pendingRemoveId.value)
  pendingRemoveId.value = null
}

const cancelRemove = () => {
  pendingRemoveId.value = null
}

// Checkout handler
const isCheckingOut = ref(false)
const checkoutError = ref('')

// Strip the backend's generic 500 prefix for a cleaner message (see deferred backend follow-up)
const formatCheckoutError = (msg) =>
  (msg || '').replace(/^An unexpected error occurred:\s*/i, '') ||
  'Something went wrong placing your order. Please try again.'

// Keep the processing overlay visible for at least this long so the transition isn't abrupt
const MIN_PROCESSING_MS = 2000
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const handleCheckout = async () => {
  // Guard against double-submit while a request is in flight
  if (isCheckingOut.value) return
  if (cartStore.selectedItems.length === 0) return

  isCheckingOut.value = true
  checkoutError.value = ''
  orderStore.clearError()
  const startedAt = Date.now()

  let order
  try {
    const orderData = {
      items: cartStore.selectedItems.map((item) => ({
        inventoryId: item.inventoryId,
        quantity: item.quantity,
      })),
    }
    order = await orderStore.createOrder(orderData)
  } catch (error) {
    console.error('Checkout failed:', error)
  }

  // Enforce the minimum overlay duration before navigating or showing an error
  const remaining = MIN_PROCESSING_MS - (Date.now() - startedAt)
  if (remaining > 0) await delay(remaining)

  if (order) {
    // Success: clear bought items and land on the order page (the confirmation).
    // Keep the overlay up through navigation so the empty cart never flashes.
    cartStore.removeSelectedItems()
    notify.success('Order placed successfully!')
    await router.push(`/orders/${order.id}`)
    return
  }

  // Failed: surface the reason and drop the overlay so the user can retry
  checkoutError.value = formatCheckoutError(orderStore.error)
  notify.error(checkoutError.value)
  isCheckingOut.value = false
}
</script>
