export const useClickOutside = (elementRef, callback) => {
  const handler = (e) => {
    if (elementRef.value && !elementRef.value.contains(e.target)) callback()
  }
  onMounted(() => document.addEventListener('mousedown', handler))
  onUnmounted(() => document.removeEventListener('mousedown', handler))
}
