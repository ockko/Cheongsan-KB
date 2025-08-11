<script setup>
import { RouterView } from 'vue-router';
import DefaultLayout from './layout/DefaultLayout.vue';
import BaseModal from '@/components/common/BaseModal.vue';
</script>

<template>
  <DefaultLayout>
    <RouterView />
    <BaseModal />
  </DefaultLayout>
</template>

<style scoped></style>
