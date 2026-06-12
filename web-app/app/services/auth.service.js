import { useAxios } from '../composables/useAxios'
import { requestAxios } from '../utils/axios-helper'
import urlConstants from '~/utils/url-constants'

class AuthService {
  constructor() {
    this.axios = useAxios()
  }

  getCurrentUser(errorCallback) {
    return requestAxios(this.axios.get(urlConstants.endpoints.auth.GET_CURRENT_USER), errorCallback)
  }

  requestOtp(email) {
    return this.axios.post(urlConstants.endpoints.auth.PASSWORDLESS_REQUEST, { email })
  }

  verifyOtp(email, code) {
    return this.axios.post(urlConstants.endpoints.auth.PASSWORDLESS_VERIFY, { email, code })
  }

  demoLogin() {
    return this.axios.post(urlConstants.endpoints.auth.PASSWORDLESS_DEMO)
  }
}

export default new AuthService()
