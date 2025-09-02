<script setup>
import styles from '@/assets/styles/components/policy/DiagnosisSection.module.css';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// 자가진단 페이지로 이동
const goToDiagnosis = () => {
  router.push('/diagnosis');
};
</script>

<template>
  <div :class="styles.diagnosisSectionContainer">
    <!-- 검색 아이콘 섹션 -->
    <div :class="styles.diagnosisSectionSearchSection">
      <div :class="styles.diagnosisSectionSearchIcon">
        <img
          src="/images/policy-icon.png"
          alt="검색"
          :class="styles.diagnosisSectionSearchIconImg"
        />
      </div>
      <div :class="styles.diagnosisSectionSearchText">
        <h2 :class="styles.diagnosisSectionSearchTitle">
          내게 맞는<br />채무 조정 제도 찾기
        </h2>
        <p :class="styles.diagnosisSectionSearchDescription">
          {{ authStore.state.user.nickName }}님의 상황에 맞는<br />채무 조정
          제도를 알려드려요.
        </p>
      </div>
    </div>
    <button @click="goToDiagnosis" :class="styles.diagnosisSectionSearchButton">
      제도 찾으러 가기
    </button>
  </div>
</template>
