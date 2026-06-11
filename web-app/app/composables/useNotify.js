import { toast } from 'vue-sonner'

export const useNotify = () => {
  return {
    success: (msg) => toast.success(msg),
    error: (msg) => toast.error(msg),
    info: (msg) => toast(msg),
    promise: toast.promise, // for in-flight actions (e.g. checkout)
  }
}
