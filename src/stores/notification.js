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

  /**
   * 읽지 않은 알림 개수 조회
   */
  const fetchUnreadCount = async () => {
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
   * @param {number} notificationId - 알림 ID
   */
  const markAsRead = async (notificationId) => {
    try {
      await notificationApi.markAsRead(notificationId);

      // WebSocket이 연결되어 있지 않은 경우에만 로컬 상태 업데이트
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
   * @param {Object} notification - 새 알림 데이터
   */
  const addNotification = (notification) => {
    notifications.value.unshift(notification);
    unreadCount.value = unreadCount.value + 1;
  };

  /**
   * 읽지 않은 알림 개수 업데이트
   * @param {number} count - 새로운 읽지 않은 알림 개수
   */
  const updateUnreadCount = (count) => {
    unreadCount.value = count;
  };

  /**
   * 브라우저 알림 표시
   * @param {string} title - 알림 제목
   * @param {string} body - 알림 내용
   * @param {string} icon - 알림 아이콘
   */
  const showBrowserNotification = (
    title,
    body,
    icon = '/images/logo-blue.png'
  ) => {
    if (Notification.permission === 'granted') {
      new Notification(title, {
        body,
        icon,
        tag: 'cheongsan-notification',
      });
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
   * 폴링 시작 (WebSocket이 연결되지 않았을 때 사용)
   * @param {number} interval - 폴링 간격 (밀리초)
   */
  const startPolling = (interval = 30000) => {
    if (isPolling.value) return;

    isPolling.value = true;
    pollingInterval.value = setInterval(() => {
      // WebSocket이 연결되어 있지 않은 경우에만 폴링
      if (!webSocketStore.isConnected) {
        fetchUnreadCount();
      }
    }, interval);
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
   * WebSocket 이벤트 핸들러들
   */
  const setupWebSocketHandlers = () => {
    // 새 알림 수신
    webSocketStore.on('notification', (data) => {
      // 읽지 않은 알림 개수 업데이트
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

    // WebSocket 연결 시
    webSocketStore.on('connect', () => {
      // 폴링 간격 조정 (WebSocket 연결 시 폴링 빈도 줄임)
      if (isPolling.value) {
        stopPolling();
        startPolling(60000); // 1분 간격으로 변경
      }
    });

    // WebSocket 연결 해제 시
    webSocketStore.on('disconnect', () => {
      // 폴링 간격 조정 (WebSocket 연결 해제 시 폴링 빈도 늘림)
      if (isPolling.value) {
        stopPolling();
        startPolling(30000); // 30초 간격으로 변경
      }
    });
  };

  /**
   * 초기화
   */
  const initialize = async () => {
    // 브라우저 알림 권한 요청
    await requestNotificationPermission();

    // WebSocket 이벤트 핸들러 설정
    setupWebSocketHandlers();

    // 초기 데이터 로딩
    await fetchUnreadCount();

    // 폴링 시작
    startPolling();
  };

  /**
   * 정리
   */
  const cleanup = () => {
    stopPolling();
  };

  /**
   * 날짜 포맷팅
   * @param {string} dateString - 날짜 문자열
   * @returns {string} 포맷된 날짜
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
  };
});
