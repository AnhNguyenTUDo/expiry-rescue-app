<template>
  <div>
    <!-- Unified district loop -->
    <template v-for="section in districtSections" :key="section.key">
      <!-- City heading -->
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
import DistrictSection from '@/components/supermarket/DistrictSection.vue'

defineProps({
  districtSections: {
    type: Array,
    required: true,
  },
})
</script>
