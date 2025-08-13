<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import request from '@/api/index';
import styles from '@/assets/styles/components/Header.module.css';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// 팝업 상태 관리
const showUserPopup = ref(false);
const userPopupRef = ref(null);

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

// 사용자 이름 가져오기
const userName = computed(() => {
  return authStore.getUser()?.nickName || '사용자';
});

// 사용자 아이콘 클릭 핸들러
const toggleUserPopup = () => {
  showUserPopup.value = !showUserPopup.value;
};

// 외부 클릭 시 팝업 닫기
const handleClickOutside = (event) => {
  if (userPopupRef.value && !userPopupRef.value.contains(event.target)) {
    showUserPopup.value = false;
  }
};

// 내 정보 관리 (향후 구현)
const goToMyInfo = () => {
  showUserPopup.value = false;
  // TODO: 내 정보 관리 기능 구현
  console.log('내 정보 관리 클릭');
};

// 로그아웃 처리
const handleLogout = async () => {
  try {
    // 서버에 로그아웃 요청
    await request.post('/cheongsan/user/logout');
  } catch (error) {
    console.error('로그아웃 요청 실패:', error);
    // 서버 요청이 실패해도 클라이언트 로그아웃은 진행
  } finally {
    // 클라이언트 상태 정리
    authStore.logout();
    showUserPopup.value = false;
    // 로그인 페이지로 리다이렉트
    router.push('/login');
  }
};

// 새로고침 핸들러
const handleRefresh = () => {
  window.location.reload();
};

// 알림 핸들러 (향후 구현)
const handleNotification = () => {
  // TODO: 알림 기능 구현
  console.log('알림 버튼 클릭');
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
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
      <button
        :class="styles.headerIconBtn"
        type="button"
        @click="handleRefresh"
        title="새로고침"
      >
        <img
          src="/images/refresh-icon-blue.png"
          alt="새로고침"
          :class="styles.headerIcon"
        />
      </button>

      <button
        :class="styles.headerIconBtn"
        type="button"
        @click="handleNotification"
        title="알림"
      >
        <img
          src="/images/notification-icon-blue.png"
          alt="알림"
          :class="styles.headerIcon"
        />
      </button>

      <!-- 사용자 아이콘과 팝업 -->
      <div :class="styles.userMenuContainer" ref="userPopupRef">
        <button
          :class="styles.headerIconBtn"
          type="button"
          @click="toggleUserPopup"
          title="사용자 메뉴"
        >
          <img
            src="/images/user-icon-blue.png"
            alt="사용자"
            :class="styles.headerIcon"
          />
        </button>

        <!-- 사용자 팝업 메뉴 -->
        <div v-if="showUserPopup" :class="styles.userPopup">
          <div :class="styles.userInfo">
            <div :class="styles.userName">
              {{ userName }}
              <div
                :class="styles.greeting"
                style="margin-top: 5px; margin-left: 5px"
              >
                님,
              </div>
            </div>
            <div :class="styles.greeting">안녕하세요.</div>
          </div>

          <hr :class="styles.popupDivider" />

          <div :class="styles.menuItems">
            <div :class="styles.greeting" style="margin-left: 27px">
              마이페이지
            </div>
            <button :class="styles.menuItem" @click="goToMyInfo">
              내 정보 관리
            </button>

            <button :class="styles.logoutButton" @click="handleLogout">
              로그아웃
            </button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>
