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
const showNotificationPopup = ref(false);
const userPopupRef = ref(null);
const notificationPopupRef = ref(null);
const isRefreshing = ref(false);

// 알림 관련 상태
const notifications = ref([]);
const unreadCount = ref(0);
const isLoadingNotifications = ref(false);

// 웹소켓 관련 상태
const websocket = ref(null);
const isWebSocketConnected = ref(false);

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

// 웹소켓 연결 함수
const connectWebSocket = () => {
  const user = authStore.getUser();
  if (!user?.id) {
    console.log('사용자 정보가 없어 WebSocket 연결을 건너뜁니다.');
    return;
  }

  // 기존 연결이 있으면 종료
  if (websocket.value) {
    websocket.value.close();
  }

  // 환경에 따라 WebSocket URL 설정 (개발/운영 환경 고려)
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  const host = import.meta.env.VITE_API_BASE_URL
    ? import.meta.env.VITE_API_BASE_URL.replace(/^https?:\/\//, '').replace(
        /\/$/,
        ''
      )
    : window.location.host;

  const wsUrl = `${protocol}//${host}/ws/notifications?userId=${user.id}`;

  console.log('WebSocket 연결 시도:', wsUrl);

  websocket.value = new WebSocket(wsUrl);

  websocket.value.onopen = () => {
    console.log('✅ WebSocket 연결 성공');
    isWebSocketConnected.value = true;
  };

  websocket.value.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);
      console.log('📨 WebSocket 메시지 수신:', data);

      if (data.type === 'notification') {
        // 새 알림 수신
        console.log('🔔 새 알림:', data.contents);

        // 읽지 않은 알림 개수 업데이트
        if (data.unreadCount !== undefined) {
          unreadCount.value = data.unreadCount;
        } else {
          unreadCount.value = unreadCount.value + 1;
        }

        // 알림 팝업이 열려있으면 목록 새로고침
        if (showNotificationPopup.value) {
          fetchNotifications();
        }

        // 브라우저 알림 표시 (권한이 있는 경우)
        if (Notification.permission === 'granted') {
          new Notification('새 알림 도착!', {
            body: data.contents,
            icon: '/images/logo-blue.png',
            tag: 'cheongsan-notification',
          });
        }
      } else if (data.type === 'unreadCount') {
        // 읽지 않은 알림 개수 업데이트
        console.log('📊 읽지 않은 알림 개수 업데이트:', data.unreadCount);
        unreadCount.value = data.unreadCount;
      }
    } catch (error) {
      console.error('❌ WebSocket 메시지 파싱 실패:', error);
    }
  };

  websocket.value.onerror = (error) => {
    console.error('❌ WebSocket 오류:', error);
    isWebSocketConnected.value = false;
  };

  websocket.value.onclose = (event) => {
    console.log('🔌 WebSocket 연결 종료:', event.code, event.reason);
    isWebSocketConnected.value = false;

    // 정상적인 종료가 아니고 사용자가 로그인된 상태라면 재연결 시도
    if (event.code !== 1000 && authStore.isLoggedIn()) {
      console.log('⏰ 5초 후 WebSocket 재연결 시도...');
      setTimeout(() => {
        if (authStore.isLoggedIn()) {
          connectWebSocket();
        }
      }, 5000);
    }
  };
};

// 웹소켓 연결 해제
const disconnectWebSocket = () => {
  if (websocket.value) {
    console.log('🔌 WebSocket 연결 해제');
    websocket.value.close();
    websocket.value = null;
    isWebSocketConnected.value = false;
  }
};

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

// 로그아웃 처리
const handleLogout = async () => {
  try {
    // 웹소켓 연결 해제
    disconnectWebSocket();

    // 서버에 로그아웃 요청
    await request.post('/cheongsan/user/logout');
  } catch (error) {
    console.error('로그아웃 요청 실패:', error);
  } finally {
    // 클라이언트 상태 정리
    authStore.logout();
    showUserPopup.value = false;
    // 로그인 페이지로 리다이렉트
    router.push('/login');
  }
};

// 새로고침 CODEF 핸들러
const handleRefresh = async () => {
  if (isRefreshing.value) return; // 이미 진행 중이면 중복 실행 방지

  try {
    isRefreshing.value = true;
    console.log('계좌 데이터 동기화 시작...');

    // Codef 동기화 API 호출
    const response = await request.post('/cheongsan/mydata/sync');

    console.log('계좌 데이터 동기화 완료:', response.data);

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

// 알림 관련 함수들
// 읽지 않은 알림 개수 조회
const fetchUnreadCount = async () => {
  try {
    const response = await request.get('/cheongsan/notifications/unread');
    unreadCount.value = response.data.unreadCount;
  } catch (error) {
    console.error('읽지 않은 알림 개수 조회 실패:', error);
  }
};

// 알림 목록 조회
const fetchNotifications = async () => {
  try {
    isLoadingNotifications.value = true;
    const response = await request.get('/cheongsan/notifications');
    notifications.value = response.data;
  } catch (error) {
    console.error('알림 목록 조회 실패:', error);
  } finally {
    isLoadingNotifications.value = false;
  }
};

// 알림 아이콘 클릭 핸들러
const handleNotification = async () => {
  showNotificationPopup.value = !showNotificationPopup.value;

  // 사용자 팝업이 열려있으면 닫기
  if (showNotificationPopup.value) {
    showUserPopup.value = false;
    await fetchNotifications();
  }
};

// 모든 알림 읽음 처리 (WebSocket을 통해 자동으로 개수 업데이트됨)
const markAllAsRead = async () => {
  try {
    await request.patch('/cheongsan/notifications/readAll');

    // WebSocket이 연결되어 있지 않은 경우에만 로컬 상태 업데이트
    if (!isWebSocketConnected.value) {
      unreadCount.value = 0;
      // 알림 목록의 읽음 상태 업데이트
      notifications.value = notifications.value.map((notification) => ({
        ...notification,
        isRead: true,
      }));
    }
  } catch (error) {
    console.error('알림 읽음 처리 실패:', error);
  }
};

// 날짜 포맷 함수
const formatDate = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = Math.abs(now - date);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays === 1) {
    return '오늘';
  } else if (diffDays === 2) {
    return '어제';
  } else {
    return `${date.getMonth() + 1}. ${date.getDate()}. ${
      ['일', '월', '화', '수', '목', '금', '토'][date.getDay()]
    }`;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);

  // 컴포넌트 마운트 시 읽지 않은 알림 개수 조회
  fetchUnreadCount();

  // 웹소켓 연결
  connectWebSocket();

  // 브라우저 알림 권한 요청
  if (Notification.permission === 'default') {
    Notification.requestPermission().then((permission) => {
      if (permission === 'granted') {
        console.log('✅ 브라우저 알림 권한 허용됨');
      } else {
        console.log('❌ 브라우저 알림 권한 거부됨');
      }
    });
  }

  // 주기적으로 읽지 않은 알림 개수 업데이트 (30초마다)
  // WebSocket이 연결되어 있으면 폴링 간격을 늘림
  const pollingInterval = isWebSocketConnected.value ? 60000 : 30000;
  const interval = setInterval(() => {
    // WebSocket이 연결되어 있지 않은 경우에만 폴링
    if (!isWebSocketConnected.value) {
      fetchUnreadCount();
    }
  }, pollingInterval);

  onUnmounted(() => {
    clearInterval(interval);
    disconnectWebSocket();
  });
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
  disconnectWebSocket();
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
          :title="`알림${isWebSocketConnected ? ' (실시간)' : ''}`"
        >
          <div :class="styles.notificationIconWrapper">
            <img
              src="/images/notification-icon-blue.png"
              alt="알림"
              :class="styles.headerIcon"
            />
            <!-- 알림 배지 -->
            <span v-if="unreadCount > 0" :class="styles.notificationBadge">
              {{ unreadCount > 99 ? '99+' : unreadCount }}
            </span>
          </div>
        </button>

        <!-- 알림 팝업 -->
        <div v-if="showNotificationPopup" :class="styles.notificationPopup">
          <!-- 알림 헤더 -->
          <div :class="styles.notificationHeader">
            <h3 :class="styles.notificationTitle">
              알림
              <!-- WebSocket 연결 상태 표시 (개발용) -->
              <span
                v-if="!isWebSocketConnected"
                style="color: orange; font-size: 12px"
              >
                (오프라인)
              </span>
            </h3>
            <button
              v-if="unreadCount > 0"
              :class="styles.markAllReadBtn"
              @click="markAllAsRead"
            >
              모두 읽음
            </button>
          </div>

          <!-- 알림 목록 -->
          <div :class="styles.notificationList">
            <!-- 로딩 상태 -->
            <div v-if="isLoadingNotifications" :class="styles.loadingMessage">
              알림을 불러오는 중...
            </div>

            <!-- 알림이 없는 경우 -->
            <div
              v-else-if="notifications.length === 0"
              :class="styles.emptyMessage"
            >
              새로운 알림이 없습니다.
            </div>

            <!-- 알림 목록 -->
            <div
              v-else
              v-for="notification in notifications"
              :key="notification.id"
              :class="[
                styles.notificationItem,
                !notification.isRead ? styles.unread : '',
              ]"
            >
              <div :class="styles.notificationDate">
                {{ formatDate(notification.createdAt) }}
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
