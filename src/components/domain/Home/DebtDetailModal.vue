<script setup>
import styles from '@/assets/styles/components/home/DebtDetailModal.module.css';
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
  <div :class="styles.modalOverlay" @click.self="$emit('close')">
    <div :class="styles.modalContent">
      <h3 :class="styles.debtTitle">{{ debtDetails.debtName }}</h3>
      <p :class="styles.organizationName">
        <span :class="styles.icon">🏢</span> {{ debtDetails.organizationName }}
      </p>

      <ul :class="styles.detailsList">
        <li>
          <span :class="styles.label">
            <span>✔️</span>
            <sapn>원금</sapn>
          </span>
          <span :class="styles.value"
            >{{ formatCurrency(debtDetails.originalAmount) }} 원</span
          >
        </li>
        <li>
          <span :class="styles.label">
            <span>✔️</span>
            <sapn>이자율</sapn>
          </span>
          <span :class="styles.value"
            >{{ debtDetails.interestRate.toFixed(2) }} %</span
          >
        </li>
        <li>
          <span :class="styles.label">
            <span>✔️</span>
            <sapn>대출 시작일</sapn>
          </span>
          <span :class="styles.value">{{ debtDetails.loanStartDate }}</span>
        </li>
        <li>
          <span :class="styles.label">
            <span>✔️</span>
            <sapn>남은 상환액</sapn>
          </span>
          <span :class="styles.value"
            >{{ formatCurrency(debtDetails.currentBalance) }} 원</span
          >
        </li>
        <li>
          <span :class="styles.label">
            <span>✔️</span>
            <sapn>거치기간</sapn>
          </span>
          <span :class="styles.value"
            >{{ debtDetails.gracePeriodMonths }} 개월</span
          >
        </li>
        <li>
          <span :class="styles.label">
            <span>✔️</span>
            <sapn>상환 방식</sapn>
          </span>
          <span :class="styles.value">{{
            formatRepaymentMethod(debtDetails.repaymentMethod)
          }}</span>
        </li>
      </ul>

      <div :class="styles.buttonContainer">
        <button :class="styles.closeButton" @click="$emit('close')">
          닫기
        </button>
      </div>
    </div>
  </div>
</template>
