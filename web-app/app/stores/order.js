import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import OrderService from '~/services/order.service'

export const useOrderStore = defineStore('order', () => {
    // State
    const orders = ref([])
    const currentOrder = ref(null)
    const loading = ref(false)
    const error = ref(null)

    // Getters
    const getOrderById = computed(() => (id) => {
        return orders.value.find(order => order.id === id)
    })

    const confirmedOrders = computed(() => {
        return orders.value.filter(order => order.status === 'CONFIRMED')
    })

    const cancelledOrders = computed(() => {
        return orders.value.filter(order => order.status === 'CANCELLED')
    })

    // Actions
    async function createOrder(orderData) {
        loading.value = true
        error.value = null

        try {
            const response = await OrderService.createOrder(orderData, (err) => {
                error.value = err.response?.data?.message || 'Failed to create order'
            })

            if (response && response.data) {
                orders.value.unshift(response.data)
                return response.data
            }
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function fetchUserOrders() {
        loading.value = true
        error.value = null

        try {
            const response = await OrderService.getUserOrders((err) => {
                error.value = err.response?.data?.message || 'Failed to fetch orders'
            })
            if (response) {
                orders.value = response
            }
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    async function searchOrders(status, searchQuery) {
        loading.value = true
        error.value = null

        try {
            const response = await OrderService.searchOrders(status, searchQuery, (err) => {
                error.value = err.response?.data?.message || 'Failed to search orders'
            })

            if (response && response.data) {
                orders.value = response.data
            }
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    async function fetchOrderById(orderId) {
        loading.value = true
        error.value = null

        try {
            const response = await OrderService.getOrderById(orderId, (err) => {
                error.value = err.response?.data?.message || 'Failed to fetch order'
            })
            console.log('Response:', response)
            if (response) {
                currentOrder.value = response
                console.log('Current order:', currentOrder.value)
                return response
            }
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function cancelOrder(orderId) {
        loading.value = true
        error.value = null

        try {
            const response = await OrderService.cancelOrder(orderId, (err) => {
                error.value = err.response?.data?.message || 'Failed to cancel order'
            })

            if (response && response.data) {
                // Update order in list
                const index = orders.value.findIndex(o => o.id === orderId)
                if (index !== -1) {
                    orders.value[index] = response.data
                }

                // Update current order if it's the same
                if (currentOrder.value?.id === orderId) {
                    currentOrder.value = response.data
                }

                return response.data
            }
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    async function deleteOrder(orderId) {
        loading.value = true
        error.value = null

        try {
            await OrderService.deleteOrder(orderId, (err) => {
                error.value = err.response?.data?.message || 'Failed to delete order'
            })

            // Remove order from list
            orders.value = orders.value.filter(o => o.id !== orderId)

            // Clear current order if it's the same
            if (currentOrder.value?.id === orderId) {
                currentOrder.value = null
            }
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            loading.value = false
        }
    }

    function clearError() {
        error.value = null
    }

    return {
        // State
        orders,
        currentOrder,
        loading,
        error,
        // Getters
        getOrderById,
        confirmedOrders,
        cancelledOrders,
        // Actions
        createOrder,
        fetchUserOrders,
        searchOrders,
        fetchOrderById,
        cancelOrder,
        deleteOrder,
        clearError,
    }
})
