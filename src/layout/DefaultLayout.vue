<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import Header from '@/components/common/Header.vue';
import NavigationBar from '@/components/common/NavigationBar.vue';

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
</script>

<template>
  <div class="layout">
    <Header v-if="showHeader" />

    <main
      class="main-content"
      :class="{
        'with-header': showHeader,
        'with-navigation': showNavigation,
      }"
    >
      <slot></slot>
    </main>

    <NavigationBar v-if="showNavigation" />
  </div>
</template>

<style scoped>
.layout {
  width: 375px;
  min-height: 100vh;
  margin: 0 auto;
  position: relative;
  background-color: #fff;
}

.main-content {
  padding: 16px;
  box-sizing: border-box;
  min-height: 100vh;
}

.main-content.with-header {
  padding-top: 76px; /* 헤더 높이 + 여백 */
}

.main-content.with-navigation {
  padding-bottom: 96px; /* 네비게이션 바 높이 + 여백 */
}

.main-content.with-header.with-navigation {
  padding-top: 76px;
  padding-bottom: 96px;
}
</style>
