<script setup>
import styles from '@/assets/styles/pages/Onboarding.module.css';
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
      <button :class="[styles.button, styles.loginButton]">로그인</button>
      <button :class="[styles.button, styles.signupButton]">회원가입</button>
    </div>
  </div>
</template>
