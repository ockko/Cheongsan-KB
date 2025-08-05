<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import styles from '@/assets/styles/components/Header.module.css';

const route = useRoute();

// 라우트 이름을 기반으로 헤더 타이틀 결정
const headerTitle = computed(() => {
  const routeName = route.name;
  if (routeName) {
    return routeName;
  }
  // 라우트 이름이 없을 경우 경로에서 추출
  const pathSegments = route.path.split('/').filter(Boolean);
  if (pathSegments.length > 0) {
    const lastSegment = pathSegments[pathSegments.length - 1];
    return lastSegment.charAt(0).toUpperCase() + lastSegment.slice(1);
  }
  return 'Home';
});
</script>

<template>
  <header :class="styles.header">
    <!-- 좌측: 로고와 현재 페이지 타이틀 -->
    <div :class="styles.headerLeft">
      <img
        src="/images/logo-blue.png"
        alt="티끌모아 청산 로고"
        :class="styles.headerLogo"
      />
      <span :class="[styles.headerTitle, 'text-bold']">{{ headerTitle }}</span>
    </div>

    <!-- 우측: 아이콘들 -->
    <div :class="styles.headerRight">
      <button :class="styles.headerIconBtn" type="button">
        <img
          src="/images/refresh-icon-blue.png"
          alt="새로고침"
          :class="styles.headerIcon"
        />
      </button>

      <button :class="styles.headerIconBtn" type="button">
        <img
          src="/images/notification-icon-blue.png"
          alt="알림"
          :class="styles.headerIcon"
        />
      </button>

      <button :class="styles.headerIconBtn" type="button">
        <img
          src="/images/user-icon-blue.png"
          alt="사용자"
          :class="styles.headerIcon"
        />
      </button>
    </div>
  </header>
</template>
