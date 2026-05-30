import { useAxios } from '../composables/useAxios'
import { requestAxios } from '../utils/axios-helper'
import Constants from '../utils/url-constants'

class SupermarketService {
  constructor() {
    this.axios = useAxios()
  }

  /**
   * Get all supermarkets
   */
  getAllSupermarkets(errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_ALL_SUPERMARKETS
    return requestAxios(this.axios.get(url), errorCallBack)
  }

  /**
   * Get active supermarkets, optionally filtered by cityId and/or districtId.
   * @param {Object} [filters]
   * @param {string} [filters.cityId]
   * @param {string} [filters.districtId]
   * @param {Function} [errorCallBack]
   */
  getActiveSupermarkets(filters = {}, errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_ACTIVE_SUPERMARKETS
    const params = {}
    if (filters.cityId) params.cityId = filters.cityId
    if (filters.districtId) params.districtId = filters.districtId
    return requestAxios(this.axios.get(url, { params }), errorCallBack)
  }

  /**
   * Get supermarket by ID
   * @param {string} id - Supermarket ID (UUID)
   */
  getSupermarketById(id, errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_SUPERMARKET_BY_ID(id)
    return requestAxios(this.axios.get(url), errorCallBack)
  }

  /**
   * Get supermarket with products by ID
   * @param {string} id - Supermarket ID (UUID)
   */
  getSupermarketWithProducts(id, errorCallBack) {
    const url = Constants.endpoints.supermarket.GET_SUPERMARKET_WITH_PRODUCTS(id)
    return requestAxios(this.axios.get(url), errorCallBack)
  }
}

export default new SupermarketService()
