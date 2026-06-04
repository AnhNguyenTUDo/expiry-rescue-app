<template>
  <div ref="dropdownRef" class="relative">
    <!-- Trigger button — same look as DropdownSelect -->
    <button
      type="button"
      :aria-disabled="isDisabled"
      :data-tooltip-target="tooltip && isDisabled ? tooltipId : undefined"
      @click="toggle"
      class="inline-flex items-center justify-between gap-2 rounded-[10px] border px-3 py-1.5 text-sm transition-colors duration-150"
      :class="
        isDisabled
          ? 'cursor-not-allowed border-gray-200 bg-gray-50 text-gray-400'
          : 'cursor-pointer border-gray-300 bg-white text-gray-700 hover:bg-gray-50 focus:border-green-600 focus:ring-1 focus:ring-green-600 focus:outline-none'
      "
      :style="{ minWidth: minWidth }"
    >
      <span>{{ selectedLabel }}</span>
      <SvgIcon
        name="icon-chevron-down"
        class="h-3 w-3 text-gray-500 transition-transform duration-200"
        :class="isOpen ? 'rotate-180' : ''"
      />
    </button>

    <!-- Flowbite Tooltip Element -->
    <Tooltip :id="tooltipId" :content="tooltip" :active="isDisabled" />

    <!-- Flowbite-style dropdown panel -->
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
        class="absolute z-20 mt-1 w-full rounded-[15px] border border-gray-200 bg-white shadow-lg"
      >
        <!-- Search input -->
        <div v-if="showSearch" class="rounded-t-lg border-b border-gray-200 bg-white p-2">
          <label for="dropdown-search-input" class="sr-only">Search</label>
          <input
            ref="searchInputRef"
            id="dropdown-search-input"
            v-model="query"
            type="text"
            class="block w-full rounded border border-gray-300 bg-gray-50 px-2.5 py-1.5 text-sm text-gray-700 placeholder-gray-400 focus:border-green-600 focus:ring-green-600"
            placeholder="Search..."
          />
        </div>

        <!-- Options list -->
        <ul class="max-h-48 overflow-y-auto p-2 text-sm font-medium text-gray-700" role="listbox">
          <li
            v-if="filteredOptions.length === 0"
            class="flex w-full items-center p-2 text-gray-400"
          >
            No results found
          </li>
          <li
            v-for="option in filteredOptions"
            :key="option.value"
            class="flex w-full cursor-pointer items-center rounded p-2 hover:bg-gray-100 hover:text-gray-900"
            :class="
              modelValue === option.value
                ? 'bg-green-50 font-semibold text-green-700 hover:bg-green-100'
                : ''
            "
            role="option"
            :aria-selected="modelValue === option.value"
            @click="select(option.value)"
          >
            {{ option.label }}
          </li>
        </ul>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { useClickOutside } from '~/composables/useClickOutside'
import Tooltip from '@/components/ui/Tooltip.vue'

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
  disabled: {
    type: Boolean,
    default: false,
  },
  tooltip: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue', 'change'])

const isOpen = ref(false)
const query = ref('')
const dropdownRef = ref(null)
const searchInputRef = ref(null)

// Generate unique tooltip element ID
const tooltipId = `tooltip-${Math.random().toString(36).substring(2, 9)}`

// Disabled either explicitly via prop or implicitly if only one (or zero) options exist
const isDisabled = computed(() => props.disabled || props.options.length <= 1)

// Only show search input if there are more than 5 options
const showSearch = computed(() => props.options.length > 5)

const selectedLabel = computed(
  () => props.options.find((o) => o.value === props.modelValue)?.label ?? props.modelValue
)

const filteredOptions = computed(() => {
  const q = query.value.trim().toLowerCase()
  if (!q) return props.options
  return props.options.filter((o) => o.label.toLowerCase().includes(q))
})

const toggle = () => {
  if (isDisabled.value) return
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    query.value = ''
    if (showSearch.value) {
      nextTick(() => searchInputRef.value?.focus())
    }
  }
}

const select = (value) => {
  emit('update:modelValue', value)
  emit('change', value)
  isOpen.value = false
  query.value = ''
}

// Reset query when closed
watch(isOpen, (val) => {
  if (!val) query.value = ''
})

useClickOutside(dropdownRef, () => {
  isOpen.value = false
})
</script>
