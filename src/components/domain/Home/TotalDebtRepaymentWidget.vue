<script setup>
import styles from '@/assets/styles/components/Home/TotalDebtRepaymentWidget.module.css';
import { defineProps, computed } from 'vue';

// 부모 컴포넌트로부터 받을 데이터(props)를 정의합니다.
const props = defineProps({
  repaymentData: {
    type: Object,
    required: true,
    default: () => ({
      totalOriginalAmount: 10000000,
      totalRepaidAmount: 4000000,
      repaymentRatio: 40.0,
    }),
  },
});

// --- 차트 계산을 위한 변수 ---
const chartSize = 85;
const strokeWidth = 12;
const radius = chartSize / 2 - strokeWidth / 2;
const circumference = 2 * Math.PI * radius;

// SVG 프로그레스 바의 offset 계산
const strokeDashoffset = computed(() => {
  const progress = props.repaymentData.repaymentRatio / 100;
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

    <div :class="styles.widgetContent">
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
            formatToManwon(repaymentData.totalOriginalAmount)
          }}</span>
        </div>
        <div :class="styles.summaryItem">
          <span>현재 상환액</span>
          <span :class="styles.amount">{{
            formatToManwon(repaymentData.totalRepaidAmount)
          }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
