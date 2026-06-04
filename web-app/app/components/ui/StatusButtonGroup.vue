<template>
  <div class="inline-flex items-center rounded-[10px] bg-gray-200 p-0.5" role="group">
    <template v-for="(option, idx) in options" :key="option.value">
      <!-- Divider between buttons -->
      <div v-if="idx > 0" class="mx-0.5 h-5 w-px shrink-0 bg-gray-400/60"></div>

      <button
        type="button"
        @click="select(option.value)"
        :class="[
          'cursor-pointer rounded-[9px] px-3 py-2 text-sm leading-none transition-all duration-150',
          modelValue === option.value ? 'bg-white text-gray-900 shadow-sm' : 'hover:text-gray-900',
        ]"
      >
        {{ option.label }}
      </button>
    </template>
  </div>
</template>

<script setup>
const options = [
  { value: 'all', label: 'All' },
  { value: 'open', label: 'Open' },
  { value: 'closed', label: 'Closed' },
]

defineProps({
  modelValue: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['update:modelValue'])

const select = (value) => {
  emit('update:modelValue', value)
}
</script>
