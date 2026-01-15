import apiClient from '~/utils/api';

/**
 * Product Inventory API Service
 * Provides methods to interact with product inventory endpoints
 */
export const useProductInventory = () => {
  /**
   * Get all product inventories
   * @returns {Promise} - Returns all inventories
   */
  const getAllInventories = async () => {
    try {
      const response = await apiClient.get('/products/inventory');
      return response.data;
    } catch (error) {
      console.error('Error fetching all inventories:', error);
      throw error;
    }
  };

  /**
   * Get product inventory by ID
   * @param {string} id - Inventory ID (UUID)
   * @returns {Promise} - Returns single inventory
   */
  const getInventoryById = async (id) => {
    try {
      const response = await apiClient.get(`/products/inventory/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching inventory ${id}:`, error);
      throw error;
    }
  };

  /**
   * Get inventories by supermarket
   * @param {string} supermarketId - Supermarket ID (UUID)
   * @returns {Promise} - Returns inventories for specific supermarket
   */
  const getInventoriesBySupermarket = async (supermarketId) => {
    try {
      const response = await apiClient.get(`/products/inventory/supermarket/${supermarketId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching inventories for supermarket ${supermarketId}:`, error);
      throw error;
    }
  };

  /**
   * Get inventories by status
   * @param {string} status - Inventory status (AVAILABLE, NOT_AVAILABLE)
   * @returns {Promise} - Returns inventories with specific status
   */
  const getInventoriesByStatus = async (status) => {
    try {
      const response = await apiClient.get(`/products/inventory/status/${status}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching inventories with status ${status}:`, error);
      throw error;
    }
  };

  /**
   * Get expiring inventories
   * @param {number} daysBeforeExpiry - Number of days before expiry (default: 7)
   * @returns {Promise} - Returns inventories expiring within specified days
   */
  const getExpiringInventories = async (daysBeforeExpiry = 7) => {
    try {
      const response = await apiClient.get('/products/inventory/expiring', {
        params: { daysBeforeExpiry }
      });
      return response.data;
    } catch (error) {
      console.error(`Error fetching expiring inventories:`, error);
      throw error;
    }
  };

  /**
   * Create new product inventory
   * @param {Object} inventoryData - Inventory data
   * @param {string} inventoryData.productMasterId - Product master ID
   * @param {string} inventoryData.supermarketId - Supermarket ID
   * @param {number} inventoryData.originalPrice - Original price
   * @param {number} inventoryData.sellingPrice - Selling price
   * @param {number} inventoryData.quantityAvailable - Quantity available
   * @param {number} inventoryData.expiryDate - Expiry date (timestamp in milliseconds)
   * @returns {Promise} - Returns created inventory
   */
  const createInventory = async (inventoryData) => {
    try {
      const response = await apiClient.post('/products/inventory', inventoryData);
      return response.data;
    } catch (error) {
      console.error('Error creating inventory:', error);
      throw error;
    }
  };

  /**
   * Update product inventory
   * @param {string} id - Inventory ID
   * @param {Object} updateData - Data to update
   * @param {number} [updateData.originalPrice] - Original price
   * @param {number} [updateData.sellingPrice] - Selling price
   * @param {number} [updateData.quantityAvailable] - Quantity available
   * @param {number} [updateData.expiryDate] - Expiry date (timestamp in milliseconds)
   * @param {string} [updateData.status] - Inventory status
   * @returns {Promise} - Returns updated inventory
   */
  const updateInventory = async (id, updateData) => {
    try {
      const response = await apiClient.put(`/products/inventory/${id}`, updateData);
      return response.data;
    } catch (error) {
      console.error(`Error updating inventory ${id}:`, error);
      throw error;
    }
  };

  /**
   * Mark inventory as not available
   * @param {string} id - Inventory ID
   * @returns {Promise} - Returns success response
   */
  const markAsNotAvailable = async (id) => {
    try {
      const response = await apiClient.put(`/products/inventory/${id}/mark-unavailable`);
      return response.data;
    } catch (error) {
      console.error(`Error marking inventory ${id} as not available:`, error);
      throw error;
    }
  };

  /**
   * Delete product inventory (soft delete)
   * @param {string} id - Inventory ID
   * @returns {Promise} - Returns success response
   */
  const deleteInventory = async (id) => {
    try {
      const response = await apiClient.delete(`/products/inventory/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error deleting inventory ${id}:`, error);
      throw error;
    }
  };

  return {
    getAllInventories,
    getInventoryById,
    getInventoriesBySupermarket,
    getInventoriesByStatus,
    getExpiringInventories,
    createInventory,
    updateInventory,
    markAsNotAvailable,
    deleteInventory,
  };
};
