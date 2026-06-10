<template>
  <div class="rounded-xl bg-white p-12 text-center shadow">
    <SvgIcon name="icon-cart-empty" class="mx-auto mb-4 block h-20 w-20 text-gray-400" />
    <h2 class="mb-2 text-2xl font-semibold text-gray-700">Your cart is empty</h2>
    <p class="mb-6 text-gray-500">Grab soon-to-expire deals before the clock runs out.</p>
    <NuxtLink
      to="/"
      class="inline-block rounded-[11px] bg-green-700 px-6 py-3 font-semibold text-white transition hover:bg-green-800"
    >
      Continue shopping
    </NuxtLink>
  </div>
</template>
