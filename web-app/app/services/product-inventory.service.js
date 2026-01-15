import { useAxios } from "../composables/useAxios";
import { requestAxios } from "../common/utils";
import Constants from "../common/url-constants";

class ProductInventoryService {
  // /**
  //  * Get all product inventories
  //  * @param {Function} errorCallBack - Optional error callback
  //  * @returns {Promise} - Returns all inventories from the API
  //  */
  constructor() {
    // TODO: test this
    // this.instanceId = Math.random();
    // console.log("call api - Instance ID:", this.instanceId);
    this.axios = useAxios(); // Initialize once
  }
  getAllProductInventory(errorCallBack) {
    const url = Constants.endpoints.product_inventory.GET_ALL_PRODUCT_INVENTORY;
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  /**
   * Get product inventory by supermarket ID
   * @param {string} supermarketId - Supermarket ID (UUID)
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns inventories for the supermarket
   */
  getInventoryBySupermarket(supermarketId, errorCallBack) {
    const url = Constants.endpoints.product_inventory.GET_INVENTORY_BY_SUPERMARKET(supermarketId);
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  /**
   * Get product inventory by product master ID
   * @param {string} productMasterId - Product Master ID (UUID)
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns inventories for the product master
   */
  getInventoryByProductMaster(productMasterId, errorCallBack) {
    const url = Constants.endpoints.product_inventory.GET_INVENTORY_BY_PRODUCT_MASTER(productMasterId);
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  // /**
  //  * Get product inventory by ID
  //  * @param {string} id - Inventory ID (UUID)
  //  * @param {Function} errorCallBack - Optional error callback
  //  * @returns {Promise} - Returns single inventory
  //  */
  // getInventoryById(id, errorCallBack) {
  //     const url = `${Constants.endpoints.product_inventory.GET_ALL_PRODUCT_INVENTORY}/${id}`;
  //     return requestAxios(BaseAxiosInstance.get(url), errorCallBack);
  // }

  // /**
  //  * Get inventories by status
  //  * @param {string} status - Inventory status (AVAILABLE, NOT_AVAILABLE)
  //  * @param {Function} errorCallBack - Optional error callback
  //  * @returns {Promise} - Returns inventories with specific status
  //  */
  // getInventoriesByStatus(status, errorCallBack) {
  //     const url = `${Constants.endpoints.product_inventory.GET_ALL_PRODUCT_INVENTORY}/status/${status}`;
  //     return requestAxios(BaseAxiosInstance.get(url), errorCallBack);
  // }

  // /**
  //  * Get expiring inventories
  //  * @param {number} daysBeforeExpiry - Number of days before expiry (default: 7)
  //  * @param {Function} errorCallBack - Optional error callback
  //  * @returns {Promise} - Returns inventories expiring within specified days
  //  */
  // getExpiringInventories(daysBeforeExpiry = 7, errorCallBack) {
  //     const url = `${Constants.endpoints.product_inventory.GET_ALL_PRODUCT_INVENTORY}/expiring?daysBeforeExpiry=${daysBeforeExpiry}`;
  //     return requestAxios(BaseAxiosInstance.get(url), errorCallBack);
  // }
}

export default new ProductInventoryService();
