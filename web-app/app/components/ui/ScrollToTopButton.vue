<template>
  <Transition
    enter-active-class="transition ease-out duration-200"
    enter-from-class="opacity-0 translate-y-2"
    enter-to-class="opacity-100 translate-y-0"
    leave-active-class="transition ease-in duration-150"
    leave-from-class="opacity-100 translate-y-0"
    leave-to-class="opacity-0 translate-y-2"
  >
    <button
      v-if="visible"
      type="button"
      @click="scrollToTop"
      class="fixed right-6 bottom-6 z-50 cursor-pointer rounded-full border-2 border-green-700 p-3 text-green-700 shadow-lg transition-all duration-300 hover:-translate-y-0.5 hover:shadow-xl active:scale-85"
      aria-label="Scroll to top"
    >
      <SvgIcon name="icon-chevron-up" class="h-5 w-5" />
    </button>
  </Transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const visible = ref(false)

const onScroll = () => {
  visible.value = window.scrollY > 300
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>
