<template>
  <SearchBar
    :model-value="modelValue"
    placeholder="Search products..."
    @update:model-value="emit('update:modelValue', $event)"
  />
</template>

<script setup>
import SearchBar from '~/components/ui/SearchBar.vue'

defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue'])
</script>
