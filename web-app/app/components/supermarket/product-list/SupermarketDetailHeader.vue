<template>
  <div
    ref="detailsRef"
    class="bg-white -mt-5 mb-6 w-screen relative left-[calc(50%-50vw)] shadow-lg"
  >
    <div class="max-w-7xl mx-auto px-4 py-6">
      <h1 class="text-3xl font-bold text-gray-800">{{ supermarket.name }}</h1>
      <div class="mt-4 space-y-2">
        <p class="text-gray-600">
          <span class="font-medium">Address:</span> {{ supermarket.address }}
        </p>
        <p v-if="supermarket.phone" class="text-gray-600">
          <span class="font-medium">Phone:</span> {{ supermarket.phone }}
        </p>
        <p
          v-if="supermarket.operatingHoursFrom && supermarket.operatingHoursTo"
          class="text-gray-600"
        >
          <span class="font-medium">Operating Hours:</span>
          {{ supermarket.operatingHoursFrom }} - {{ supermarket.operatingHoursTo }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'

defineProps({
  supermarket: {
    type: Object,
    required: true,
  },
})

const detailsRef = ref(null)
const headerShadow = useHeaderShadow()

const updateHeaderShadow = () => {
  if (!detailsRef.value) return
  const header = document.querySelector('header')
  const headerBottom = header ? header.getBoundingClientRect().bottom : 0
  const detailsBottom = detailsRef.value.getBoundingClientRect().bottom
  headerShadow.value = detailsBottom <= headerBottom
}

onMounted(async () => {
  headerShadow.value = false
  window.addEventListener('scroll', updateHeaderShadow, { passive: true })
  window.addEventListener('resize', updateHeaderShadow)
  await nextTick()
  updateHeaderShadow()
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', updateHeaderShadow)
  window.removeEventListener('resize', updateHeaderShadow)
  headerShadow.value = true
})
</script>
