<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useModalStore } from '@/stores/modal';
import Header from '@/components/common/Header.vue';
import NavigationBar from '@/components/common/NavigationBar.vue';
import styles from '@/assets/styles/layout/DefaultLayout.module.css';

const route = useRoute();
const modalStore = useModalStore();

// 헤더를 표시할 페이지들 (네비게이션 바가 있는 메인 페이지들)
const showHeader = computed(() => {
  const mainPages = [
    '/home',
    '/policy',
    '/calendar',
    '/simulation',
    '/study',
    '/study/detail',
  ];
  return mainPages.includes(route.path);
});

// 네비게이션 바를 표시할 페이지들 (모달이 열려있어도 표시)
const showNavigation = computed(() => {
  const mainPages = [
    '/home',
    '/policy',
    '/calendar',
    '/simulation',
    '/study',
    '/study/detail',
  ];
  
  // 메인 페이지에 있거나 모달이 열려있으면 네비게이션 바 표시
  return mainPages.includes(route.path) || modalStore.isAnyModalOpen;
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
