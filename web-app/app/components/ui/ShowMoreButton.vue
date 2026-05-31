<template>
  <div class="text-center mt-4">
    <button
      type="button"
      class="px-6 py-2 rounded-xl border-2 border-gray-600 text-gray-600 font-medium transition-transform duration-300 hover:-translate-y-0.5 hover:shadow-md cursor-pointer active:scale-95"
      @click="emit('click')"
    >
      Show more
      <span class="text-sm opacity-70">({{ remaining }} more)</span>
    </button>
  </div>
</template>

<script setup>
defineProps({
  remaining: {
    type: Number,
    required: true,
  },
})

const emit = defineEmits(['click'])
</script>
