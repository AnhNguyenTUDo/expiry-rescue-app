export const calculateDiscount = (originalPrice, sellingPrice) => {
  if (!originalPrice || !sellingPrice) return ''
  const discount = Math.round(((originalPrice - sellingPrice) / originalPrice) * 100)
  return `-${discount}%`
}
