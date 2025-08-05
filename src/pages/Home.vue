<script setup>
import ArbitarySectionOne from '@/components/domain/Home/ArbitarySectionOne.vue';
import ArbitarySectionTwo from '@/components/domain/Home/ArbitarySectionTwo.vue';
import styles from '@/assets/styles/pages/Home.module.css';
</script>
<template>
  <div :class="styles.page">
    <ArbitarySectionOne />
    <ArbitarySectionTwo />
  </div>
</template>
