<template>
  <div class="flex mb-6 gap-3">
    <CategoryFilter
      :model-value="modelValue"
      :categories="categories"
      @update:model-value="emit('update:modelValue', $event)"
    />
  </div>
</template>

<script setup>
import CategoryFilter from './CategoryFilter.vue'

defineProps({
  modelValue: {
    type: String,
    required: true,
  },
  categories: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['update:modelValue'])
</script>
