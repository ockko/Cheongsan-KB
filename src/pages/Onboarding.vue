<script setup>
import styles from '@/assets/styles/pages/Onboarding.module.css';
import { useRouter } from 'vue-router';

const router = useRouter();

const goToLogin = () => {
  router.push('/login');
};

const goToSignup = () => {
  router.push('/signup');
};
</script>

<template>
  <div :class="styles.container">
    <img
      src="/images/logo-blue.png"
      alt="로고"
      style="width: 166px; height: 166px"
    />
    <div :class="styles.title">티끌 모아 청산</div>
    <div :class="styles.description">
      티모청의 맞춤형 부채관리 서비스를 이용해보세요
    </div>
    <div :class="styles.buttonContainer">
      <button :class="[styles.button, styles.loginButton]" @click="goToLogin">
        로그인
      </button>
      <button :class="[styles.button, styles.signupButton]" @click="goToSignup">
        회원가입
      </button>
    </div>
  </div>
</template>
