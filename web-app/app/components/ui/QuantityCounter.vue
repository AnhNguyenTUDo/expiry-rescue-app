<template>
  <div class="flex items-stretch select-none">
    <!-- Decrement -->
    <button
      type="button"
      :disabled="model <= min"
      class="flex cursor-pointer items-center justify-center rounded-l-[11px] border border-r-0 border-gray-400 text-gray-600 transition hover:bg-gray-200 hover:text-gray-800 focus:outline-none disabled:cursor-not-allowed disabled:opacity-40"
      :class="sizeClasses.button"
      @click="decrement"
    >
      <SvgIcon name="icon-minus" :class="sizeClasses.icon" />
    </button>

    <input
      type="text"
      inputmode="numeric"
      :value="model"
      class="border border-gray-400 bg-transparent text-center font-semibold text-gray-700 focus:outline-none"
      :class="sizeClasses.input"
      @input="onInput"
      @blur="onBlur"
    />

    <!-- Increment -->
    <button
      type="button"
      :disabled="model >= max"
      class="flex cursor-pointer items-center justify-center rounded-r-[11px] border border-l-0 border-gray-400 text-gray-600 transition hover:bg-gray-200 hover:text-gray-800 focus:outline-none disabled:cursor-not-allowed disabled:opacity-40"
      :class="sizeClasses.button"
      @click="increment"
    >
      <SvgIcon name="icon-plus" :class="sizeClasses.icon" />
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const model = defineModel({ type: Number, default: 1 })

const props = defineProps({
  min: {
    type: Number,
    default: 1,
  },
  // Maximum selectable quantity (e.g. units available in stock)
  max: {
    type: Number,
    required: true,
  },
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md'].includes(v),
  },
})

const SIZES = {
  sm: { button: 'px-2', icon: 'h-4 w-4', input: 'w-10 py-[3px] text-sm' },
  md: { button: 'px-3', icon: 'h-5 w-5', input: 'w-12' },
}

const sizeClasses = computed(() => SIZES[props.size])

const clamp = (n) => Math.min(Math.max(props.min, n), props.max)

const decrement = () => {
  model.value = clamp(model.value - 1)
}

const increment = () => {
  model.value = clamp(model.value + 1)
}

const onInput = (e) => {
  const n = parseInt(e.target.value, 10)
  if (!Number.isNaN(n)) model.value = clamp(n)
}

const onBlur = (e) => {
  const n = parseInt(e.target.value, 10)
  model.value = Number.isNaN(n) ? props.min : clamp(n)
  // Reflect the clamped value back (covers out-of-range or non-numeric input)
  e.target.value = model.value
}
</script>

<style scoped>
input:focus,
input:focus-visible {
  outline: none;
  box-shadow: none;
}
</style>
