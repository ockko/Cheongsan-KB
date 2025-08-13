<script setup>
import styles from '@/assets/styles/components/home/TotalDebtRepaymentWidget.module.css';
import { ref, computed, onMounted } from 'vue';
import { getTotalDebtRepaymentData } from '@/api/dashboard-bottomApi.js';

// 컴포넌트 내부에서 데이터를 관리
const repaymentData = ref({
  totalDebt: 0,
  totalRepaid: 0,
  repaymentRatio: 0,
  remainingDebt: 0,
});

const loading = ref(true);
const error = ref(null);

// 데이터 로드 함수
const loadRepaymentData = async () => {
  try {
    loading.value = true;
    error.value = null;
    const data = await getTotalDebtRepaymentData();
    repaymentData.value = data;
  } catch (err) {
    console.error('상환 데이터 로드 실패:', err);
    error.value = '데이터를 불러오는데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadRepaymentData();
});

// --- 차트 계산을 위한 변수 ---
const chartSize = 80;
const strokeWidth = 12;
const radius = chartSize / 2 - strokeWidth / 2;
const circumference = 2 * Math.PI * radius;

// SVG 프로그레스 바의 offset 계산
const strokeDashoffset = computed(() => {
  const progress = repaymentData.value.repaymentRatio / 100;
  return circumference * (1 - progress);
});

// 숫자를 '만원' 단위 텍스트로 변환하는 헬퍼 함수
const formatToManwon = (value) => {
  if (typeof value !== 'number' || value === 0) return '0 원';
  const manwon = Math.floor(value / 10000);
  return `${manwon.toLocaleString('ko-KR')} 만원`;
};
</script>

<template>
  <div :class="styles.widgetCard">
    <div :class="styles.widgetHeader">
      <h3>총 부채 상환율</h3>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" :class="styles.loading">데이터를 불러오는 중...</div>

    <!-- 에러 상태 -->
    <div v-else-if="error" :class="styles.error">
      {{ error }}
      <button @click="loadRepaymentData" :class="styles.retryButton">
        재시도
      </button>
    </div>

    <!-- 데이터 표시 -->
    <div v-else :class="styles.widgetContent">
      <div :class="styles.chartContainer">
        <svg
          :class="styles.progressRing"
          :width="chartSize"
          :height="chartSize"
        >
          <circle
            :class="styles.progressRingTrack"
            :stroke-width="strokeWidth"
            fill="transparent"
            :r="radius"
            :cx="chartSize / 2"
            :cy="chartSize / 2"
          />
          <circle
            :class="styles.progressRingIndicator"
            :stroke-width="strokeWidth"
            fill="transparent"
            :r="radius"
            :cx="chartSize / 2"
            :cy="chartSize / 2"
            :style="{ strokeDashoffset: strokeDashoffset }"
            :stroke-dasharray="circumference + ' ' + circumference"
          />
        </svg>
        <div :class="styles.percentageText">
          {{ repaymentData.repaymentRatio.toFixed(0) }}%
        </div>
      </div>

      <div :class="styles.summaryText">
        <div :class="styles.summaryItem">
          <span>총 부채</span>
          <span :class="styles.amount">{{
            formatToManwon(repaymentData.totalDebt)
          }}</span>
        </div>
        <div :class="styles.summaryItem">
          <span>현재 상환액</span>
          <span :class="styles.amount">{{
            formatToManwon(repaymentData.totalRepaid)
          }}</span>
        </div>
        <div :class="styles.summaryItem">
          <span>남은 부채</span>
          <span :class="styles.amount">{{
            formatToManwon(repaymentData.remainingDebt)
          }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
