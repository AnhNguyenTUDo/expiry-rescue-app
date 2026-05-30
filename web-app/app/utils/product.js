export const calculateAvailability = (expiryDate, quantityAvailable, status) => {
  if (status !== 'AVAILABLE') return 'out of stock'

  const now = Date.now()
  if (expiryDate <= now) return 'out of stock'

  const daysUntilExpiry = Math.ceil((expiryDate - now) / (1000 * 60 * 60 * 24))
  if (daysUntilExpiry <= 3) return 'limited'

  if (quantityAvailable === 0) return 'out of stock'
  if (quantityAvailable <= 10) return 'limited'

  return 'available'
}

export const getAvailability = (item) => {
  return calculateAvailability(item.expiryDate, item.quantityAvailable, item.status)
}

export const getCategoryEmoji = (categoryName) => {
  if (!categoryName) return '🛒'
  const lowerName = categoryName.toLowerCase()
  const emojiMap = {
    dairy: '🧀',
    bakery: '🥐',
    beverages: '🥤',
    spices: '🌶️',
    cosmetics: '💄',
    meat: '🍖',
    seafood: '🦐',
    produce: '🥬',
    fruits: '🍎',
    vegetables: '🥕',
  }
  for (const [keyword, emoji] of Object.entries(emojiMap)) {
    if (lowerName.includes(keyword)) return emoji
  }
  return '🛒'
}
