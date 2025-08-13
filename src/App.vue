<script setup>
import { RouterView, useRoute } from 'vue-router';
import DefaultLayout from './layout/DefaultLayout.vue';
import BaseModal from '@/components/common/BaseModal.vue';

const route = useRoute();
const isStartupPage = route.name === 'Startup';
</script>

<template>
  <DefaultLayout v-if="!isStartupPage">
    <RouterView />
    <BaseModal />
  </DefaultLayout>
  <RouterView v-else />
</template>

<style scoped></style>
