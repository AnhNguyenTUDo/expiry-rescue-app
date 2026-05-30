export const isSupermarketOpen = (from, to) => {
  if (!from || !to) return false
  const now = new Date()
  const currentMinutes = now.getHours() * 60 + now.getMinutes()
  const [fh, fm] = from.split(':').map(Number)
  const [th, tm] = to.split(':').map(Number)
  const fromMinutes = fh * 60 + fm
  const toMinutes = th * 60 + tm
  if (fromMinutes <= toMinutes) {
    return currentMinutes >= fromMinutes && currentMinutes < toMinutes
  }
  return currentMinutes >= fromMinutes || currentMinutes < toMinutes
}

export const formatHour = (time) => {
  if (!time) return ''
  const [h, m] = time.split(':')
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`
}
