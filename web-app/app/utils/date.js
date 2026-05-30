export const formatDate = (timestamp, options) => {
  if (!timestamp) return 'N/A'
  const date = new Date(timestamp)
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    ...options,
  })
}

export const formatDateTime = (timestamp) => {
  return formatDate(timestamp, {
    hour: '2-digit',
    minute: '2-digit',
  })
}

export const calculateDaysUntil = (timestamp) => {
  if (!timestamp) return 'N/A'
  const now = Date.now()
  const days = Math.ceil((timestamp - now) / (1000 * 60 * 60 * 24))

  if (days < 0) return 'Expired'
  if (days === 0) return 'Today'
  if (days === 1) return '1 day'
  return `${days} days`
}
