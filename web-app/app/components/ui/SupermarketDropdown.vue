<template>
  <div ref="dropdownRef" class="relative">
    <button
      type="button"
      @click="isOpen = !isOpen"
      class="bg-transparent border border-gray-300 text-gray-700 rounded-[10px] px-4 py-1.5 pr-10 focus:outline-none focus:ring-1 focus:ring-green-500 focus:border-green-500 cursor-pointer hover:bg-gray-200 flex items-center justify-between min-w-full w-full"
    >
      <span class="truncate">
        {{ selectedLabel }}
      </span>
      <svg
        class="w-3 h-3 text-gray-500 transition-transform duration-200 absolute right-4"
        :class="isOpen ? 'rotate-180' : ''"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 10 6"
      >
        <path
          stroke="currentColor"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="m1 1 4 4 4-4"
        />
      </svg>
    </button>

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
        class="absolute left-0 z-50 mt-2 bg-white rounded-lg shadow-lg border border-gray-100 w-full min-w-max max-h-80 overflow-y-auto"
      >
        <ul class="p-2 text-sm text-gray-700">
          <li v-for="supermarket in sortedSupermarkets" :key="supermarket.id">
            <button
              type="button"
              @click="select(supermarket.id)"
              class="w-full text-left px-3 py-2.5 rounded-md hover:bg-gray-100 cursor-pointer flex items-center gap-2 transition-colors"
              :class="modelValue === supermarket.id ? 'bg-green-50 hover:bg-green-100' : ''"
            >
              <div class="flex-1 min-w-0">
                <p
                  class="font-medium text-gray-900 truncate"
                  :class="modelValue === supermarket.id ? 'text-green-700' : ''"
                >
                  {{ supermarket.name }}
                </p>
                <p class="text-xs text-gray-500 truncate mt-0.5">
                  {{ supermarket.districtName
                  }}<span v-if="supermarket.cityName">, {{ supermarket.cityName }}</span>
                </p>
              </div>
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
  supermarkets: {
    type: Array,
    required: true,
  },
  currentSupermarketName: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue', 'change'])

const sortedSupermarkets = computed(() => {
  return [...props.supermarkets].sort((a, b) => {
    const nameA = a.name || ''
    const nameB = b.name || ''
    return nameA.localeCompare(nameB)
  })
})

const isOpen = ref(false)
const dropdownRef = ref(null)

const selectedLabel = computed(() => {
  if (props.currentSupermarketName) {
    return props.currentSupermarketName
  }
  return 'Select Supermarket'
})

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
