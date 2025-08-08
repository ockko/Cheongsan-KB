import axios from 'axios';
import { getApiBaseUrl } from '@/config/api';

const instance = axios.create({
  baseURL: getApiBaseUrl(),
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 - Authorization 헤더 자동 추가
instance.interceptors.request.use(
  (config) => {
    // auth store를 동적으로 가져와서 순환 참조 방지
    const authStore = JSON.parse(localStorage.getItem('auth') || '{}');
    const token = authStore.accessToken;

    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log(
        'API 요청 with token:',
        config.method?.toUpperCase(),
        config.url
      );
    } else {
      console.log(
        'API 요청 without token:',
        config.method?.toUpperCase(),
        config.url
      );
    }

    return config;
  },
  (error) => {
    console.error('API 요청 에러:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 토큰 만료 처리
instance.interceptors.response.use(
  (response) => {
    console.log('API 응답 성공:', response.status, response.config.url);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // 401 에러 (토큰 만료) 처리
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // 토큰 갱신 시도
        const authStore = JSON.parse(localStorage.getItem('auth') || '{}');

        if (authStore.refreshToken) {
          const response = await axios.post(
            `${getApiBaseUrl()}/cheongsan/auth/refresh`,
            {
              refreshToken: authStore.refreshToken,
            }
          );

          const { accessToken, refreshToken } = response.data;

          // localStorage 업데이트
          const newAuthState = {
            ...authStore,
            accessToken,
            refreshToken,
          };
          localStorage.setItem('auth', JSON.stringify(newAuthState));

          // 새 토큰으로 원래 요청 재시도
          originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
          return instance(originalRequest);
        }
      } catch (refreshError) {
        // 토큰 갱신 실패 시 로그인 페이지로 리다이렉트
        console.error('토큰 갱신 실패:', refreshError);
        localStorage.removeItem('auth');

        // Vue Router 동적 import로 순환 참조 방지
        import('@/router').then(({ default: router }) => {
          router.push('/login?error=session_expired');
        });

        return Promise.reject(refreshError);
      }
    }

    // 404 에러 처리
    if (error.response?.status === 404) {
      console.error('404 에러:', error.config.url);
      return Promise.reject(new Error('요청한 리소스를 찾을 수 없습니다.'));
    }

    // 500 에러 처리
    if (error.response?.status === 500) {
      console.error('서버 에러:', error);
      return Promise.reject(new Error('서버 내부 오류가 발생했습니다.'));
    }

    console.error('API 응답 에러:', error.response?.status, error.message);
    return Promise.reject(error);
  }
);

// API 요청 함수들 (기존 코드와 호환)
export const request = {
  // GET 요청
  get: async (url, config = {}) => {
    try {
      const response = await instance.get(url, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // POST 요청
  post: async (url, data = {}, config = {}) => {
    try {
      const response = await instance.post(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // PUT 요청
  put: async (url, data = {}, config = {}) => {
    try {
      const response = await instance.put(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // PATCH 요청
  patch: async (url, data = {}, config = {}) => {
    try {
      const response = await instance.patch(url, data, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // DELETE 요청
  delete: async (url, config = {}) => {
    try {
      const response = await instance.delete(url, config);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};

// 기존 코드 호환성을 위한 default export
export default instance;
