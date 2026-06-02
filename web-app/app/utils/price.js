export const formatPrice = (amount) => {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount)
}

export const calculateDiscount = (originalPrice, sellingPrice) => {
  if (!originalPrice || !sellingPrice) return ''
  const discount = Math.round(((originalPrice - sellingPrice) / originalPrice) * 100)
  return `-${discount}%`
}
