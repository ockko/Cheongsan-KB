<script setup>
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import styles from '@/assets/styles/pages/Login.module.css';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 폼 데이터
const formData = ref({
  username: '',
  password: '',
});

// 에러 상태
const errorMessage = ref('');
const isSubmitting = ref(false);

// URL 파라미터에서 에러 메시지 확인
if (route.query.error === 'session_expired') {
  errorMessage.value = '세션이 만료되었습니다. 다시 로그인해주세요.';
} else if (route.query.error === 'login_required') {
  errorMessage.value = '로그인이 필요한 서비스입니다.';
}

// 컴포넌트 마운트 시 네이버 콜백 처리
onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  const state = urlParams.get('state');

  // 네이버 OAuth 콜백인지 확인
  if (code && state) {
    handleNaverCallback(code);
  }
});

// 네이버 OAuth 콜백 처리
const handleNaverCallback = async (code) => {
  isSubmitting.value = true;
  errorMessage.value = '';

  try {
    console.log('네이버 인증코드 수신:', code);

    // authStore의 naverLogin 함수 사용
    await authStore.naverLogin(code);

    console.log('네이버 로그인 성공');

    // URL에서 파라미터 제거하고 홈으로 이동
    window.history.replaceState({}, '', '/login');
    router.push('/home');
  } catch (error) {
    console.error('네이버 로그인 처리 실패:', error);
    errorMessage.value =
      error.message || '네이버 로그인 중 오류가 발생했습니다.';

    // URL 정리
    window.history.replaceState({}, '', '/login');
  } finally {
    isSubmitting.value = false;
  }
};

// 일반 로그인 처리
const handleLogin = async () => {
  // 유효성 검사
  if (!formData.value.username.trim()) {
    errorMessage.value = '아이디를 입력해주세요.';
    return;
  }

  if (!formData.value.password.trim()) {
    errorMessage.value = '비밀번호를 입력해주세요.';
    return;
  }

  isSubmitting.value = true;
  errorMessage.value = '';

  try {
    await authStore.login(formData.value);

    // 로그인 성공 시 홈으로 이동
    router.push('/initialSetup/page1');
  } catch (error) {
    errorMessage.value = error.message;
  } finally {
    isSubmitting.value = false;
  }
};

// 네이버 로그인 - OAuth 페이지로 리다이렉트 (환경변수 사용)
const handleNaverLogin = () => {
  if (isSubmitting.value) return;

  // 환경변수에서 프론트엔드 URL과 네이버 클라이언트 ID 가져오기
  const frontendUrl =
    import.meta.env.VITE_FRONTEND_URL || window.location.origin;
  const naverClientId = import.meta.env.VITE_NAVER_CLIENT_ID;
  const redirectUri = `${frontendUrl}/login`;

  // 네이버 OAuth URL
  const naverAuthUrl = `https://nid.naver.com/oauth2.0/authorize?client_id=${naverClientId}&redirect_uri=${redirectUri}&response_type=code&state=test123`;

  console.log('네이버 로그인 시작...', {
    frontendUrl,
    redirectUri,
    clientId: naverClientId,
  });
  window.location.href = naverAuthUrl;
};

// 회원가입 페이지로 이동
const goToSignup = () => {
  // 회원가입 페이지 구현 후 라우팅
  console.log('회원가입 페이지로 이동 예정');
};
</script>

<template>
  <div :class="styles.loginPage">
    <div :class="styles.container">
      <!-- 타이틀 -->
      <h1 :class="styles.title">로그인</h1>
      <p :class="styles.subtitle">티끌모아 청산에 오신 걸 환영합니다.</p>

      <!-- 로그인 폼 -->
      <form @submit.prevent="handleLogin" :class="styles.loginForm">
        <!-- 에러 메시지 -->
        <div v-if="errorMessage" :class="styles.errorMessage">
          {{ errorMessage }}
        </div>

        <!-- 아이디 입력 -->
        <div :class="styles.inputGroup">
          <input
            v-model="formData.username"
            type="text"
            placeholder="아이디"
            :class="styles.input"
            :disabled="isSubmitting"
          />
        </div>

        <!-- 비밀번호 입력 -->
        <div :class="styles.inputGroup">
          <input
            v-model="formData.password"
            type="password"
            placeholder="비밀번호"
            :class="styles.input"
            :disabled="isSubmitting"
          />
        </div>

        <!-- 로그인 버튼 -->
        <button
          type="submit"
          :class="[styles.loginButton, { [styles.loading]: isSubmitting }]"
          :disabled="isSubmitting"
        >
          {{ isSubmitting ? '로그인 중...' : 'Login' }}
        </button>
      </form>

      <!-- SNS 로그인 구분선 -->
      <div :class="styles.divider">
        <span>SNS 계정 로그인</span>
      </div>

      <img
        @click="handleNaverLogin"
        style="height: 44px; cursor: pointer"
        :disabled="isSubmitting"
        src="/images/naver-btn.png"
        alt="네이버 로그인 버튼"
      />

      <!-- 하단 링크들 -->
      <div :class="styles.footer">
        <button :class="styles.linkButton">아이디 찾기</button>
        <span :class="styles.separator">|</span>
        <button :class="styles.linkButton">비밀번호 찾기</button>
      </div>
    </div>
  </div>
</template>
