<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  debtDetails: {
    type: Object,
    default: null, // 기본값은 null
  },
});
defineEmits(['close']);

// 숫자에 콤마(,)를 찍어주는 헬퍼 함수
const formatCurrency = (value) => {
  if (typeof value !== 'number') return '0';
  return value.toLocaleString('ko-KR');
};
</script>

<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h3>대출 상세 정보</h3>

      <div v-if="debtDetails" class="details-grid">
        <span class="label">대출 기관:</span>
        <span class="value">{{ debtDetails.organizationName }}</span>

        <span class="label">대출명:</span>
        <span class="value">{{ debtDetails.debtName }}</span>

        <span class="label">총 대출 원금:</span>
        <span class="value"
          >{{ formatCurrency(debtDetails.originalAmount) }} 원</span
        >

        <span class="label">남은 상환액:</span>
        <span class="value"
          >{{ formatCurrency(debtDetails.currentBalance) }} 원</span
        >

        <span class="label">상환율:</span>
        <span class="value"
          >{{ (debtDetails.repaymentRate * 100).toFixed(2) }} %</span
        >

        <span class="label">이자율:</span>
        <span class="value">{{ debtDetails.interestRate.toFixed(2) }} %</span>

        <span class="label">대출 기간:</span>
        <span class="value"
          >{{ debtDetails.loanStartDate }} ~ {{ debtDetails.loanEndDate }}</span
        >
      </div>
      <div v-else>
        <p>표시할 정보가 없습니다.</p>
      </div>

      <div class="button-group">
        <button class="cancel-button" @click="$emit('close')">닫기</button>
      </div>
    </div>
  </div>
</template>
