<template>
  <form @submit.prevent>
    <label for="supermarket-search" class="block mb-2.5 text-sm font-medium sr-only">Search</label>
    <div class="relative">
      <div class="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none">
        <SvgIcon name="icon-search" class="w-4 h-4 text-gray-500" />
      </div>
      <input
        :value="modelValue"
        @input="emit('update:modelValue', $event.target.value)"
        type="search"
        id="supermarket-search"
        class="block w-full px-3 py-1.5 ps-9 bg-white border border-gray-300 text-gray-900 text-sm rounded-[10px] focus:ring-green-600 focus:border-green-600 shadow-xs placeholder:text-gray-400"
        :placeholder="placeholder"
      />
    </div>
  </form>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  placeholder: {
    type: String,
    default: 'Search',
  },
})

const emit = defineEmits(['update:modelValue'])
</script>
