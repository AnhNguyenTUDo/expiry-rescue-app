<template>
  <div class="mx-auto max-w-7xl">
    <h1 class="mb-6 text-2xl font-bold text-gray-800">Cart</h1>

    <!-- Empty Cart State -->
    <EmptyCart v-if="cartStore.cartItems.length === 0" />

    <!-- Cart Items -->
    <div v-else class="flex items-start gap-6">
      <!-- Left: column headers + grouped items -->
      <div class="min-w-0 flex-1 rounded-xl bg-white p-4 shadow">
        <!-- Products Grouped by Supermarket -->
        <div class="space-y-6">
          <CartStoreGroup
            v-for="group in cartStore.itemsBySupermarket"
            :key="group.supermarketId"
            :group="group"
            @toggle-selection="cartStore.toggleItemSelection($event)"
            @update-quantity="cartStore.updateQuantity($event.inventoryId, $event.quantity)"
            @remove="cartStore.removeFromCart($event)"
          />
        </div>
      </div>

      <!-- Right: summary -->
      <div class="w-80 shrink-0">
        <CartSummary
          :total-items="cartStore.totalSelectedItems"
          :total-price="cartStore.totalPrice"
          :total-savings="cartStore.totalSavings"
          :checkout-disabled="cartStore.selectedItems.length === 0"
          @checkout="handleCheckout"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import CartStoreGroup from '~/components/cart/CartStoreGroup.vue'
import CartSummary from '~/components/cart/CartSummary.vue'
import EmptyCart from '~/components/cart/EmptyCart.vue'
import { useCartStore } from '~/stores/cart'
import { useOrderStore } from '~/stores/order'

const cartStore = useCartStore()
const orderStore = useOrderStore()
const router = useRouter()

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
