<template>
  <div
    class="flex items-center gap-1.5"
    :class="size === 'lg' ? 'text-base text-gray-700' : 'text-sm text-gray-600'"
  >
    <SvgIcon :name="tier.icon" :class="[tier.iconClass, size === 'lg' ? 'h-7 w-7' : 'h-6 w-6']" />
    <span>Expires {{ dateLabel }}</span>
    <span
      class="rounded-sm border font-semibold"
      :class="[
        tier.pillClass,
        size === 'lg' ? 'px-2.5 py-0.5 text-sm' : 'px-2 pt-px pb-0.75 text-xs',
      ]"
    >
      {{ daysLabel }}
    </span>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { calculateDaysUntil, formatDate } from '~/utils/date'

const props = defineProps({
  expiryDate: {
    type: Number,
    required: true,
  },
  // 'sm' matches the compact ProductCard style; 'lg' is for the product detail buy box
  size: {
    type: String,
    default: 'sm',
  },
})

const daysUntilExpiry = computed(() =>
  Math.ceil((props.expiryDate - Date.now()) / (1000 * 60 * 60 * 24))
)

// Tiered urgency: red <=3d, yellow <=7d, green otherwise (matches the rescue urgency vocabulary)
const tier = computed(() => {
  const days = daysUntilExpiry.value
  if (days <= 3)
    return {
      icon: 'icon-calendar-sad',
      iconClass: 'text-red-500',
      pillClass: 'bg-red-50 text-red-700 border-red-200',
    }
  if (days <= 7)
    return {
      icon: 'icon-calendar-neutral',
      iconClass: 'text-yellow-500',
      pillClass: 'bg-yellow-50 text-yellow-700 border-yellow-300',
    }
  return {
    icon: 'icon-calendar-happy',
    iconClass: 'text-green-600',
    pillClass: 'bg-green-50 text-green-700 border-green-200',
  }
})

const dateLabel = computed(() => formatDate(props.expiryDate, { year: undefined }))
const daysLabel = computed(() => calculateDaysUntil(props.expiryDate))
</script>
