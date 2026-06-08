<template>
  <div class="mx-auto max-w-7xl">
    <h1 class="mb-6 text-3xl font-bold text-gray-800">Shopping Cart</h1>

    <!-- Empty Cart State -->
    <EmptyCart v-if="cartStore.cartItems.length === 0" />

    <!-- Cart Items -->
    <div v-else class="space-y-6">
      <!-- Select All -->
      <div class="rounded-xl bg-white p-4 shadow">
        <label class="flex cursor-pointer items-center">
          <input
            type="checkbox"
            :checked="cartStore.allSelected"
            class="h-5 w-5 rounded border-gray-300 text-green-600 focus:ring-green-500"
            @change="cartStore.toggleSelectAll()"
          />
          <span class="ml-3 text-lg font-semibold text-gray-700">Select All</span>
        </label>
      </div>

      <!-- Products Grouped by Supermarket -->
      <div
        v-for="group in cartStore.itemsBySupermarket"
        :key="group.supermarketId"
        class="rounded-xl bg-white shadow"
      >
        <!-- Supermarket Header -->
        <div class="rounded-t-xl bg-green-600 p-4 text-white">
          <h2 class="text-xl font-bold">{{ group.supermarketName }}</h2>
        </div>

        <!-- Products List -->
        <div class="divide-y divide-gray-200">
          <CartItemRow
            v-for="item in group.items"
            :key="item.inventoryId"
            :item="item"
            @toggle-selection="cartStore.toggleItemSelection($event)"
            @update-quantity="cartStore.updateQuantity($event.inventoryId, $event.quantity)"
            @remove="cartStore.removeFromCart($event)"
          />
        </div>
      </div>

      <!-- Cart Summary -->
      <div class="sticky bottom-4 rounded-xl bg-white p-6 shadow">
        <div class="mb-4 flex items-center justify-between">
          <div>
            <div class="text-sm text-gray-600">
              Total Items: <span class="font-semibold">{{ cartStore.totalSelectedItems }}</span>
            </div>
            <div class="text-sm text-gray-600">
              Selected:
              <span class="font-semibold">{{ cartStore.selectedItems.length }} product(s)</span>
            </div>
          </div>
          <div class="text-right">
            <div class="mb-1 text-sm text-gray-600">Total Price:</div>
            <div class="text-3xl font-bold text-green-700">
              {{ formatPrice(cartStore.totalPrice) }}
            </div>
          </div>
        </div>

        <div class="flex gap-3">
          <button
            class="flex-1 cursor-pointer rounded-lg bg-gray-500 px-6 py-3 text-lg font-semibold text-white transition hover:bg-gray-600"
            @click="cartStore.clearCart()"
          >
            Clear Cart
          </button>
          <button
            :disabled="cartStore.selectedItems.length === 0"
            class="flex-1 rounded-lg px-6 py-3 text-lg font-semibold transition"
            :class="
              cartStore.selectedItems.length === 0
                ? 'cursor-not-allowed bg-gray-300 text-gray-500'
                : 'cursor-pointer bg-green-600 text-white hover:bg-green-700'
            "
            @click="handleCheckout"
          >
            Checkout ({{ cartStore.selectedItems.length }})
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import CartItemRow from '~/components/cart/CartItemRow.vue'
import EmptyCart from '~/components/cart/EmptyCart.vue'
import { useCartStore } from '~/stores/cart'
import { useOrderStore } from '~/stores/order'
import { formatPrice } from '~/utils/price'

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
