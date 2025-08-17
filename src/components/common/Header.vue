<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useNotificationStore } from '@/stores/notification';
import { useWebSocketStore } from '@/stores/websocket';
import { mydataApi } from '@/api/mydata';
import { useMyPageStore } from '@/stores/mypage';
import styles from '@/assets/styles/components/Header.module.css';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const webSocketStore = useWebSocketStore();
const store = useMyPageStore();

// 팝업 상태 관리
const showUserPopup = ref(false);
const showNotificationPopup = ref(false);
const userPopupRef = ref(null);
const notificationPopupRef = ref(null);
const isRefreshing = ref(false);

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

// 로고 클릭 시 페이지 상단으로 스크롤하는 함수
const scrollToTop = () => {
  // 여러 방법을 시도해서 확실하게 스크롤

  // 방법 1: window.scrollTo 사용
  window.scrollTo({
    top: 0,
    left: 0,
    behavior: 'smooth',
  });

  // 방법 2: document.documentElement.scrollTop 사용 (백업)
  setTimeout(() => {
    document.documentElement.scrollTop = 0;
    document.body.scrollTop = 0; // Safari 호환성
  }, 100);

  // 방법 3: Vue Router의 scrollBehavior 트리거 (백업의 백업)
  setTimeout(() => {
    if (window.scrollY > 0) {
      window.scrollTo(0, 0);
    }
  }, 200);
};

// 사용자 이름 가져오기
const userName = computed(() => {
  return authStore.getUser()?.nickName || '사용자';
});

// 사용자 아이콘 클릭 핸들러
const toggleUserPopup = () => {
  showUserPopup.value = !showUserPopup.value;
  // 알림 팝업이 열려있으면 닫기
  if (showUserPopup.value) {
    showNotificationPopup.value = false;
  }
};

// 외부 클릭 시 팝업 닫기
const handleClickOutside = (event) => {
  if (userPopupRef.value && !userPopupRef.value.contains(event.target)) {
    showUserPopup.value = false;
  }
  if (
    notificationPopupRef.value &&
    !notificationPopupRef.value.contains(event.target)
  ) {
    showNotificationPopup.value = false;
  }
};

// 내 정보 관리 (향후 구현)
const goToMyInfo = () => {
  showUserPopup.value = false;
  router.push('/mypage');
};

// 로그아웃 처리 - mypageApi 사용으로 수정
const handleLogout = async () => {
  await store.logout();
  router.push('/login'); // 로그아웃 후 로그인 페이지로 이동
};

// 새로고침 CODEF 핸들러
const handleRefresh = async () => {
  if (isRefreshing.value) return; // 이미 진행 중이면 중복 실행 방지

  try {
    isRefreshing.value = true;

    // Codef 동기화 API 호출
    const response = await mydataApi.syncAccountData();

    console.log('계좌 데이터 동기화 완료:');

    // 동기화 완료 후 페이지 새로고침
    window.location.reload();
  } catch (error) {
    console.error('계좌 데이터 동기화 실패:', error);

    // 에러 메시지 표시 (선택사항)
    alert('계좌 데이터 동기화에 실패했습니다. 페이지를 새로고침합니다.');

    // 에러가 발생해도 페이지는 새로고침
    window.location.reload();
  } finally {
    isRefreshing.value = false;
  }
};

// 알림 아이콘 클릭 핸들러
const handleNotification = async () => {
  showNotificationPopup.value = !showNotificationPopup.value;

  // 사용자 팝업이 열려있으면 닫기
  if (showNotificationPopup.value) {
    showUserPopup.value = false;
    await notificationStore.fetchNotifications();
    await notificationStore.fetchUnreadCount();
  }
};

// 모든 알림 읽음 처리
const handleMarkAllAsRead = async () => {
  try {
    await notificationStore.markAllAsRead();
  } catch (error) {
    console.error('알림 읽음 처리 실패:', error);
  }
};

// 알림 팝업에서 새로고침 핸들러 추가
const handleNotificationRefresh = () => {
  if (showNotificationPopup.value) {
    notificationStore.fetchNotifications();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);

  // 알림 스토어 초기화
  notificationStore.initialize();

  // 웹소켓 연결
  webSocketStore.connect();

  // WebSocket에서 알림 팝업이 열려있을 때 새로고침 이벤트 처리
  webSocketStore.on('notification', handleNotificationRefresh);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);

  // 웹소켓 연결 해제
  webSocketStore.disconnect();

  // 알림 스토어 정리
  notificationStore.cleanup();

  // 이벤트 핸들러 제거
  webSocketStore.off('notification', handleNotificationRefresh);
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
        @click="scrollToTop"
        style="cursor: pointer"
      />
      <span :class="[styles.headerTitle, 'text-bold']">{{ headerTitle }}</span>
    </div>

    <!-- 우측: 아이콘들 -->
    <div :class="styles.headerRight">
      <button
        :class="styles.headerIconBtn"
        type="button"
        @click="handleRefresh"
        :disabled="isRefreshing"
        :title="isRefreshing ? '동기화 중...' : '새로고침'"
      >
        <img
          src="/images/refresh-icon-blue.png"
          alt="새로고침"
          :class="[styles.headerIcon, isRefreshing ? styles.spinning : '']"
        />
      </button>

      <!-- 알림 아이콘과 팝업 -->
      <div :class="styles.notificationContainer" ref="notificationPopupRef">
        <button
          :class="styles.headerIconBtn"
          type="button"
          @click="handleNotification"
          :title="`알림${webSocketStore.isConnected ? ' (실시간)' : ''}`"
        >
          <div :class="styles.notificationIconWrapper">
            <img
              src="/images/notification-icon-blue.png"
              alt="알림"
              :class="styles.headerIcon"
            />
            <!-- 알림 배지 -->
            <span
              v-if="notificationStore.hasUnreadNotifications"
              :class="styles.notificationBadge"
            >
              {{ notificationStore.unreadBadgeText }}
            </span>
          </div>
        </button>

        <!-- 알림 팝업 -->
        <div v-if="showNotificationPopup" :class="styles.notificationPopup">
          <!-- 알림 헤더 -->
          <div :class="styles.notificationHeader">
            <h3 :class="styles.notificationTitle">
              Notice
              <!-- WebSocket 연결 상태 표시 (개발용) -->
              <!-- <span
                v-if="!webSocketStore.isConnected"
                style="color: orange; font-size: 12px"
              >
                (오프라인)
              </span> -->
            </h3>
            <button
              v-if="notificationStore.hasUnreadNotifications"
              :class="styles.markAllReadBtn"
              @click="handleMarkAllAsRead"
            >
              모두 읽음
            </button>
          </div>

          <!-- 알림 목록 -->
          <div :class="styles.notificationList">
            <!-- 로딩 상태 -->
            <div
              v-if="notificationStore.isLoading"
              :class="styles.loadingMessage"
            >
              알림을 불러오는 중...
            </div>

            <!-- 알림이 없는 경우 -->
            <div
              v-else-if="notificationStore.notifications.length === 0"
              :class="styles.emptyMessage"
            >
              새로운 알림이 없습니다.
            </div>

            <!-- 알림 목록 -->
            <div
              v-else
              v-for="notification in notificationStore.notifications"
              :key="notification.id"
              :class="[
                styles.notificationItem,
                !notification.isRead ? styles.unread : '',
              ]"
            >
              <div :class="styles.notificationDate">
                {{ notificationStore.formatDate(notification.createdAt) }}
              </div>
              <div :class="styles.notificationContent">
                {{ notification.contents }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 사용자 아이콘과 팝업 -->
      <div :class="styles.userMenuContainer" ref="userPopupRef">
        <button
          :class="styles.headerIconBtn"
          type="button"
          @click="toggleUserPopup"
          title="사용자 메뉴"
        >
          <img
            :src="
              showUserPopup
                ? '/images/user-colored-icon.png'
                : '/images/user-icon.png'
            "
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
