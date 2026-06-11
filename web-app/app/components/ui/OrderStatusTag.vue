<template>
  <div
    class="inline-flex items-center gap-1.5 rounded-[7px] px-2.5 py-1.5 font-semibold"
    :class="[tag.tagClass, size === 'lg' ? 'text-sm' : 'text-xs']"
  >
    <SvgIcon v-if="tag.icon" :name="tag.icon" :class="size === 'lg' ? 'h-4 w-4' : 'h-3 w-3'" />
    {{ tag.label }}
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // Order status, e.g. 'CONFIRMED' | 'CANCELLED'
  status: {
    type: String,
    required: true,
  },
  // 'sm' for list cards; 'lg' for the order detail header
  size: {
    type: String,
    default: 'sm',
  },
})

const TAG_BY_STATUS = {
  CONFIRMED: {
    label: 'Confirmed',
    icon: 'icon-check-circle',
    tagClass: 'bg-green-50 border border-green-200 text-green-700',
  },
  CANCELLED: {
    label: 'Cancelled',
    icon: 'icon-out-of-stock',
    tagClass: 'bg-red-50 border border-red-200 text-[#dc3545]',
  },
}

const tag = computed(
  () =>
    TAG_BY_STATUS[props.status] || {
      label: props.status || 'Unknown',
      icon: null,
      tagClass: 'bg-gray-50 border border-gray-200 text-gray-700',
    }
)
</script>
