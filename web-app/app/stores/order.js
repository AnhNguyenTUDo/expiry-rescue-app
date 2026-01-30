import { defineStore } from 'pinia'
import OrderService from '~/services/order.service'

export const useOrderStore = defineStore('order', {
    state: () => ({
        orders: [],
        currentOrder: null,
        loading: false,
        error: null,
    }),

    getters: {
        getOrderById: (state) => (id) => {
            return state.orders.find(order => order.id === id)
        },

        confirmedOrders: (state) => {
            return state.orders.filter(order => order.status === 'CONFIRMED')
        },

        cancelledOrders: (state) => {
            return state.orders.filter(order => order.status === 'CANCELLED')
        },
    },

    actions: {
        async createOrder(orderData) {
            this.loading = true
            this.error = null

            try {
                const response = await OrderService.createOrder(orderData, (error) => {
                    this.error = error.response?.data?.message || 'Failed to create order'
                })

                if (response && response.data) {
                    this.orders.unshift(response.data)
                    return response.data
                }
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        async fetchUserOrders() {
            this.loading = true
            this.error = null

            try {
                const response = await OrderService.getUserOrders((error) => {
                    this.error = error.response?.data?.message || 'Failed to fetch orders'
                })

                if (response && response.data) {
                    this.orders = response.data
                }
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        async searchOrders(status, searchQuery) {
            this.loading = true
            this.error = null

            try {
                const response = await OrderService.searchOrders(status, searchQuery, (error) => {
                    this.error = error.response?.data?.message || 'Failed to search orders'
                })

                if (response && response.data) {
                    this.orders = response.data
                }
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        async fetchOrderById(orderId) {
            this.loading = true
            this.error = null

            try {
                const response = await OrderService.getOrderById(orderId, (error) => {
                    this.error = error.response?.data?.message || 'Failed to fetch order'
                })

                if (response && response.data) {
                    this.currentOrder = response.data
                    return response.data
                }
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        async cancelOrder(orderId) {
            this.loading = true
            this.error = null

            try {
                const response = await OrderService.cancelOrder(orderId, (error) => {
                    this.error = error.response?.data?.message || 'Failed to cancel order'
                })

                if (response && response.data) {
                    // Update order in list
                    const index = this.orders.findIndex(o => o.id === orderId)
                    if (index !== -1) {
                        this.orders[index] = response.data
                    }

                    // Update current order if it's the same
                    if (this.currentOrder?.id === orderId) {
                        this.currentOrder = response.data
                    }

                    return response.data
                }
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        async deleteOrder(orderId) {
            this.loading = true
            this.error = null

            try {
                await OrderService.deleteOrder(orderId, (error) => {
                    this.error = error.response?.data?.message || 'Failed to delete order'
                })

                // Remove order from list
                this.orders = this.orders.filter(o => o.id !== orderId)

                // Clear current order if it's the same
                if (this.currentOrder?.id === orderId) {
                    this.currentOrder = null
                }
            } catch (error) {
                this.error = error.message
                throw error
            } finally {
                this.loading = false
            }
        },

        clearError() {
            this.error = null
        },
    },
})
