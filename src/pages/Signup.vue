<script setup>
import { ref, computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { getApiBaseUrl } from '@/config/url';
import styles from '@/assets/styles/pages/Signup.module.css';

import UserIdInput from '@/components/domain/Signup/UserIdInput.vue';
import PasswordInput from '@/components/domain/Signup/PasswordInput.vue';
import EmailInput from '@/components/domain/Signup/EmailInput.vue';
import TermsAgreement from '@/components/domain/Signup/TermsAgreement.vue';

const router = useRouter();

// 폼 데이터
const formData = reactive({
  userId: '',
  password: '',
  passwordConfirm: '',
  emailLocal: '',
  emailDomain: '',
  fullEmail: '',
});

// 상태 관리
const isSubmitting = ref(false);

// 각 컴포넌트의 유효성 검사 상태
const validation = reactive({
  userId: {
    isChecked: false,
    isAvailable: false,
  },
  password: {
    passwordValid: false,
    passwordConfirmValid: false,
  },
  email: {
    isValid: false,
  },
  terms: {
    isValid: false,
  },
});

// 폼 전체 유효성 검사
const isFormValid = computed(() => {
  return (
    formData.userId &&
    validation.userId.isChecked &&
    validation.userId.isAvailable &&
    validation.password.passwordValid &&
    validation.password.passwordConfirmValid &&
    validation.email.isValid &&
    validation.terms.isValid
  );
});

// 아이디 유효성 검사 결과 처리
const handleUserIdValidation = ({ isChecked, isAvailable }) => {
  validation.userId.isChecked = isChecked;
  validation.userId.isAvailable = isAvailable;
};

// 비밀번호 유효성 검사 결과 처리
const handlePasswordValidation = ({ passwordValid, passwordConfirmValid }) => {
  validation.password.passwordValid = passwordValid;
  validation.password.passwordConfirmValid = passwordConfirmValid;
};

// 이메일 유효성 검사 결과 처리
const handleEmailValidation = ({ isValid, fullEmail }) => {
  validation.email.isValid = isValid;
  formData.fullEmail = fullEmail;
};

// 이용약관 유효성 검사 결과 처리
const handleTermsValidation = ({ isValid }) => {
  validation.terms.isValid = isValid;
};

// 회원가입 처리
const handleSignUp = async () => {
  if (!isFormValid.value) {
    alert('모든 필수 항목을 올바르게 입력해주세요.');
    return;
  }

  isSubmitting.value = true;

  try {
    const signUpData = {
      userId: formData.userId,
      password: formData.password,
      email: formData.fullEmail,
    };

    const response = await axios.post(
      `${getApiBaseUrl()}/cheongsan/auth/signup`,
      signUpData
    );

    console.log('회원가입 성공:', response.data);
    alert('회원가입이 완료되었습니다!');

    // 로그인 페이지로 이동
    router.push('/login');
  } catch (error) {
    console.error('회원가입 실패:', error);

    let errorMessage = '회원가입에 실패했습니다.';
    if (error.response?.status === 400) {
      errorMessage =
        error.response.data?.message || '입력 정보를 확인해주세요.';
    } else if (error.response?.status === 500) {
      errorMessage = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
    }

    alert(errorMessage);
  } finally {
    isSubmitting.value = false;
  }
};

// 네이버 회원가입
const handleNaverLogin = () => {
  const naverClientId = 'YOUR_NAVER_CLIENT_ID'; // 실제 네이버 클라이언트 ID로 교체
  const redirectUri = encodeURIComponent(
    window.location.origin + '/auth/naver/callback'
  );
  const naverAuthUrl = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${naverClientId}&redirect_uri=${redirectUri}&state=signup`;

  window.location.href = naverAuthUrl;
};
</script>
<template>
  <div :class="styles.signUpPage">
    <div :class="styles.container">
      <h1 :class="styles.title">회원가입</h1>

      <form @submit.prevent="handleSignUp" :class="styles.signUpForm">
        <!-- 아이디 입력 컴포넌트 -->
        <UserIdInput
          v-model="formData.userId"
          :disabled="isSubmitting"
          @validation-change="handleUserIdValidation"
        />

        <!-- 비밀번호 입력 컴포넌트 -->
        <PasswordInput
          :password="formData.password"
          :password-confirm="formData.passwordConfirm"
          :disabled="isSubmitting"
          @update:password="formData.password = $event"
          @update:passwordConfirm="formData.passwordConfirm = $event"
          @validation-change="handlePasswordValidation"
        />

        <!-- 이메일 입력 컴포넌트 -->
        <EmailInput
          :email-local="formData.emailLocal"
          :email-domain="formData.emailDomain"
          :disabled="isSubmitting"
          @update:emailLocal="formData.emailLocal = $event"
          @update:emailDomain="formData.emailDomain = $event"
          @validation-change="handleEmailValidation"
        />

        <!-- 이용약관 동의 컴포넌트 -->
        <TermsAgreement @validation-change="handleTermsValidation" />

        <!-- 회원가입 버튼 -->
        <button
          type="submit"
          :class="[styles.signUpButton, { [styles.loading]: isSubmitting }]"
          :disabled="isSubmitting || !isFormValid"
        >
          {{ isSubmitting ? '회원가입 중...' : 'Sign Up' }}
        </button>
      </form>

      <!-- SNS 로그인 구분선 -->
      <div :class="styles.divider">
        <span>SNS 계정 회원가입</span>
      </div>

      <!-- 네이버 회원가입 버튼 -->
      <img
        @click="handleNaverLogin"
        :class="styles.naverButton"
        :disabled="isSubmitting"
        src="/images/naver-btn.png"
        alt="네이버 회원가입 버튼"
      />
    </div>
  </div>
</template>
