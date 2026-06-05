<template>
  <button
    :disabled="loading"
    class="group relative flex w-full cursor-pointer items-center justify-center gap-3 rounded-xl border border-gray-300 bg-white px-6 py-3.5 font-medium text-gray-700 transition-colors duration-200 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
    @click="$emit('click')"
  >
    <SvgIcon v-if="loading" name="icon-loading" class="h-5.5 w-5.5 text-gray-500" />
    <SvgIcon v-else name="icon-google" class="h-5.5 w-5.5" />
    <span>{{ loading ? 'Signing in...' : 'Continue with Google' }}</span>
  </button>
</template>

<script setup>
defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['click'])
</script>
