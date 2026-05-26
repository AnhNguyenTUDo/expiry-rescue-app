// // import axios from "axios";

// // const basebaseAxiosInstance = () => {
// //   const runtimeConfig = useRuntimeConfig();
// //   console.log("Assdfsdfdsfsdfa");
// //   const baseAxiosInstance = axios.create({
// //     baseURL: runtimeConfig.public.apiBase,
// //     headers: { "Content-Type": "application/json" },
// //   });

// //   baseAxiosInstance.interceptors.request.use(
// //     function (config) {
// //       return config;
// //     },
// //     function (error) {
// //       return Promise.reject(error);
// //     }
// //   );
// //   return baseAxiosInstance;
// // };

// // export default basebaseAxiosInstance;

// import axios from "axios";

// const runtimeConfig = useRuntimeConfig();

// const basebaseAxiosInstance = axios.create({
//   baseURL: runtimeConfig.public.apiBase,
//   headers: { "Content-Type": "application/json" },
// });

// basebaseAxiosInstance.interceptors.request.use(
//   function (config) {
//     return config;
//   },
//   function (error) {
//     return Promise.reject(error);
//   }
// );

// export default basebaseAxiosInstance;
// composables/useAxios.ts
import axios from "axios";

let baseAxiosInstance = null;

export const useAxios = () => {
  console.log("AAAAAAAAAAAAAAAAAAA");
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

export default baseAxiosInstance;
