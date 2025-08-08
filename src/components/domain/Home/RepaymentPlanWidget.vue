<script setup>
import styles from '@/assets/styles/components/Home/RepaymentPlanWidget.module.css';
import { defineProps } from 'vue';

// 부모 컴포넌트로부터 받을 데이터(props)를 정의합니다.
const props = defineProps({
  planData: {
    type: Object,
    required: true,
    default: () => ({
      monthlyPayment: 0,
      totalMonths: 0,
      debtFreeDate: 'N/A',
    }),
  },
});

// 숫자를 '만원' 단위 텍스트로 변환하는 헬퍼 함수
const formatToManwon = (value) => {
  if (typeof value !== 'number' || value === 0) return '0 원';
  // 10000으로 나누어 만원 단위로 변환 후 콤마 추가
  const manwon = Math.round(value / 10000);
  return `${manwon.toLocaleString('ko-KR')} 만원`;
};
</script>

<template>
  <div :class="styles.widgetCard">
    <div :class="styles.widgetHeader">
      <h3>나의 청산 플랜</h3>
    </div>

    <div :class="styles.widgetContent">
      <div :class="styles.planItem">
        <span :class="styles.label">월 상환액</span>
        <span :class="[styles.value, styles.highlight]">{{
          formatToManwon(planData.monthlyPayment)
        }}</span>
      </div>

      <div :class="styles.planItem">
        <span :class="styles.label">총 상환 기간</span>
        <span :class="styles.value">{{ planData.totalMonths }}개월</span>
      </div>

      <div :class="styles.planItem">
        <span :class="styles.label">최종 빚 졸업일</span>
        <span :class="styles.value">{{ planData.debtFreeDate }}</span>
      </div>
    </div>
  </div>
</template>
