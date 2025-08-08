import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';
import { getApiBaseUrl } from '@/config/api';

const initState = {
  accessToken: '',
  refreshToken: '',
  user: {
    id: null,
    nickname: '',
  },
};

export const useAuthStore = defineStore('auth', () => {
  const state = ref({ ...initState });
  const isLoading = ref(false);

  // 로그인
  const login = async (credentials) => {
    isLoading.value = true;

    try {
      const response = await axios.post(
        `${getApiBaseUrl()}/cheongsan/auth/login`,
        {
          username: credentials.username,
          password: credentials.password,
        }
      );

      const { id, accessToken, refreshToken, nickname } = response.data;

      // 상태 업데이트
      state.value = {
        accessToken,
        refreshToken,
        user: {
          id,
          nickname: nickname || '', // nickname이 없을 경우 빈 문자열
        },
      };

      // localStorage에 저장
      localStorage.setItem('auth', JSON.stringify(state.value));

      console.log('로그인 성공:', state.value);
      return response.data;
    } catch (error) {
      console.error('로그인 실패:', error);

      // 에러 메시지 처리
      let errorMessage = '로그인에 실패했습니다.';
      if (error.response?.status === 400) {
        errorMessage = '아이디 또는 비밀번호가 잘못되었습니다.';
      } else if (error.response?.status === 500) {
        errorMessage = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }

      throw new Error(errorMessage);
    } finally {
      isLoading.value = false;
    }
  };

  // 토큰 갱신
  const refreshTokens = async () => {
    if (!state.value.refreshToken) {
      throw new Error('Refresh token이 없습니다.');
    }

    try {
      const response = await axios.post(
        `${getApiBaseUrl()}/cheongsan/auth/refresh`,
        {
          refreshToken: state.value.refreshToken,
        }
      );

      const { accessToken, refreshToken } = response.data;

      // 새 토큰으로 업데이트
      state.value.accessToken = accessToken;
      state.value.refreshToken = refreshToken;

      // localStorage 업데이트
      localStorage.setItem('auth', JSON.stringify(state.value));

      console.log('토큰 갱신 성공');
      return { accessToken, refreshToken };
    } catch (error) {
      console.error('토큰 갱신 실패:', error);
      // 갱신 실패시 로그아웃
      logout();
      throw new Error('세션이 만료되었습니다. 다시 로그인해주세요.');
    }
  };

  // 로그아웃
  const logout = () => {
    localStorage.removeItem('auth');
    state.value = { ...initState };
    console.log('로그아웃 완료');
  };

  // 토큰 가져오기
  const getToken = () => state.value.accessToken;

  // 사용자 정보 가져오기
  const getUser = () => state.value.user;

  // 로그인 상태 확인
  const isLoggedIn = () => !!state.value.accessToken;

  // localStorage에서 상태 로드
  const load = () => {
    try {
      const auth = localStorage.getItem('auth');
      if (auth) {
        const parsedAuth = JSON.parse(auth);
        state.value = parsedAuth;
        console.log('인증 상태 로드:', state.value);
      }
    } catch (error) {
      console.error('인증 상태 로드 실패:', error);
      localStorage.removeItem('auth'); // 손상된 데이터 제거
    }
  };

  // 초기화 시 상태 로드
  load();

  return {
    state,
    isLoading,
    login,
    logout,
    refreshTokens,
    getToken,
    getUser,
    isLoggedIn,
    load,
  };
});
