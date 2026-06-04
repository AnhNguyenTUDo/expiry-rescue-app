<template>
  <div class="mt-4 text-center">
    <button
      type="button"
      :disabled="loading"
      class="cursor-pointer rounded-xl border-2 border-gray-600 px-6 py-2 font-medium text-gray-600 transition-transform duration-300 hover:-translate-y-0.5 hover:shadow-md active:scale-95 disabled:translate-y-0 disabled:cursor-not-allowed disabled:opacity-60"
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
