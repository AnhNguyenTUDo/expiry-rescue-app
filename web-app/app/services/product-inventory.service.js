import { useAxios } from '../composables/useAxios'
import { requestAxios } from '../utils/axios-helper'
import Constants from '../utils/url-constants'

class ProductInventoryService {
  constructor() {
    this.axios = useAxios()
  }

  getInventoryBySupermarket(supermarketId, errorCallBack) {
    const url = Constants.endpoints.product_inventory.GET_INVENTORY_BY_SUPERMARKET(supermarketId)
    return requestAxios(this.axios.get(url), errorCallBack)
  }

  getInventoryByProductMaster(productMasterId, errorCallBack) {
    const url =
      Constants.endpoints.product_inventory.GET_INVENTORY_BY_PRODUCT_MASTER(productMasterId)
    return requestAxios(this.axios.get(url), errorCallBack)
  }

  getInventoryBySupermarketAndProductMaster(supermarketId, productMasterId, errorCallBack) {
    const url =
      Constants.endpoints.product_inventory.GET_INVENTORY_BY_SUPERMARKET_AND_PRODUCT_MASTER(
        supermarketId,
        productMasterId
      )
    return requestAxios(this.axios.get(url), errorCallBack)
  }

  getOtherLocationSummaries(
    productMasterId,
    excludeSupermarketId,
    page = 0,
    size = 6,
    errorCallBack
  ) {
    const url = Constants.endpoints.product_inventory.GET_OTHER_LOCATION_SUMMARIES(
      productMasterId,
      excludeSupermarketId,
      page,
      size
    )
    return requestAxios(this.axios.get(url), errorCallBack)
  }
}

export default new ProductInventoryService()
