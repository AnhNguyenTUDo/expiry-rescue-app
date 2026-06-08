<template>
  <div class="mx-auto max-w-7xl">
    <h1 class="mb-6 text-3xl font-bold text-gray-800">Shopping Cart</h1>

    <!-- Empty Cart State -->
    <div
      v-if="cartStore.cartItems.length === 0"
      class="rounded-xl bg-white p-12 text-center shadow"
    >
      <div class="mb-4 text-6xl">🛒</div>
      <h2 class="mb-2 text-2xl font-semibold text-gray-700">Your cart is empty</h2>
      <p class="mb-6 text-gray-500">Add products to your cart to get started!</p>
      <NuxtLink to="/" class="btn bg-green-600 text-white hover:bg-green-700">
        Browse Products
      </NuxtLink>
    </div>

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
          <div
            v-for="item in group.items"
            :key="item.inventoryId"
            class="p-4 transition hover:bg-gray-50"
          >
            <div class="flex items-start gap-4">
              <!-- Checkbox -->
              <div class="pt-1">
                <input
                  type="checkbox"
                  :checked="item.selected"
                  class="h-5 w-5 rounded border-gray-300 text-green-600 focus:ring-green-500"
                  @change="cartStore.toggleItemSelection(item.inventoryId)"
                />
              </div>

              <!-- Product Info -->
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-800">{{ item.productName }}</h3>
                <p class="mb-2 text-sm text-gray-500">{{ item.categoryName }}</p>

                <!-- Expiry Info -->
                <div class="mb-2">
                  <ExpiryBadge :expiry-date="item.expiryDate" />
                </div>

                <!-- Price -->
                <div class="mb-3 flex items-center gap-3">
                  <span class="text-2xl font-bold text-green-700">
                    {{ formatPrice(item.sellingPrice) }}
                  </span>
                  <span class="text-sm text-gray-400 line-through">
                    {{ formatPrice(item.originalPrice) }}
                  </span>
                  <span class="rounded bg-red-600 px-2 py-1 text-xs font-semibold text-white">
                    {{ calculateDiscount(item.originalPrice, item.sellingPrice) }}
                  </span>
                </div>

                <!-- Quantity Controls -->
                <div class="flex items-center gap-4">
                  <div class="flex items-center rounded-lg border border-gray-300">
                    <button
                      :disabled="item.quantity <= 1"
                      class="cursor-pointer px-3 py-1 text-lg font-bold hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-50"
                      @click="cartStore.decreaseQuantity(item.inventoryId)"
                    >
                      −
                    </button>
                    <span class="min-w-[50px] px-4 py-1 text-center font-semibold">
                      {{ item.quantity }}
                    </span>
                    <button
                      :disabled="item.quantity >= item.quantityAvailable"
                      class="cursor-pointer px-3 py-1 text-lg font-bold hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-50"
                      @click="cartStore.increaseQuantity(item.inventoryId)"
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
              <div class="space-y-2 text-right">
                <div class="text-xl font-bold text-gray-800">
                  {{ formatPrice(item.sellingPrice * item.quantity) }}
                </div>
                <button
                  class="cursor-pointer text-sm font-semibold text-red-600 hover:text-red-800"
                  @click="cartStore.removeFromCart(item.inventoryId)"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
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
import ExpiryBadge from '~/components/ui/ExpiryBadge.vue'
import { useCartStore } from '~/stores/cart'
import { useOrderStore } from '~/stores/order'
import { calculateDiscount, formatPrice } from '~/utils/price'

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

<style scoped>
@reference "tailwindcss";
.btn {
  @apply rounded-lg px-6 py-3 font-semibold transition;
}
</style>
