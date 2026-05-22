<template>
  <div class="mb-8">
    <hr v-if="showDivider" class="w-60 border-t-8 border-dotted border-gray-300 mx-auto mb-7 mt-2" />
    <h3 class="text-xl font-semibold mb-2 pb-2">
      {{ districtName }} ({{ stores.length }})
    </h3>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <SupermarketCard
        v-for="supermarket in visibleStores"
        :key="supermarket.id"
        :supermarket="supermarket"
      />
    </div>
    <div v-if="hasMore" class="mt-6 text-center">
      <button @click="showMore" class="
        px-6 py-2 rounded-xl border-2 border-emerald-500 text-emerald-600 font-medium
        transition-transform duration-300 hover:-translate-y-0.5 hover:shadow-md cursor-pointer
        active:scale-95">
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
