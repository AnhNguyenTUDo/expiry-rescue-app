<template>
  <div class="flex items-center gap-4">
    <select
      id="categoryFilter"
      :value="modelValue"
      class="border border-gray-300 rounded-lg px-4 py-2 focus:ring-green-600 focus:border-green-600 min-w-[250px]"
      @change="emit('update:modelValue', $event.target.value)"
    >
      <option value="all">All Categories</option>
      <option v-for="category in categories" :key="category.id" :value="category.id">
        {{ category.name }}
      </option>
    </select>
  </div>
</template>

<script setup>
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
