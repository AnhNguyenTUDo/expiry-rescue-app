<template>
  <div class="mx-auto max-w-7xl">
    <h1 class="mb-6 text-2xl font-bold text-gray-800">Cart</h1>

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
          @checkout="handleCheckout"
        />
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CartStoreGroup from '~/components/cart/CartStoreGroup.vue'
import CartSummary from '~/components/cart/CartSummary.vue'
import EmptyCart from '~/components/cart/EmptyCart.vue'
import ConfirmModal from '~/components/ui/ConfirmModal.vue'
import { useCartStore } from '~/stores/cart'
import { useOrderStore } from '~/stores/order'

const cartStore = useCartStore()
const orderStore = useOrderStore()
const router = useRouter()

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
const handleCheckout = async () => {
  if (cartStore.selectedItems.length === 0) {
    alert('Please select items to checkout')
    return
  }

  try {
    // Prepare order data
    const orderData = {
      items: cartStore.selectedItems.map((item) => ({
        inventoryId: item.inventoryId,
        quantity: item.quantity,
      })),
    }

    // Create order
    const order = await orderStore.createOrder(orderData)

    if (order) {
      // Remove checked out items from cart
      cartStore.selectedItems.forEach((item) => {
        cartStore.removeFromCart(item.inventoryId)
      })

      // Redirect to order detail page
      alert(`Order #${order.orderNumber} created successfully!`)
      router.push(`/orders/${order.id}`)
    }
  } catch (error) {
    console.error('Checkout failed:', error)
    alert('Failed to create order. Please try again.')
  }
}
</script>
