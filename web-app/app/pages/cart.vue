<template>
  <div class="max-w-7xl mx-auto">
    <h1 class="text-3xl font-bold text-gray-800 mb-6">Shopping Cart</h1>

    <!-- Empty Cart State -->
    <div
      v-if="cartStore.cartItems.length === 0"
      class="bg-white rounded-xl shadow p-12 text-center"
    >
      <div class="text-6xl mb-4">🛒</div>
      <h2 class="text-2xl font-semibold text-gray-700 mb-2">Your cart is empty</h2>
      <p class="text-gray-500 mb-6">Add products to your cart to get started!</p>
      <NuxtLink to="/" class="btn bg-green-600 text-white hover:bg-green-700">
        Browse Products
      </NuxtLink>
    </div>

    <!-- Cart Items -->
    <div v-else class="space-y-6">
      <!-- Select All -->
      <div class="bg-white rounded-xl shadow p-4">
        <label class="flex items-center cursor-pointer">
          <input
            type="checkbox"
            :checked="cartStore.allSelected"
            @change="cartStore.toggleSelectAll()"
            class="w-5 h-5 text-green-600 border-gray-300 rounded focus:ring-green-500"
          />
          <span class="ml-3 text-lg font-semibold text-gray-700">Select All</span>
        </label>
      </div>

      <!-- Products Grouped by Supermarket -->
      <div
        v-for="group in cartStore.itemsBySupermarket"
        :key="group.supermarketId"
        class="bg-white rounded-xl shadow"
      >
        <!-- Supermarket Header -->
        <div class="bg-green-600 text-white p-4 rounded-t-xl">
          <h2 class="text-xl font-bold">{{ group.supermarketName }}</h2>
        </div>

        <!-- Products List -->
        <div class="divide-y divide-gray-200">
          <div
            v-for="item in group.items"
            :key="item.inventoryId"
            class="p-4 hover:bg-gray-50 transition"
          >
            <div class="flex items-start gap-4">
              <!-- Checkbox -->
              <div class="pt-1">
                <input
                  type="checkbox"
                  :checked="item.selected"
                  @change="cartStore.toggleItemSelection(item.inventoryId)"
                  class="w-5 h-5 text-green-600 border-gray-300 rounded focus:ring-green-500"
                />
              </div>

              <!-- Product Info -->
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-800">{{ item.productName }}</h3>
                <p class="text-sm text-gray-500 mb-2">{{ item.categoryName }}</p>

                <!-- Expiry Info -->
                <div class="flex items-center gap-2 text-sm mb-2">
                  <span class="text-gray-600">Expires:</span>
                  <span class="text-green-700 font-semibold">
                    {{ formatDate(item.expiryDate) }}
                  </span>
                  <span class="bg-green-700 text-white text-xs px-2 py-0.5 rounded">
                    {{ calculateDaysUntil(item.expiryDate) }}
                  </span>
                </div>

                <!-- Price -->
                <div class="flex items-center gap-3 mb-3">
                  <span class="text-2xl font-bold text-green-700">
                    {{ item.sellingPrice.toLocaleString() }}₫
                  </span>
                  <span class="text-sm line-through text-gray-400">
                    {{ item.originalPrice.toLocaleString() }}₫
                  </span>
                  <span class="bg-red-600 text-white text-xs font-semibold px-2 py-1 rounded">
                    {{ calculateDiscount(item.originalPrice, item.sellingPrice) }}
                  </span>
                </div>

                <!-- Quantity Controls -->
                <div class="flex items-center gap-4">
                  <div class="flex items-center border border-gray-300 rounded-lg">
                    <button
                      @click="cartStore.decreaseQuantity(item.inventoryId)"
                      :disabled="item.quantity <= 1"
                      class="px-3 py-1 text-lg font-bold hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
                    >
                      −
                    </button>
                    <span class="px-4 py-1 font-semibold min-w-[50px] text-center">
                      {{ item.quantity }}
                    </span>
                    <button
                      @click="cartStore.increaseQuantity(item.inventoryId)"
                      :disabled="item.quantity >= item.quantityAvailable"
                      class="px-3 py-1 text-lg font-bold hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
                    >
                      +
                    </button>
                  </div>
                  <span class="text-sm text-gray-500">
                    ({{ item.quantityAvailable }} available)
                  </span>
                </div>
              </div>

              <!-- Subtotal and Delete -->
              <div class="text-right space-y-2">
                <div class="text-xl font-bold text-gray-800">
                  {{ (item.sellingPrice * item.quantity).toLocaleString() }}₫
                </div>
                <button
                  @click="cartStore.removeFromCart(item.inventoryId)"
                  class="text-red-600 hover:text-red-800 text-sm font-semibold cursor-pointer"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Cart Summary -->
      <div class="bg-white rounded-xl shadow p-6 sticky bottom-4">
        <div class="flex justify-between items-center mb-4">
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
            <div class="text-sm text-gray-600 mb-1">Total Price:</div>
            <div class="text-3xl font-bold text-green-700">
              {{ cartStore.totalPrice.toLocaleString() }}₫
            </div>
          </div>
        </div>

        <div class="flex gap-3">
          <button
            @click="cartStore.clearCart()"
            class="flex-1 py-3 px-6 rounded-lg font-semibold text-lg bg-gray-500 text-white hover:bg-gray-600 transition cursor-pointer"
          >
            Clear Cart
          </button>
          <button
            @click="handleCheckout"
            :disabled="cartStore.selectedItems.length === 0"
            class="flex-1 py-3 px-6 rounded-lg font-semibold text-lg transition"
            :class="
              cartStore.selectedItems.length === 0
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                : 'bg-green-600 text-white hover:bg-green-700 cursor-pointer'
            "
          >
            Checkout ({{ cartStore.selectedItems.length }})
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '~/stores/cart'
import { useOrderStore } from '~/stores/order'
import { useRouter } from 'vue-router'

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

// Shared helpers
import { formatDate, calculateDaysUntil } from '~/utils/date'
import { calculateDiscount } from '~/utils/price'
</script>

<style scoped>
@reference "tailwindcss";
.btn {
  @apply px-6 py-3 rounded-lg font-semibold transition;
}
</style>
