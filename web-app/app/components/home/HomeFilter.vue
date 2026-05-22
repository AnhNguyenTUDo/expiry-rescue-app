<template>
  <div class="relative mb-6 pb-4">
    <div class="flex items-center gap-3 mb-2 flex-wrap">
      <!-- Search bar -->
      <SearchBar v-model="searchQuery" placeholder="Search supermarkets..." />

      <!-- City dropdown -->
      <DropdownSelect
        v-model="cityId"
        :options="cityOptions"
        @change="emit('city-change')"
      />

      <!-- District dropdown (searchable) -->
      <SearchableDropdown
        v-model="districtId"
        :options="districtOptions"
        :disabled="cityId === 'all'"
        tooltip="Select a city first"
        @change="emit('district-change')"
      />

      <!-- Open/Closed status filter pills -->
      <StatusButtonGroup v-model="status" />

      <!-- Reset filters -->
      <button
        v-if="isFiltered"
        type="button"
        @click="reset"
        class="flex items-center gap-1 text-sm text-[#bc4747] hover:text-red-700 hover:scale-104 transition-all duration-300 cursor-pointer"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 640 640" fill="currentColor">
          <path d="M0 0h640v640H0z" fill="none" />
          <path d="M320 128c-56.8 0-107.9 24.7-143.1 64H224c17.7 0 32 14.3 32 32s-14.3 32-32 32H96c-17.7 0-32-14.3-32-32V96c0-17.7 14.3-32 32-32s32 14.3 32 32v54.7C174.9 97.6 243.5 64 320 64c141.4 0 256 114.6 256 256S461.4 576 320 576c-87 0-163.9-43.4-210.1-109.7c-10.1-14.5-6.6-34.4 7.9-44.6s34.4-6.6 44.6 7.9c34.8 49.8 92.4 82.3 157.6 82.3c106 0 192-86 192-192S426 128 320 128" />
        </svg>
        Reset Filter
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import DropdownSelect from "@/components/ui/DropdownSelect.vue";
import SearchableDropdown from "@/components/ui/SearchableDropdown.vue";
import StatusButtonGroup from "@/components/ui/StatusButtonGroup.vue";
import SearchBar from "@/components/ui/SearchBar.vue";

defineProps({
  cityOptions: {
    type: Array,
    required: true,
  },
  districtOptions: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(["city-change", "district-change"]);

const cityId = defineModel("cityId");
const districtId = defineModel("districtId");
const status = defineModel("status");
const searchQuery = defineModel("searchQuery");

const isFiltered = computed(() =>
  cityId.value !== "all" ||
  districtId.value !== "all" ||
  status.value !== "all" ||
  searchQuery.value.trim() !== ""
);

const reset = () => {
  cityId.value = "all";
  districtId.value = "all";
  status.value = "all";
  searchQuery.value = "";
  emit("city-change");
};
</script>
