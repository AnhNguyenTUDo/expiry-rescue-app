import { useAxios } from "../composables/useAxios";
import { requestAxios } from "../common/utils";
import urlConstants from "~/common/url-constants";

class OrderService {
  constructor() {
    this.axios = useAxios();
  }

  /**
   * Create a new order
   */
  createOrder(orderData, errorCallback) {
    return requestAxios(
      this.axios.post(urlConstants.endpoints.order.CREATE_ORDER, orderData),
      errorCallback
    );
  }

  /**
   * Get all user orders
   */
  getUserOrders(errorCallback) {
    return requestAxios(
      this.axios.get(urlConstants.endpoints.order.GET_USER_ORDERS),
      errorCallback
    );
  }

  /**
   * Search orders with filters
   */
  searchOrders(status, searchQuery, errorCallback) {
    const params = {};
    if (status) params.status = status;
    if (searchQuery) params.search = searchQuery;

    return requestAxios(
      this.axios.get(urlConstants.endpoints.order.GET_USER_ORDERS, { params }),
      errorCallback
    );
  }

  /**
   * Get order by ID
   */
  getOrderById(orderId, errorCallback) {
    return requestAxios(
      this.axios.get(urlConstants.endpoints.order.GET_ORDER_BY_ID(orderId)),
      errorCallback
    );
  }

  /**
   * Cancel an order
   */
  cancelOrder(orderId, errorCallback) {
    return requestAxios(
      this.axios.put(urlConstants.endpoints.order.CANCEL_ORDER(orderId)),
      errorCallback
    );
  }

  /**
   * Delete an order (soft delete)
   */
  deleteOrder(orderId, errorCallback) {
    return requestAxios(
      this.axios.delete(urlConstants.endpoints.order.DELETE_ORDER(orderId)),
      errorCallback
    );
  }
}

export default new OrderService();
