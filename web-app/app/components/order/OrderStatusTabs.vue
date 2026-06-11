<template>
  <div class="flex items-center gap-6 border-b border-gray-200">
    <button
      v-for="option in options"
      :key="option.value"
      type="button"
      class="-mb-px cursor-pointer border-b-2 px-1 pb-3 text-sm font-medium transition-colors"
      :class="
        modelValue === option.value
          ? 'border-green-700 text-green-700'
          : 'border-transparent text-gray-500 hover:text-gray-800'
      "
      @click="emit('update:modelValue', option.value)"
    >
      {{ option.label }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  // Active status value, e.g. 'CONFIRMED'
  modelValue: {
    type: String,
    required: true,
  },
  // [{ value, label }]
  options: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['update:modelValue'])
</script>
