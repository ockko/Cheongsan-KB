<script setup>
import { RouterView, useRoute } from 'vue-router';
import DefaultLayout from './layout/DefaultLayout.vue';

const route = useRoute();
const isStartupPage = route.name === 'Startup';
</script>

<template>
  <DefaultLayout v-if="!isStartupPage">
    <RouterView />
  </DefaultLayout>
  <RouterView v-else />
</template>

<style scoped></style>
