<template>
  <div ref="dropdownRef" class="relative">
    <button
      type="button"
      class="flex w-full min-w-full cursor-pointer items-center justify-between rounded-[10px] border border-gray-300 bg-transparent px-4 py-1.5 pr-10 text-gray-700 hover:bg-gray-200 focus:border-green-500 focus:ring-1 focus:ring-green-500 focus:outline-none"
      @click="isOpen = !isOpen"
    >
      <span class="truncate">
        {{ selectedLabel }}
      </span>
      <SvgIcon
        name="icon-chevron-down"
        class="absolute right-4 h-3 w-3 text-gray-500 transition-transform duration-200"
        :class="isOpen ? 'rotate-180' : ''"
      />
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
        class="absolute left-0 z-50 mt-2 max-h-80 w-full min-w-max overflow-y-auto rounded-lg border border-gray-100 bg-white shadow-lg"
      >
        <ul class="p-2 text-sm text-gray-700">
          <li v-for="supermarket in sortedSupermarkets" :key="supermarket.id">
            <button
              type="button"
              class="flex w-full cursor-pointer items-center gap-2 rounded-md px-3 py-2.5 text-left transition-colors hover:bg-gray-100"
              :class="modelValue === supermarket.id ? 'bg-green-50 hover:bg-green-100' : ''"
              @click="select(supermarket.id)"
            >
              <div class="min-w-0 flex-1">
                <p
                  class="truncate font-medium text-gray-900"
                  :class="modelValue === supermarket.id ? 'text-green-700' : ''"
                >
                  {{ supermarket.name }}
                </p>
                <p class="mt-0.5 truncate text-xs text-gray-500">
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
import { computed, ref } from 'vue'
import { useClickOutside } from '~/composables/useClickOutside'

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

useClickOutside(dropdownRef, () => {
  isOpen.value = false
})
</script>
