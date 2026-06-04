<template>
  <div class="flex items-stretch select-none">
    <!-- Decrement -->
    <button
      type="button"
      :disabled="model <= min"
      class="px-3 flex items-center justify-center border border-r-0 border-gray-300 rounded-l-[11px] text-gray-600 hover:bg-gray-100 focus:outline-none disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer transition"
      @click="decrement"
    >
      <SvgIcon name="icon-minus" class="w-5 h-5" />
    </button>

    <input
      type="text"
      inputmode="numeric"
      :value="model"
      class="w-12 text-center font-semibold text-gray-700 bg-transparent border border-gray-300 focus:outline-none"
      @input="onInput"
      @blur="onBlur"
    />

    <!-- Increment -->
    <button
      type="button"
      :disabled="model >= max"
      class="px-3 flex items-center justify-center border border-l-0 border-gray-300 rounded-r-[11px] text-gray-600 hover:bg-gray-100 focus:outline-none disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer transition"
      @click="increment"
    >
      <SvgIcon name="icon-plus" class="w-5 h-5" />
    </button>
  </div>
</template>

<script setup>
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
})

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
