<template>
  <div class="mb-8">
    <hr
      v-if="showDivider"
      class="mx-auto mt-2 mb-7 w-60 border-t-8 border-dotted border-gray-300"
    />
    <h3 class="mb-2 pb-2 text-xl font-semibold">{{ districtName }} ({{ stores.length }})</h3>
    <!-- Empty state -->
    <div
      v-if="stores.length === 0"
      class="flex flex-col items-center justify-center py-10 text-center"
    >
      <SvgIcon name="icon-store-lookup" class="h-20 w-20" />
      <p class="text-sm text-gray-500">
        No supermarkets found in <span class="font-semibold">{{ districtName }}</span
        >.
      </p>
    </div>

    <div v-else class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
      <SupermarketCard
        v-for="supermarket in visibleStores"
        :key="supermarket.id"
        :supermarket="supermarket"
      />
    </div>
    <ShowMoreButton v-if="hasMore" :remaining="remaining" @click="showMore" />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import ShowMoreButton from '../ui/ShowMoreButton.vue'
import SupermarketCard from '@/components/supermarket/SupermarketCard.vue'

const props = defineProps({
  districtName: {
    type: String,
    required: true,
  },
  stores: {
    type: Array,
    required: true,
  },
  showDivider: {
    type: Boolean,
    default: false,
  },
})

const PAGE_SIZE = 8
const visibleCount = ref(PAGE_SIZE)

// Reset pagination whenever the stores list changes (e.g. filter applied)
watch(
  () => props.stores,
  () => {
    visibleCount.value = PAGE_SIZE
  }
)

const visibleStores = computed(() => props.stores.slice(0, visibleCount.value))
const hasMore = computed(() => props.stores.length > visibleCount.value)
const remaining = computed(() => props.stores.length - visibleCount.value)

const showMore = () => {
  visibleCount.value += PAGE_SIZE
}
</script>
