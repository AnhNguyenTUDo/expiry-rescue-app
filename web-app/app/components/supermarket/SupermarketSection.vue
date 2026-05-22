<template>
  <div>
    <!-- Single district: empty state -->
    <div
      v-if="selectedDistrictId !== 'all' && hasLoadedData && districtSections.length === 0"
      class="flex flex-col items-center justify-center py-16 text-center"
    >
      <div class="text-4xl mb-3">
        <svg xmlns="http://www.w3.org/2000/svg" width="2.5em" height="2.5em" viewBox="0 0 24 24">
          <path d="M0 0h24v24H0z" fill="none" />
          <path fill="#5ce259" d="M18 4H2V2h16zm-4.96 6h-10l.6-3h12.72l.4 2c.74.04 1.45.19 2.13.46L18 5H2l-1 5v2h1v6h8.5c-.33-.8-.5-1.65-.5-2.5v.5H4v-4h6v3.5c0-1.66.64-3.33 1.9-4.6q.54-.525 1.14-.9m10.35 11L22 22.39l-3.12-3.07c-.69.43-1.51.68-2.38.68c-2.5 0-4.5-2-4.5-4.5s2-4.5 4.5-4.5s4.5 2 4.5 4.5c0 .88-.25 1.71-.69 2.4zM19 15.5a2.5 2.5 0 0 0-5 0a2.5 2.5 0 0 0 5 0" />
        </svg>
      </div>
      <p class="text-gray-700 font-medium">
        No <span class="font-semibold">{{ selectedStatus }}</span> supermarkets in
        <span class="font-semibold">{{ currentDistrictLabel }}</span>.
      </p>
      <p class="text-sm text-gray-500 mt-1">Try switching to <strong>All</strong> to see all stores.</p>
    </div>

    <!-- All districts: empty state (status filter has no matches) -->
    <div
      v-else-if="selectedDistrictId === 'all' && selectedStatus !== 'all' && hasLoadedData && districtSections.length === 0"
      class="flex flex-col items-center justify-center py-16 text-center"
    >
      <div class="text-4xl mb-3">
        <svg xmlns="http://www.w3.org/2000/svg" width="2.5em" height="2.5em" viewBox="0 0 24 24">
          <path d="M0 0h24v24H0z" fill="none" />
          <path fill="#5ce259" d="M18 4H2V2h16zm-4.96 6h-10l.6-3h12.72l.4 2c.74.04 1.45.19 2.13.46L18 5H2l-1 5v2h1v6h8.5c-.33-.8-.5-1.65-.5-2.5v.5H4v-4h6v3.5c0-1.66.64-3.33 1.9-4.6q.54-.525 1.14-.9m10.35 11L22 22.39l-3.12-3.07c-.69.43-1.51.68-2.38.68c-2.5 0-4.5-2-4.5-4.5s2-4.5 4.5-4.5s4.5 2 4.5 4.5c0 .88-.25 1.71-.69 2.4zM19 15.5a2.5 2.5 0 0 0-5 0a2.5 2.5 0 0 0 5 0" />
        </svg>
      </div>
      <p class="text-gray-700 font-medium">
        No <span class="font-semibold">{{ selectedStatus }}</span> supermarkets right now.
      </p>
      <p class="text-sm text-gray-500 mt-1">Try switching to <strong>All</strong> to see all stores.</p>
    </div>

    <!-- Unified district loop -->
    <template v-for="section in districtSections" :key="section.key">
      <!-- City heading (only in all-cities view) -->
      <h2
        v-if="section.type === 'city-heading'"
        class="text-2xl font-bold mb-4 pb-2 border-b-2 border-emerald-500 inline-block"
        :class="section.isFirst ? '' : 'mt-8'"
      >
        {{ section.label }}
      </h2>

      <!-- District block -->
      <DistrictSection
        v-else
        :district-name="section.districtName"
        :stores="section.stores"
        :show-divider="section.showDivider"
      />
    </template>
  </div>
</template>

<script setup>
import DistrictSection from "@/components/supermarket/DistrictSection.vue";

defineProps({
  districtSections: {
    type: Array,
    required: true,
  },
  selectedDistrictId: {
    type: String,
    required: true,
  },
  selectedStatus: {
    type: String,
    required: true,
  },
  currentDistrictLabel: {
    type: String,
    default: "",
  },
  hasLoadedData: {
    type: Boolean,
    default: false,
  },
});
</script>
