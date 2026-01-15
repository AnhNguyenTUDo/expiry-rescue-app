import axios from "axios";

let baseAxiosInstance = null;

export const useAxios = () => {
  if (!baseAxiosInstance) {
    const runtimeConfig = useRuntimeConfig();

    baseAxiosInstance = axios.create({
      baseURL: runtimeConfig.public.apiBase,
      headers: { "Content-Type": "application/json" },
    });

    baseAxiosInstance.interceptors.request.use(
      function (config) {
        return config;
      },
      function (error) {
        return Promise.reject(error);
      }
    );
  }

  return baseAxiosInstance;
};
