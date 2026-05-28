<template>
  <div ref="dropdownRef" class="relative">
    <!-- Trigger button -->
    <button
      type="button"
      @click="isOpen = !isOpen"
      class="inline-flex items-center justify-between gap-2 border border-gray-300 rounded-[10px] px-3 py-1.5 text-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-1 focus:ring-green-600 focus:border-green-600 cursor-pointer"
      :style="{ minWidth: minWidth }"
    >
      <span>{{ selectedLabel }} </span>
      <SvgIcon
        name="icon-chevron-down"
        class="w-3 h-3 text-gray-500 transition-transform duration-200"
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
        class="absolute z-20 mt-1 bg-white rounded-[15px] shadow-lg border border-gray-100 w-full min-w-max overflow-hidden"
      >
        <ul class="p-2 text-sm text-gray-700 max-h-60 overflow-y-auto">
          <li v-for="option in options" :key="option.value">
            <button
              type="button"
              @click="select(option.value)"
              class="w-full text-left px-3 py-2 rounded-md hover:bg-gray-100 hover:text-gray-900 cursor-pointer flex items-center gap-2 transition-colors"
              :class="
                modelValue === option.value
                  ? 'font-semibold text-green-700 bg-green-50 hover:bg-green-100'
                  : ''
              "
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
import { ref, computed, onMounted, onUnmounted } from 'vue'

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

// Close on click outside
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false
  }
}

onMounted(() => document.addEventListener('mousedown', handleClickOutside))
onUnmounted(() => document.removeEventListener('mousedown', handleClickOutside))
</script>
