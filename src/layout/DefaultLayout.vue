<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import Header from '@/components/common/Header.vue';
import NavigationBar from '@/components/common/NavigationBar.vue';
import styles from '@/assets/styles/layout/DefaultLayout.module.css';

const route = useRoute();

// 헤더를 표시할 페이지들 (네비게이션 바가 있는 메인 페이지들)
const showHeader = computed(() => {
  const mainPages = ['/home', '/policy', '/calendar', '/simulation', '/study'];
  return mainPages.includes(route.path);
});

// 네비게이션 바를 표시할 페이지들
const showNavigation = computed(() => {
  const mainPages = ['/home', '/policy', '/calendar', '/simulation', '/study'];
  return mainPages.includes(route.path);
});

// 동적 클래스 계산
const mainContentClass = computed(() => {
  const classes = [styles.mainContent];

  if (showHeader.value && showNavigation.value) {
    classes.push(styles.withHeaderAndNavigation);
  } else if (showHeader.value) {
    classes.push(styles.withHeader);
  } else if (showNavigation.value) {
    classes.push(styles.withNavigation);
  }

  return classes;
});
</script>

<template>
  <div :class="styles.layout">
    <Header v-if="showHeader" />

    <main :class="mainContentClass">
      <slot></slot>
    </main>

    <NavigationBar v-if="showNavigation" />
  </div>
</template>
