import { useAxios } from "../composables/useAxios";
import { requestAxios } from "../common/utils";
import Constants from "../common/url-constants";

class CityService {
  constructor() {
    this.axios = useAxios();
  }

  /**
   * Get all active cities
   */
  getAllCities(errorCallBack) {
    const url = Constants.endpoints.city.GET_ALL_CITIES;
    return requestAxios(this.axios.get(url), errorCallBack);
  }

  /**
   * Get districts for a given city
   * @param {string} cityId
   */
  getDistrictsByCity(cityId, errorCallBack) {
    const url = Constants.endpoints.city.GET_DISTRICTS_BY_CITY(cityId);
    return requestAxios(this.axios.get(url), errorCallBack);
  }
}

export default new CityService();
