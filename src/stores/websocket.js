import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';

export const useWebSocketStore = defineStore('websocket', () => {
  const websocket = ref(null);
  const isConnected = ref(false);
  const reconnectAttempts = ref(0);
  const maxReconnectAttempts = 5;
  const reconnectDelay = 5000; // 5초

  // 이벤트 핸들러들
  const eventHandlers = ref({
    notification: [],
    unreadCount: [],
    connect: [],
    disconnect: [],
    error: [],
  });

  /**
   * 이벤트 핸들러 등록
   * @param {string} event - 이벤트 타입
   * @param {Function} handler - 핸들러 함수
   */
  const on = (event, handler) => {
    if (eventHandlers.value[event]) {
      eventHandlers.value[event].push(handler);
    }
  };

  /**
   * 이벤트 핸들러 제거
   * @param {string} event - 이벤트 타입
   * @param {Function} handler - 제거할 핸들러 함수
   */
  const off = (event, handler) => {
    if (eventHandlers.value[event]) {
      const index = eventHandlers.value[event].indexOf(handler);
      if (index > -1) {
        eventHandlers.value[event].splice(index, 1);
      }
    }
  };

  /**
   * 이벤트 실행
   * @param {string} event - 이벤트 타입
   * @param {*} data - 전달할 데이터
   */
  const emit = (event, data) => {
    if (eventHandlers.value[event]) {
      eventHandlers.value[event].forEach((handler) => {
        try {
          handler(data);
        } catch (error) {
          console.error(`이벤트 핸들러 실행 오류 (${event}):`, error);
        }
      });
    }
  };

  /**
   * WebSocket URL 생성
   * @param {number} userId - 사용자 ID
   * @returns {string} WebSocket URL
   */
  const createWebSocketUrl = (userId) => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = import.meta.env.VITE_API_BASE_URL
      ? import.meta.env.VITE_API_BASE_URL.replace(/^https?:\/\//, '').replace(
          /\/$/,
          ''
        )
      : window.location.host;

    return `${protocol}//${host}/ws/notifications?userId=${userId}`;
  };

  /**
   * WebSocket 연결
   */
  const connect = () => {
    const authStore = useAuthStore();
    const user = authStore.getUser();

    if (!user?.id) {
      console.log('사용자 정보가 없어 WebSocket 연결을 건너뜁니다.');
      return;
    }

    // 기존 연결이 있으면 종료
    disconnect();

    const wsUrl = createWebSocketUrl(user.id);
    console.log('WebSocket 연결 시도:', wsUrl);

    websocket.value = new WebSocket(wsUrl);

    websocket.value.onopen = () => {
      console.log('✅ WebSocket 연결 성공');
      isConnected.value = true;
      reconnectAttempts.value = 0;
      emit('connect');
    };

    websocket.value.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        console.log('📨 WebSocket 메시지 수신:', data);

        // 메시지 타입별로 이벤트 발생
        if (data.type === 'notification') {
          emit('notification', data);
        } else if (data.type === 'unreadCount') {
          emit('unreadCount', data);
        }
      } catch (error) {
        console.error('❌ WebSocket 메시지 파싱 실패:', error);
      }
    };

    websocket.value.onerror = (error) => {
      console.error('❌ WebSocket 오류:', error);
      isConnected.value = false;
      emit('error', error);
    };

    websocket.value.onclose = (event) => {
      console.log('🔌 WebSocket 연결 종료:', event.code, event.reason);
      isConnected.value = false;
      emit('disconnect', { code: event.code, reason: event.reason });

      // 정상적인 종료가 아니고 사용자가 로그인된 상태라면 재연결 시도
      if (
        event.code !== 1000 &&
        authStore.isLoggedIn() &&
        reconnectAttempts.value < maxReconnectAttempts
      ) {
        reconnectAttempts.value++;
        console.log(
          `⏰ ${reconnectDelay / 1000}초 후 WebSocket 재연결 시도... (${
            reconnectAttempts.value
          }/${maxReconnectAttempts})`
        );

        setTimeout(() => {
          if (authStore.isLoggedIn()) {
            connect();
          }
        }, reconnectDelay);
      }
    };
  };

  /**
   * WebSocket 연결 해제
   */
  const disconnect = () => {
    if (websocket.value) {
      console.log('🔌 WebSocket 연결 해제');
      websocket.value.close(1000, 'Manual disconnect');
      websocket.value = null;
      isConnected.value = false;
    }
  };

  /**
   * 메시지 전송
   * @param {Object} data - 전송할 데이터
   */
  const send = (data) => {
    if (websocket.value && isConnected.value) {
      websocket.value.send(JSON.stringify(data));
    } else {
      console.warn('WebSocket이 연결되지 않아 메시지를 전송할 수 없습니다.');
    }
  };

  return {
    // State
    isConnected,
    reconnectAttempts,

    // Actions
    connect,
    disconnect,
    send,
    on,
    off,

    // Getters
    getConnectionStatus: () => isConnected.value,
  };
});
