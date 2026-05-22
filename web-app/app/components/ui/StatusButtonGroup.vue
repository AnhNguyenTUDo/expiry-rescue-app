<template>
<div class="relative inline-flex group rounded-lg" role="group">
    <div class="relative inline-flex rounded-lg bg-white" role="group">
    <button
      type="button"
      @click="select('all')"
      :class="[
        'px-4 py-1.5 text-sm rounded-s-[10px] border border-gray-300 focus:z-10 focus:outline-none cursor-pointer transition-colors',
        modelValue === 'all'
          ? 'bg-white text-gray-900 ring-1 ring-gray-700 border-gray-700 z-10'
          : 'bg-white text-gray-900 hover:bg-gray-100 focus:ring-2 focus:ring-gray-200 hover:text-green-700'
      ]"
    >
      All
    </button>
    <button
      type="button"
      @click="select('open')"
      :class="[
        'px-4 py-1.5 text-sm border border-gray-300 focus:z-10 focus:outline-none cursor-pointer transition-colors',
        modelValue === 'open'
          ? 'bg-white text-gray-900 ring-1 ring-green-600 border-green-600 z-10'
          : 'bg-white text-gray-900 hover:bg-gray-100 focus:ring-2 focus:ring-gray-200 hover:text-green-700'
      ]"
    >
      Open
    </button>
    <button
      type="button"
      @click="select('closed')"
      :class="[
        'px-4 py-1.5 text-sm border border-gray-300 rounded-e-[10px] focus:z-10 focus:outline-none cursor-pointer transition-colors',
        modelValue === 'closed'
          ? 'bg-white text-gray-900 ring-1 ring-red-400 border-red-400 z-10'
          : 'bg-white text-gray-900 hover:bg-gray-100 focus:ring-2 focus:ring-gray-200 hover:text-green-700'
      ]"
    >
      Closed
    </button>  </div>

  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['update:modelValue']);

const select = (value) => {
  emit('update:modelValue', value);
};
</script>
