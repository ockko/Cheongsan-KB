<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: Number,
  total: Number,
})

const percent = computed(() => (100 * props.current) / props.total)
</script>

<template>
    <div class="progress-header">
      <div class="progress-bar-wrapper">
        <div class="progress-bar color-main" :style="{ width: percent + '%' }"></div>
      </div>
      <p class="step-text color-main">{{ current }} / {{ total }}</p>
    </div>
  </template>
  
  <style scoped>
  .progress-header {
    padding: 1rem 0;
    text-align: center;
  }
  
  .progress-bar-wrapper {
    height: 10px;
    background-color: gray;
  }
  
  .progress-bar {
    height: 10px;
    transition: width 0.3s;
    background-color: navy;
  }

  .step-text {
    font-size: 22px;
    margin-top: 1.5rem;
    font-weight: 500;
    color: #00497a;
  }
  </style>
  