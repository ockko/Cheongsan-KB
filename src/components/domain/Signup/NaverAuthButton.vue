<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import styles from '@/assets/styles/components/Signup/NaverAuthButton.module.css';

const props = defineProps({
  type: {
    type: String,
    default: 'login', // 'login' 또는 'signup'
    validator: (value) => ['login', 'signup'].includes(value),
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['auth-start', 'auth-success', 'auth-error']);

const router = useRouter();
const authStore = useAuthStore();

const isSubmitting = ref(false);
const errorMessage = ref('');

// 버튼 텍스트 계산
const buttonText = props.type === 'login' ? '네이버 로그인' : '네이버 회원가입';

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

  emit('auth-start');

  try {
    console.log('네이버 인증코드 수신:', code);

    // authStore의 naverLogin 함수 사용
    await authStore.naverLogin(code);

    console.log('네이버 로그인 성공');

    emit('auth-success');

    // URL에서 파라미터 제거하고 홈으로 이동
    const currentPath = props.type === 'login' ? '/login' : '/signup';
    window.history.replaceState({}, '', currentPath);
    router.push('/home');
  } catch (error) {
    console.error('네이버 로그인 처리 실패:', error);
    const errorMsg = error.message || '네이버 로그인 중 오류가 발생했습니다.';
    errorMessage.value = errorMsg;

    emit('auth-error', errorMsg);

    // URL 정리
    const currentPath = props.type === 'login' ? '/login' : '/signup';
    window.history.replaceState({}, '', currentPath);
  } finally {
    isSubmitting.value = false;
  }
};

// 네이버 로그인/회원가입 - OAuth 페이지로 리다이렉트
const handleNaverAuth = () => {
  if (isSubmitting.value || props.disabled) return;

  emit('auth-start');

  // 환경변수에서 프론트엔드 URL과 네이버 클라이언트 ID 가져오기
  const frontendUrl =
    import.meta.env.VITE_FRONTEND_URL || window.location.origin;
  const naverClientId = import.meta.env.VITE_NAVER_CLIENT_ID;
  const redirectUri = `${frontendUrl}/${props.type}`; // /login 또는 /signup

  // 네이버 OAuth URL
  const naverAuthUrl = `https://nid.naver.com/oauth2.0/authorize?client_id=${naverClientId}&redirect_uri=${redirectUri}&response_type=code&state=test123`;

  console.log(`네이버 ${props.type} 시작...`, {
    frontendUrl,
    redirectUri,
    clientId: naverClientId,
  });

  window.location.href = naverAuthUrl;
};
</script>

<template>
  <button
    @click="handleNaverAuth"
    :class="styles.naverButton"
    :disabled="disabled || isSubmitting"
    type="button"
  >
    <div :class="styles.naverButtonContent">
      <span :class="styles.naverLogo">N</span>
      <span :class="styles.naverButtonText">
        {{ isSubmitting ? '처리 중...' : buttonText }}
      </span>
    </div>
  </button>
</template>
