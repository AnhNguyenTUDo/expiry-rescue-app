<template>
  <div class="sticky top-20 z-30 bg-white shadow-lg mb-6 w-screen ml-[calc(50%-50vw)]">
    <div class="max-w-7xl mx-auto px-4 py-3 relative flex items-center">
      <button
        type="button"
        class="shrink-0 p-1.5 rounded-[8px] transition-all duration-150"
        :class="atStart ? 'text-gray-300' : 'text-gray-600 cursor-pointer'"
        :disabled="atStart"
        @click="scrollLeft"
      >
        <SvgIcon name="icon-chevron-left" class="w-3.5 h-3.5" />
      </button>

      <div
        ref="trackRef"
        class="flex gap-2 overflow-x-auto scroll-smooth flex-1"
        style="scrollbar-width: none"
      >
        <button
          v-for="option in options"
          :key="option.id"
          type="button"
          class="cursor-pointer flex-col items-center gap-1.5 px-3 py-1.5 rounded-[10px] whitespace-nowrap transition-all duration-150"
          :class="
            modelValue === option.id ? 'bg-green-50' : 'bg-white text-gray-700 hover:bg-gray-100'
          "
          @click="emit('update:modelValue', option.id)"
        >
          <div class="flex justify-center">
            <SvgIcon name="icon-shopping-cart" class="text-gray-400 w-10 h-10" />
          </div>
          <div class="flex items-center gap-1">
            <span>{{ option.name }}</span>
            <span class="text-sm opacity-70">({{ option.count }})</span>
          </div>
        </button>
      </div>

      <button
        type="button"
        class="shrink-0 p-1.5 rounded-[8px] transition-all duration-150"
        :class="atEnd ? 'text-gray-300' : 'text-gray-600 cursor-pointer'"
        :disabled="atEnd"
        @click="scrollRight"
      >
        <SvgIcon name="icon-chevron-right" class="w-3.5 h-3.5" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

defineProps({
  modelValue: {
    type: String,
    required: true,
  },
  options: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['update:modelValue'])

const trackRef = ref(null)
const atStart = ref(true)
const atEnd = ref(false)

const SCROLL_STEP = 200

const updateBounds = () => {
  const el = trackRef.value
  if (!el) return
  atStart.value = el.scrollLeft <= 0
  atEnd.value = el.scrollLeft + el.clientWidth >= el.scrollWidth - 1
}

const scrollLeft = () => {
  trackRef.value?.scrollBy({ left: -SCROLL_STEP, behavior: 'smooth' })
}

const scrollRight = () => {
  trackRef.value?.scrollBy({ left: SCROLL_STEP, behavior: 'smooth' })
}

onMounted(() => {
  const el = trackRef.value
  if (!el) return
  updateBounds()
  el.addEventListener('scroll', updateBounds)
  new ResizeObserver(updateBounds).observe(el)
})

onUnmounted(() => {
  trackRef.value?.removeEventListener('scroll', updateBounds)
})
</script>
