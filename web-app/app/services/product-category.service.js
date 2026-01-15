import { useAxios } from "../composables/useAxios";
import { requestAxios } from "../common/utils";
import Constants from "../common/url-constants";

class ProductCategoryService {
  constructor() {
    this.axios = useAxios();
  }

  /**
   * Get all product categories
   * @param {Function} errorCallBack - Optional error callback
   * @returns {Promise} - Returns all categories from the API
   */
  getAllCategories(errorCallBack) {
    const url = Constants.endpoints.product_category.GET_ALL_CATEGORIES;
    return requestAxios(this.axios.get(url), errorCallBack);
  }
}

export default new ProductCategoryService();
