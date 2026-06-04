<template>
  <div ref="dropdownRef" class="relative">
    <!-- Trigger button -->
    <button
      type="button"
      class="inline-flex cursor-pointer items-center justify-between gap-2 rounded-[10px] border border-gray-300 bg-white px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-50 focus:border-green-600 focus:ring-1 focus:ring-green-600 focus:outline-none"
      :style="{ minWidth: minWidth }"
      @click="isOpen = !isOpen"
    >
      <span>{{ selectedLabel }} </span>
      <SvgIcon
        name="icon-chevron-down"
        class="h-3 w-3 text-gray-500 transition-transform duration-200"
        :class="isOpen ? 'rotate-180' : ''"
      />
    </button>

    <!-- Dropdown menu -->
    <Transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div
        v-show="isOpen"
        class="absolute z-20 mt-1 w-full min-w-max overflow-hidden rounded-[15px] border border-gray-100 bg-white shadow-lg"
      >
        <ul class="max-h-60 overflow-y-auto p-2 text-sm text-gray-700">
          <li v-for="option in options" :key="option.value">
            <button
              type="button"
              class="flex w-full cursor-pointer items-center gap-2 rounded-md px-3 py-2 text-left transition-colors hover:bg-gray-100 hover:text-gray-900"
              :class="
                modelValue === option.value
                  ? 'bg-green-50 font-semibold text-green-700 hover:bg-green-100'
                  : ''
              "
              @click="select(option.value)"
            >
              {{ option.label }}
            </button>
          </li>
        </ul>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useClickOutside } from '~/composables/useClickOutside'

const props = defineProps({
  modelValue: {
    type: String,
    required: true,
  },
  options: {
    type: Array, // Array<{ value: string; label: string }>
    required: true,
  },
  minWidth: {
    type: String,
    default: '160px',
  },
})

const emit = defineEmits(['update:modelValue', 'change'])

const isOpen = ref(false)
const dropdownRef = ref(null)

const selectedLabel = computed(
  () => props.options.find((o) => o.value === props.modelValue)?.label ?? props.modelValue
)

const select = (value) => {
  emit('update:modelValue', value)
  emit('change', value)
  isOpen.value = false
}

useClickOutside(dropdownRef, () => {
  isOpen.value = false
})
</script>
