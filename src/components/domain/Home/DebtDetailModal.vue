<script setup>
import styles from '@/assets/styles/components/Home/DebtDetailModal.module.css';
import { defineProps, defineEmits } from 'vue';

// 부모 컴포넌트로부터 받을 데이터(props)를 정의합니다.
const props = defineProps({
  debtDetails: {
    type: Object,
    required: true,
    default: () => ({}),
  },
});
defineEmits(['close']);

// 숫자에 콤마(,)를 찍어주는 헬퍼 함수
const formatCurrency = (value) => {
  if (typeof value !== 'number') return '0';
  return value.toLocaleString('ko-KR');
};

// 상환 방식 코드값을 한글로 변환하는 헬퍼 함수
const formatRepaymentMethod = (method) => {
  switch (method) {
    case 'EQUAL_PRINCIPAL_INTEREST':
      return '원리금균등상환';
    case 'EQUAL_PRINCIPAL':
      return '원금균등상환';
    case 'BULLET_REPAYMENT':
      return '만기일시상환';
    default:
      return method;
  }
};
</script>

<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h3 class="debt-title">{{ debtDetails.debtName }}</h3>
      <p class="organization-name">
        <span class="icon">🏢</span> {{ debtDetails.organizationName }}
      </p>

      <ul class="details-list">
        <li>
          <span class="label">✔️ 원금</span>
          <span class="value"
            >{{ formatCurrency(debtDetails.originalAmount) }} 원</span
          >
        </li>
        <li>
          <span class="label">✔️ 이자율</span>
          <span class="value">{{ debtDetails.interestRate.toFixed(2) }} %</span>
        </li>
        <li>
          <span class="label">✔️ 대출 시작일</span>
          <span class="value">{{ debtDetails.loanStartDate }}</span>
        </li>
        <li>
          <span class="label">✔️ 남은 상환액</span>
          <span class="value"
            >{{ formatCurrency(debtDetails.currentBalance) }} 원</span
          >
        </li>
        <li>
          <span class="label">✔️ 거치기간</span>
          <span class="value">{{ debtDetails.gracePeriodMonths }} 개월</span>
        </li>
        <li>
          <span class="label">✔️ 상환 방식</span>
          <span class="value">{{
            formatRepaymentMethod(debtDetails.repaymentMethod)
          }}</span>
        </li>
      </ul>

      <button class="close-button" @click="$emit('close')">닫기</button>
    </div>
  </div>
</template>
