<template>
  <form @submit.prevent>
    <label for="supermarket-search" class="sr-only mb-2.5 block text-sm font-medium">Search</label>
    <div class="relative">
      <div class="pointer-events-none absolute inset-y-0 start-0 flex items-center ps-3">
        <SvgIcon name="icon-search" class="h-4 w-4 text-gray-500" />
      </div>
      <input
        :value="modelValue"
        @input="emit('update:modelValue', $event.target.value)"
        type="search"
        id="supermarket-search"
        class="block w-full rounded-[10px] border border-gray-300 bg-white px-3 py-1.5 ps-9 text-sm text-gray-900 shadow-xs placeholder:text-gray-400 focus:border-green-600 focus:ring-green-600"
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
