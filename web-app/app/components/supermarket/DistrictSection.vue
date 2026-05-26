<template>
  <div class="mb-8">
    <hr
      v-if="showDivider"
      class="w-60 border-t-8 border-dotted border-gray-300 mx-auto mb-7 mt-2"
    />
    <h3 class="text-xl font-semibold mb-2 pb-2">{{ districtName }} ({{ stores.length }})</h3>
    <!-- Empty state -->
    <div
      v-if="stores.length === 0"
      class="flex flex-col items-center justify-center py-10 text-center"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="5em"
        height="5em"
        viewBox="0 0 24 24"
        class="mb-3"
      >
        <path d="M0 0h24v24H0z" fill="none" />
        <path
          fill="#5ce259"
          d="M18 4H2V2h16zm-4.96 6h-10l.6-3h12.72l.4 2c.74.04 1.45.19 2.13.46L18 5H2l-1 5v2h1v6h8.5c-.33-.8-.5-1.65-.5-2.5v.5H4v-4h6v3.5c0-1.66.64-3.33 1.9-4.6q.54-.525 1.14-.9m10.35 11L22 22.39l-3.12-3.07c-.69.43-1.51.68-2.38.68c-2.5 0-4.5-2-4.5-4.5s2-4.5 4.5-4.5s4.5 2 4.5 4.5c0 .88-.25 1.71-.69 2.4zM19 15.5a2.5 2.5 0 0 0-5 0a2.5 2.5 0 0 0 5 0"
        />
      </svg>
      <p class="text-gray-500 text-sm">
        No supermarkets found in <span class="font-semibold">{{ districtName }}</span
        >.
      </p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <SupermarketCard
        v-for="supermarket in visibleStores"
        :key="supermarket.id"
        :supermarket="supermarket"
      />
    </div>
    <div v-if="hasMore" class="mt-6 text-center">
      <button
        @click="showMore"
        class="px-6 py-2 rounded-xl border-2 border-gray-600 text-gray-600 font-medium transition-transform duration-300 hover:-translate-y-0.5 hover:shadow-md cursor-pointer active:scale-95"
      >
        Show more
        <span class="text-sm opacity-70">({{ remaining }} more)</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import SupermarketCard from "@/components/supermarket/SupermarketCard.vue";

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
});

const PAGE_SIZE = 8;
const visibleCount = ref(PAGE_SIZE);

// Reset pagination whenever the stores list changes (e.g. filter applied)
watch(
  () => props.stores,
  () => {
    visibleCount.value = PAGE_SIZE;
  }
);

const visibleStores = computed(() => props.stores.slice(0, visibleCount.value));
const hasMore = computed(() => props.stores.length > visibleCount.value);
const remaining = computed(() => props.stores.length - visibleCount.value);

const showMore = () => {
  visibleCount.value += PAGE_SIZE;
};
</script>
