<template>
  <div class="inline-flex items-center bg-gray-200 rounded-[10px] p-0.5" role="group">
    <template v-for="(option, idx) in options" :key="option.value">
      <!-- Divider between buttons -->
      <div v-if="idx > 0" class="w-px h-5 bg-gray-400/60 mx-0.5 shrink-0"></div>

      <button
        type="button"
        @click="select(option.value)"
        :class="[
          'px-3 py-2 text-sm rounded-[9px] cursor-pointer transition-all duration-150 leading-none',
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
