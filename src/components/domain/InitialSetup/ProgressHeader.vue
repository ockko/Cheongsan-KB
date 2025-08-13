<script setup>
import styles from '@/assets/styles/components/InitialSetup/ProgressHeader.module.css';
import { computed } from 'vue';

const props = defineProps({
  current: Number,
  total: Number,
});

const percent = computed(() => (100 * props.current) / props.total);
</script>

<template>
  <div :class="styles.progressHeader">
    <div :class="styles.progressBarWrapper">
      <div
        :class="[styles.progressBar, 'color-main']"
        :style="{ width: percent + '%' }"
      ></div>
    </div>
    <p
      v-if="current !== 0 && total !== 0"
      :class="[styles.stepText, 'color-main']"
    >
      {{ current }} / {{ total }}
    </p>
  </div>
</template>
