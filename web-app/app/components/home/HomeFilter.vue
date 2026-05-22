<template>
  <div class="relative mb-6 pb-4">
    <div class="flex items-center justify-end gap-3 mb-2 flex-wrap">
      <!-- City dropdown -->
      <DropdownSelect
        v-model="cityId"
        :options="cityOptions"
        @change="emit('city-change')"
      />

      <!-- District dropdown (depends on city) -->
      <DropdownSelect
        v-model="districtId"
        :options="districtOptions"
        @change="emit('district-change')"
      />

      <!-- Open/Closed status filter pills -->
      <StatusButtonGroup v-model="status" />
    </div>
  </div>
</template>

<script setup>
import DropdownSelect from "@/components/ui/DropdownSelect.vue";
import StatusButtonGroup from "@/components/ui/StatusButtonGroup.vue";

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

// Two-way bindings for the three filter values
const cityId = defineModel("cityId");
const districtId = defineModel("districtId");
const status = defineModel("status");
</script>
