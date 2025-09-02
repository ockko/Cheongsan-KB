<script setup>
import { onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

import animation from '@/assets/styles/pages/InitialSetup/InitialSetup2.module.scss';
import styles from '@/assets/styles/pages/InitialSetup/InitialSetup2.module.css';
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue';
import { useAuthStore } from '@/stores/auth.js';

const router = useRouter();
const authStore = useAuthStore();

const nickname = computed(() => authStore.getUser().nickName || '000'); // 없으면 fallback

onMounted(() => {
  setTimeout(() => {
    router.push('/initialSetup/page3');
  }, 3000);
});
</script>

<template>
  <div :class="styles.page">
    <ProgressHeader :current="0" :total="0" />
    <div :class="styles.container">
      <div :class="styles.titleBox">
        <h2>마이데이터<br />연동 중</h2>
        <p>
          <span :class="styles.nickname">{{ nickname }}</span
          >님의 자산내역을 불러오는 중입니다. <br />
          잠시만 기다려주세요.
        </p>
        <img src="/images/logo-blue.png" alt="로고" />
      </div>
      <div :class="animation.pageScope">
        <div :class="animation.container">
          <div :class="animation.stick" v-for="n in 6" :key="n"></div>
          <h1 :class="animation.h1">Loading...</h1>
        </div>
      </div>
    </div>
  </div>
</template>
