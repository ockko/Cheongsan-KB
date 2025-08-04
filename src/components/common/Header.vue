<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

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
  <header class="header">
    <!-- 좌측: 로고와 현재 페이지 타이틀 -->
    <div class="header-left">
      <img
        src="/images/logo-blue.png"
        alt="티끌모아 청산 로고"
        class="header-logo"
      />
      <span class="header-title text-bold">{{ headerTitle }}</span>
    </div>

    <!-- 우측: 아이콘들 -->
    <div class="header-right">
      <button class="header-icon-btn" type="button">
        <img
          src="/images/refresh-icon-blue.png"
          alt="새로고침"
          class="header-icon"
        />
      </button>

      <button class="header-icon-btn" type="button">
        <img
          src="/images/notification-icon-blue.png"
          alt="알림"
          class="header-icon"
        />
      </button>

      <button class="header-icon-btn" type="button">
        <img
          src="/images/user-icon-blue.png"
          alt="사용자"
          class="header-icon"
        />
      </button>
    </div>
  </header>
</template>

<style scoped>
.header {
  width: 343px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 16px 16px 12px 16px;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-logo {
  width: 45.31px;
  height: 45.31px;
  object-fit: contain;
}

.header-title {
  color: var(--color-main);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon {
  width: 30px;
  height: 30px;
  object-fit: contain;
}
</style>
