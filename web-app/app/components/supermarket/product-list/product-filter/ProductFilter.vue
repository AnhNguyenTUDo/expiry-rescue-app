<template>
  <div class="flex mb-6 gap-3">
    <ProductNameFilter
      :model-value="modelValue"
      @update:model-value="emit('update:modelValue', $event)"
    />
  </div>
</template>

<script setup>
import ProductNameFilter from './ProductNameFilter.vue'

defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue'])
</script>
