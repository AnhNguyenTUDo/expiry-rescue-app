export default {
  endpoints: {
    product_inventory: {
      GET_ALL_PRODUCT_INVENTORY: '/products/inventory',
      GET_INVENTORY_BY_SUPERMARKET: (supermarketId) => `/products/inventory/supermarket/${supermarketId}`,
      GET_INVENTORY_BY_PRODUCT_MASTER: (productMasterId) => `/products/inventory/product-master/${productMasterId}`,
    },
    supermarket: {
      GET_ALL_SUPERMARKETS: '/supermarkets',
      GET_ACTIVE_SUPERMARKETS: '/supermarkets/active',
      GET_DISTRICTS: '/supermarkets/districts',
      GET_SUPERMARKET_BY_ID: (id) => `/supermarkets/${id}`,
      GET_SUPERMARKET_WITH_PRODUCTS: (id) => `/supermarkets/${id}/with-products`,
    },
    product_category: {
      GET_ALL_CATEGORIES: '/product-categories',
    },
  },
}
