export default {
  endpoints: {
    product_inventory: {
      GET_ALL_PRODUCT_INVENTORY: "/products/inventory",
      GET_INVENTORY_BY_SUPERMARKET: (supermarketId) =>
        `/products/inventory/supermarket/${supermarketId}`,
      GET_INVENTORY_BY_PRODUCT_MASTER: (productMasterId) =>
        `/products/inventory/product-master/${productMasterId}`,
    },
    supermarket: {
      GET_ALL_SUPERMARKETS: "/supermarkets",
      GET_ACTIVE_SUPERMARKETS: "/supermarkets/active",
      GET_SUPERMARKET_BY_ID: (id) => `/supermarkets/${id}`,
      GET_SUPERMARKET_WITH_PRODUCTS: (id) => `/supermarkets/${id}/with-products`,
    },
    city: {
      GET_ALL_CITIES: "/cities",
      GET_DISTRICTS_BY_CITY: (cityId) => `/cities/${cityId}/districts`,
    },
    product_category: {
      GET_ALL_CATEGORIES: "/product-categories",
    },
    order: {
      CREATE_ORDER: "/orders",
      GET_USER_ORDERS: "/orders",
      GET_ORDER_BY_ID: (id) => `/orders/${id}`,
      CANCEL_ORDER: (id) => `/orders/${id}/cancel`,
      DELETE_ORDER: (id) => `/orders/${id}`,
    },
  },
};
