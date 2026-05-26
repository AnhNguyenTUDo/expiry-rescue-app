import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useSupermarketStore = defineStore("supermarket", () => {
  // State
  const supermarkets = ref([]);
  const selectedSupermarketId = ref("");

  // Getters
  const currentSupermarket = computed(() => {
    if (!selectedSupermarketId.value) return null;
    return supermarkets.value.find((s) => s.id === selectedSupermarketId.value);
  });

  const currentSupermarketName = computed(() => {
    if (!currentSupermarket.value) return null;
    const { name, districtName } = currentSupermarket.value;
    return districtName ? `${name} - ${districtName}` : name;
  });

  // Actions
  function setSupermarkets(data) {
    supermarkets.value = data;
  }

  function setSelectedSupermarketId(id) {
    selectedSupermarketId.value = id;
  }

  function clearSelectedSupermarket() {
    selectedSupermarketId.value = "";
  }

  return {
    // State
    supermarkets,
    selectedSupermarketId,
    // Getters
    currentSupermarket,
    currentSupermarketName,
    // Actions
    setSupermarkets,
    setSelectedSupermarketId,
    clearSelectedSupermarket,
  };
});
