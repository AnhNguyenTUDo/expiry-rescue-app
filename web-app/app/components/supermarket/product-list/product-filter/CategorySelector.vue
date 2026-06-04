<template>
  <div class="sticky top-20 z-30 mb-6 ml-[calc(50%-50vw)] w-screen bg-white shadow-lg">
    <div class="relative mx-auto flex max-w-7xl items-center px-4 py-3">
      <button
        type="button"
        class="shrink-0 rounded-[8px] p-1.5 transition-all duration-150"
        :class="atStart ? 'text-gray-300' : 'cursor-pointer text-gray-600'"
        :disabled="atStart"
        @click="scrollLeft"
      >
        <SvgIcon name="icon-chevron-left" class="h-3.5 w-3.5" />
      </button>

      <div
        ref="trackRef"
        class="flex flex-1 gap-2 overflow-x-auto scroll-smooth"
        style="scrollbar-width: none"
      >
        <button
          v-for="option in options"
          :key="option.id"
          type="button"
          class="cursor-pointer flex-col items-center gap-1.5 rounded-[10px] px-3 py-1.5 whitespace-nowrap transition-all duration-150"
          :class="
            modelValue === option.id ? 'bg-green-50' : 'bg-white text-gray-700 hover:bg-gray-100'
          "
          @click="emit('update:modelValue', option.id)"
        >
          <div class="flex justify-center">
            <SvgIcon name="icon-shopping-cart" class="h-10 w-10 text-gray-400" />
          </div>
          <div class="flex items-center gap-1">
            <span>{{ option.name }}</span>
            <span class="text-sm opacity-70">({{ option.count }})</span>
          </div>
        </button>
      </div>

      <button
        type="button"
        class="shrink-0 rounded-[8px] p-1.5 transition-all duration-150"
        :class="atEnd ? 'text-gray-300' : 'cursor-pointer text-gray-600'"
        :disabled="atEnd"
        @click="scrollRight"
      >
        <SvgIcon name="icon-chevron-right" class="h-3.5 w-3.5" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

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
