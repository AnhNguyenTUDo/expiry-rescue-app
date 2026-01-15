import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  // State
  const cartItems = ref([])

  // Getters
  const totalItems = computed(() => {
    return cartItems.value.reduce((total, item) => total + item.quantity, 0)
  })

  const selectedItems = computed(() => {
    return cartItems.value.filter(item => item.selected)
  })

  const totalSelectedItems = computed(() => {
    return selectedItems.value.reduce((total, item) => total + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return selectedItems.value.reduce((total, item) => {
      return total + (item.sellingPrice * item.quantity)
    }, 0)
  })

  const itemsBySupermarket = computed(() => {
    const grouped = {}
    cartItems.value.forEach(item => {
      if (!grouped[item.supermarketId]) {
        grouped[item.supermarketId] = {
          supermarketId: item.supermarketId,
          supermarketName: item.supermarketName,
          items: []
        }
      }
      grouped[item.supermarketId].items.push(item)
    })
    return Object.values(grouped)
  })

  const allSelected = computed(() => {
    return cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
  })

  // Actions
  function addToCart(product) {
    const existingItem = cartItems.value.find(
      item => item.inventoryId === product.inventoryId
    )

    if (existingItem) {
      // Increase quantity if item already exists
      existingItem.quantity += 1
    } else {
      // Add new item to cart
      cartItems.value.push({
        inventoryId: product.inventoryId,
        productMasterId: product.productMasterId,
        productName: product.productName,
        productDescription: product.productDescription,
        categoryName: product.categoryName,
        supermarketId: product.supermarketId,
        supermarketName: product.supermarketName,
        originalPrice: product.originalPrice,
        sellingPrice: product.sellingPrice,
        expiryDate: product.expiryDate,
        quantityAvailable: product.quantityAvailable,
        quantity: 1,
        selected: true
      })
    }
  }

  function removeFromCart(inventoryId) {
    const index = cartItems.value.findIndex(item => item.inventoryId === inventoryId)
    if (index !== -1) {
      cartItems.value.splice(index, 1)
    }
  }

  function updateQuantity(inventoryId, quantity) {
    const item = cartItems.value.find(item => item.inventoryId === inventoryId)
    if (item) {
      // Ensure quantity doesn't exceed available stock
      item.quantity = Math.min(Math.max(1, quantity), item.quantityAvailable)
    }
  }

  function increaseQuantity(inventoryId) {
    const item = cartItems.value.find(item => item.inventoryId === inventoryId)
    if (item && item.quantity < item.quantityAvailable) {
      item.quantity += 1
    }
  }

  function decreaseQuantity(inventoryId) {
    const item = cartItems.value.find(item => item.inventoryId === inventoryId)
    if (item && item.quantity > 1) {
      item.quantity -= 1
    }
  }

  function toggleItemSelection(inventoryId) {
    const item = cartItems.value.find(item => item.inventoryId === inventoryId)
    if (item) {
      item.selected = !item.selected
    }
  }

  function toggleSelectAll() {
    const selectAll = !allSelected.value
    cartItems.value.forEach(item => {
      item.selected = selectAll
    })
  }

  function clearCart() {
    cartItems.value = []
  }

  function isInCart(inventoryId) {
    return cartItems.value.some(item => item.inventoryId === inventoryId)
  }

  function getCartItemQuantity(inventoryId) {
    const item = cartItems.value.find(item => item.inventoryId === inventoryId)
    return item ? item.quantity : 0
  }

  return {
    // State
    cartItems,
    // Getters
    totalItems,
    selectedItems,
    totalSelectedItems,
    totalPrice,
    itemsBySupermarket,
    allSelected,
    // Actions
    addToCart,
    removeFromCart,
    updateQuantity,
    increaseQuantity,
    decreaseQuantity,
    toggleItemSelection,
    toggleSelectAll,
    clearCart,
    isInCart,
    getCartItemQuantity
  }
})
