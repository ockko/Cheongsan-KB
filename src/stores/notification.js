import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { notificationApi } from '@/api/notifications';
import { useWebSocketStore } from '@/stores/websocket';

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([]);
  const unreadCount = ref(0);
  const isLoading = ref(false);
  const isPolling = ref(false);
  const pollingInterval = ref(null);

  // WebSocket 스토어
  const webSocketStore = useWebSocketStore();

  // 성능 최적화를 위한 상태
  let lastFetchTime = 0;
  let isPageVisible = true;
  let testModeActive = false;
  const FETCH_THROTTLE_MS = 1000; // 1초 내 중복 요청 방지

  /**
   * Throttled 읽지 않은 알림 개수 조회 (중복 요청 방지)
   */
  const fetchUnreadCount = async () => {
    const now = Date.now();
    if (now - lastFetchTime < FETCH_THROTTLE_MS) {
      return;
    }
    lastFetchTime = now;

    try {
      const data = await notificationApi.getUnreadCount();
      unreadCount.value = data.unreadCount;
    } catch (error) {
      console.error('읽지 않은 알림 개수 조회 실패:', error);
    }
  };

  /**
   * 알림 목록 조회
   */
  const fetchNotifications = async () => {
    try {
      isLoading.value = true;
      const data = await notificationApi.getNotifications();
      notifications.value = data;
    } catch (error) {
      console.error('알림 목록 조회 실패:', error);
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * 모든 알림을 읽음으로 처리
   */
  const markAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();

      // WebSocket이 연결되어 있지 않은 경우에만 로컬 상태 업데이트
      if (!webSocketStore.isConnected) {
        unreadCount.value = 0;
        notifications.value = notifications.value.map((notification) => ({
          ...notification,
          isRead: true,
        }));
      }
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error);
      throw error;
    }
  };

  /**
   * 특정 알림을 읽음으로 처리
   */
  const markAsRead = async (notificationId) => {
    try {
      await notificationApi.markAsRead(notificationId);

      if (!webSocketStore.isConnected) {
        const notification = notifications.value.find(
          (n) => n.id === notificationId
        );
        if (notification && !notification.isRead) {
          notification.isRead = true;
          unreadCount.value = Math.max(0, unreadCount.value - 1);
        }
      }
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error);
      throw error;
    }
  };

  /**
   * 새 알림 추가 (WebSocket을 통한 실시간 알림)
   */
  const addNotification = (notification) => {
    notifications.value.unshift(notification);
    unreadCount.value = unreadCount.value + 1;
  };

  /**
   * 읽지 않은 알림 개수 업데이트 (중복 방지)
   */
  const updateUnreadCount = (count) => {
    if (unreadCount.value !== count) {
      unreadCount.value = count;
      console.log(`🔄 unreadCount 변경: ${count}`);
    }
  };

  /**
   * 브라우저 알림 표시 (사용자 설정 고려)
   */
  const showBrowserNotification = (
    title,
    body,
    icon = '/images/logo-blue.png'
  ) => {
    // 페이지가 포커스되어 있으면 브라우저 알림 표시 안함
    if (!document.hidden) {
      return;
    }

    if (Notification.permission === 'granted') {
      const notification = new Notification(title, {
        body,
        icon,
        tag: 'cheongsan-notification',
        silent: false,
        requireInteraction: false,
      });

      // 5초 후 자동으로 닫기
      setTimeout(() => notification.close(), 5000);
    }
  };

  /**
   * 브라우저 알림 권한 요청
   */
  const requestNotificationPermission = async () => {
    if (Notification.permission === 'default') {
      const permission = await Notification.requestPermission();
      if (permission === 'granted') {
        console.log('✅ 브라우저 알림 권한 허용됨');
      } else {
        console.log('❌ 브라우저 알림 권한 거부됨');
      }
      return permission;
    }
    return Notification.permission;
  };

  /**
   * 최적화된 폴링 간격 설정
   */
  const getPollingInterval = (type = 'default') => {
    const isDev = import.meta.env.DEV;

    if (testModeActive) {
      return 5000; // 테스트 모드: 5초
    }

    if (type === 'websocket-connected') {
      // 웹소켓 연결 시 폴링을 거의 안 함
      return isDev ? 120000 : 300000; // 개발: 2분, 운영: 5분
    } else {
      // 웹소켓 끊어진 경우
      return isDev ? 15000 : 30000; // 개발: 15초, 운영: 30초
    }
  };

  /**
   * 테스트 모드 제어
   */
  const enableTestMode = () => {
    testModeActive = true;
    console.log('🧪 테스트 모드 활성화 - 5초 폴링 (2분간)');

    if (isPolling.value) {
      stopPolling();
      startPolling();
    }

    // 2분 후 자동 해제
    setTimeout(() => {
      disableTestMode();
    }, 120000);
  };

  const disableTestMode = () => {
    testModeActive = false;

    if (isPolling.value) {
      stopPolling();
      startPolling();
    }
  };

  /**
   * 스마트 폴링 시작
   */
  const startPolling = (customInterval) => {
    if (isPolling.value) return;

    const interval = customInterval || getPollingInterval('default');

    isPolling.value = true;
    pollingInterval.value = setInterval(() => {
      // 페이지가 숨겨져 있으면 폴링 스킵
      if (!isPageVisible) {
        return;
      }

      // 웹소켓이 연결되어 있으면 폴링 스킵 (테스트 모드 제외)
      if (webSocketStore.isConnected && !testModeActive) {
        return;
      }

      fetchUnreadCount();
    }, interval);

    const intervalSec = interval / 1000;
    const mode = testModeActive ? '(테스트 모드)' : '';
  };

  /**
   * 폴링 중지
   */
  const stopPolling = () => {
    if (pollingInterval.value) {
      clearInterval(pollingInterval.value);
      pollingInterval.value = null;
      isPolling.value = false;
    }
  };

  /**
   * 페이지 가시성 변화 처리
   */
  const setupVisibilityHandler = () => {
    const handleVisibilityChange = () => {
      isPageVisible = !document.hidden;

      if (isPageVisible) {
        // 페이지가 보이면 즉시 한 번 체크 (WebSocket 없을 때만)
        if (!webSocketStore.isConnected) {
          setTimeout(fetchUnreadCount, 500); // 약간의 지연
        }
      } else {
      }
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);

    // cleanup 함수에서 제거할 수 있도록 반환
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
  };

  /**
   * 최적화된 WebSocket 이벤트 핸들러들
   */
  const setupWebSocketHandlers = () => {
    // 새 알림 수신
    webSocketStore.on('notification', (data) => {
      if (data.unreadCount !== undefined) {
        updateUnreadCount(data.unreadCount);
      } else {
        updateUnreadCount(unreadCount.value + 1);
      }

      // 브라우저 알림 표시
      showBrowserNotification('새 알림 도착!', data.contents);
    });

    // 읽지 않은 알림 개수 업데이트
    webSocketStore.on('unreadCount', (data) => {
      updateUnreadCount(data.unreadCount);
    });

    // WebSocket 연결 시 - 폴링 최소화
    webSocketStore.on('connect', () => {
      if (isPolling.value && !testModeActive) {
        stopPolling();
        startPolling(getPollingInterval('websocket-connected'));
      }
    });

    // WebSocket 연결 해제 시 - 폴링 활성화
    webSocketStore.on('disconnect', () => {
      if (isPolling.value && !testModeActive) {
        stopPolling();
        startPolling(getPollingInterval('default'));
      }
    });
  };

  // cleanup 함수들을 저장
  let cleanupVisibility = null;

  /**
   * 초기화
   */
  const initialize = async () => {
    // 브라우저 알림 권한 요청
    await requestNotificationPermission();

    // WebSocket 이벤트 핸들러 설정
    setupWebSocketHandlers();

    // 페이지 가시성 핸들러 설정
    cleanupVisibility = setupVisibilityHandler();

    // 초기 데이터 로딩
    await fetchUnreadCount();

    // 스마트 폴링 시작
    startPolling();

    // 개발 환경에서 전역 함수 노출
    if (import.meta.env.DEV) {
      window.enableTestMode = enableTestMode;
      window.disableTestMode = disableTestMode;
      window.notificationStore = {
        enableTestMode,
        disableTestMode,
        fetchUnreadCount,
        unreadCount: () => unreadCount.value,
        isConnected: () => webSocketStore.isConnected,
      };
    }
  };

  /**
   * 정리
   */
  const cleanup = () => {
    stopPolling();

    // 페이지 가시성 이벤트 리스너 제거
    if (cleanupVisibility) {
      cleanupVisibility();
      cleanupVisibility = null;
    }

    // 개발 환경 전역 변수 정리
    if (import.meta.env.DEV && window.notificationStore) {
      delete window.notificationStore;
      delete window.enableTestMode;
      delete window.disableTestMode;
    }
  };

  /**
   * 날짜 포맷팅
   */
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

  // Computed
  const hasUnreadNotifications = computed(() => unreadCount.value > 0);
  const unreadBadgeText = computed(() =>
    unreadCount.value > 99 ? '99+' : unreadCount.value.toString()
  );

  return {
    // State
    notifications,
    unreadCount,
    isLoading,
    isPolling,

    // Computed
    hasUnreadNotifications,
    unreadBadgeText,

    // Actions
    fetchUnreadCount,
    fetchNotifications,
    markAllAsRead,
    markAsRead,
    addNotification,
    updateUnreadCount,
    showBrowserNotification,
    requestNotificationPermission,
    startPolling,
    stopPolling,
    initialize,
    cleanup,
    formatDate,

    // 테스트용 함수들
    enableTestMode,
    disableTestMode,
  };
});
