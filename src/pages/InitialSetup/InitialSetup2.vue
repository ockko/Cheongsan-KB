<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

import styles from '@/assets/styles/pages/InitialSetup/InitialSetup2.module.css';
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue';

const router = useRouter();

onMounted(() => {
  setTimeout(() => {
    router.push('/initialSetup/page3'); // 다음 페이지 경로로 변경하세요
  }, 3000); // 3000ms = 3초
});
</script>

<template>
  <div :class="styles.page">
    <ProgressHeader :current="0" :total="0" />
    <div :class="styles.container">
      <div :class="styles.titleBox">
        <h2>마이데이터<br />연동 중</h2>
        <p>
          000님의 자산내역을 불러오는 중입니다. <br />
          잠시만 기다려주세요.
        </p>
        <img src="/images/logo-blue.png" alt="로고" />
      </div>

      <div id="container">
        <div class="stick" v-for="n in 6" :key="n"></div>
        <h1>Loading...</h1>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@use '@/assets/styles/pages/InitialSetup/InitialSetup2.module.scss';
</style>
