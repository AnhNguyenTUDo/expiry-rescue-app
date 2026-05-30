export const getStatusClass = (status) => {
  const classes = {
    CONFIRMED: 'bg-green-100 text-green-800',
    CANCELLED: 'bg-red-100 text-red-800',
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

export const getStatusLabel = (status) => {
  const labels = {
    CONFIRMED: '✅ Confirmed',
    CANCELLED: '❌ Cancelled',
  }
  return labels[status] || status
}
