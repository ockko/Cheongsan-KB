import { useAuthStore } from '@/stores/auth';
import router from '@/router';
import axios from 'axios';

const instance = axios.create({
  timeout: 1000,
});

// 요청인터셉터
instance.interceptors.request.use(
  (config) => {
    // config.headers :요청헤더
    // JWT 추출
    const { getToken } = useAuthStore();
    const token = getToken();
    if (token) {
      //토큰이 있는경우
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log(config.headers.Authorization);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터럽트
instance.interceptors.response.use(
  (response) => {
    if (response.status === 200) {
      return response;
    }
    if (response.status === 404) {
      // 404응답도 정상적인 응답으로 볼 수 있다.
      return Promise.reject('404: 페이지 없음 ' + response.request);
    }
    return response;
  },
  async (error) => {
    if (error.response?.status === 401) {
      const { logout } = useAuthStore();
      logout();
      router.push('/auth/login?error=login_required');
      return Promise.reject({ error: '로그인이 필요한 서비스입니다.' });
    }

    return Promise.reject(error);
  }
);

export default instance;
// index.js는 axios통신을 위한 초기셋팅입니다. 이 파일은 백엔드 개발에 따라 수정이 될 수 있으므로 수정시 나중에 다시 공지하겠습니다.
