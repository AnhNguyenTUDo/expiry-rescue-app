import { useAxios } from "../composables/useAxios";
import { requestAxios } from "../common/utils";
import Constants from "../common/url-constants";

class SupermarketService {
  constructor() {
    this.axios = useAxios();
  }

  /**
   * Get all supermarkets
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns all supermarkets from the API
   */
  getAllSupermarkets(errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_ALL_SUPERMARKETS;
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  /**
   * Get all active supermarkets
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns active supermarkets from the API
   */
  getActiveSupermarkets(errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_ACTIVE_SUPERMARKETS;
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  /**
   * Get distinct districts
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns list of distinct districts
   */
  getDistinctDistricts(errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_DISTRICTS;
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  /**
   * Get supermarket by ID
   * @param {string} id - Supermarket ID (UUID)
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns supermarket details
   */
  getSupermarketById(id, errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_SUPERMARKET_BY_ID(id);
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  /**
   * Get supermarket with products by ID
   * @param {string} id - Supermarket ID (UUID)
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns supermarket details with products
   */
  getSupermarketWithProducts(id, errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_SUPERMARKET_WITH_PRODUCTS(id);
    return requestAxios(this.axios.get(url), errorCallBack);
  }
}

export default new SupermarketService();
