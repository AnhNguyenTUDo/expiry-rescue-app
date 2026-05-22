<template>
  <div ref="dropdownRef" class="relative">
    <!-- Trigger button — same look as DropdownSelect -->
    <button
      type="button"
      :aria-disabled="isDisabled"
      :data-tooltip-target="tooltip && isDisabled ? tooltipId : undefined"
      @click="toggle"
      class="inline-flex items-center justify-between gap-2 border rounded-[10px] px-3 py-1.5 text-sm transition-colors duration-150"
      :class="isDisabled 
        ? 'border-gray-200 bg-gray-50 text-gray-400 cursor-not-allowed'
        : 'border-gray-300 bg-white text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-1 focus:ring-green-600 focus:border-green-600 cursor-pointer'"
      :style="{ minWidth: minWidth }"
    >
      <span>{{ selectedLabel }}</span>
      <svg
        class="w-3 h-3 transition-transform duration-200"
        :class="[
          isOpen ? 'rotate-180' : '',
          isDisabled ? 'text-gray-300' : 'text-gray-500'
        ]"
        aria-hidden="true"
        xmlns="http://www.w3.org/2000/svg"
        width="24" height="24" fill="none" viewBox="0 0 24 24"
      >
        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m19 9-7 7-7-7"/>
      </svg>
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
        class="absolute z-20 mt-1 bg-white border border-gray-200 rounded-[15px] shadow-lg w-full"
      >
        <!-- Search input -->
        <div v-if="showSearch" class="bg-white border-b border-gray-200 p-2 rounded-t-lg">
          <label for="dropdown-search-input" class="sr-only">Search</label>
          <input
            ref="searchInputRef"
            id="dropdown-search-input"
            v-model="query"
            type="text"
            class="bg-gray-50 border border-gray-300 text-gray-700 text-sm rounded focus:ring-green-500 focus:border-green-500 block w-full px-2.5 py-2 placeholder-gray-400"
            placeholder="Search..."
          />
        </div>

        <!-- Options list -->
        <ul class="max-h-48 p-2 text-sm text-gray-700 font-medium overflow-y-auto" role="listbox">
          <li v-if="filteredOptions.length === 0" class="w-full flex items-center p-2 text-gray-400">
            No results found
          </li>
          <li
            v-for="option in filteredOptions"
            :key="option.value"
            class="w-full flex items-center p-2 hover:bg-gray-100 hover:text-gray-900 rounded cursor-pointer"
            :class="modelValue === option.value ? 'font-semibold text-green-700 bg-green-50 hover:bg-green-100' : ''"
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
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from "vue";
import Tooltip from "@/components/ui/Tooltip.vue";

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
    default: "160px",
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  tooltip: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue", "change"]);

const isOpen = ref(false);
const query = ref("");
const dropdownRef = ref(null);
const searchInputRef = ref(null);

// Generate unique tooltip element ID
const tooltipId = `tooltip-${Math.random().toString(36).substring(2, 9)}`;

// Disabled either explicitly via prop or implicitly if only one (or zero) options exist
const isDisabled = computed(() => props.disabled || props.options.length <= 1);

// Only show search input if there are more than 5 options
const showSearch = computed(() => props.options.length > 5);

const selectedLabel = computed(
  () => props.options.find((o) => o.value === props.modelValue)?.label ?? props.modelValue
);

const filteredOptions = computed(() => {
  const q = query.value.trim().toLowerCase();
  if (!q) return props.options;
  return props.options.filter((o) => o.label.toLowerCase().includes(q));
});

const toggle = () => {
  if (isDisabled.value) return;
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    query.value = "";
    if (showSearch.value) {
      nextTick(() => searchInputRef.value?.focus());
    }
  }
};

const select = (value) => {
  emit("update:modelValue", value);
  emit("change", value);
  isOpen.value = false;
  query.value = "";
};

// Reset query when closed
watch(isOpen, (val) => {
  if (!val) query.value = "";
});

// Close on click outside
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("mousedown", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("mousedown", handleClickOutside);
});
</script>
