<template>
  <div class="text-center mt-4">
    <button
      type="button"
      :disabled="loading"
      class="px-6 py-2 rounded-xl border-2 border-gray-600 text-gray-600 font-medium transition-transform duration-300 hover:-translate-y-0.5 hover:shadow-md cursor-pointer active:scale-95 disabled:opacity-60 disabled:cursor-not-allowed disabled:translate-y-0"
      @click="emit('click')"
    >
      {{ loading ? 'Loading...' : label }}
      <span v-if="!loading && showRemaining" class="text-sm opacity-70"
        >({{ remaining }} more)</span
      >
    </button>
  </div>
</template>

<script setup>
defineProps({
  remaining: {
    type: Number,
    required: true,
  },
  showRemaining: {
    type: Boolean,
    default: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  label: {
    type: String,
    default: 'Show more',
  },
})

const emit = defineEmits(['click'])
</script>
