<script setup>
import { ref, computed } from 'vue';
import CalendarHeader from '@/components/domain/Calendar/CalendarHeader.vue';
import CalendarGrid from '@/components/domain/Calendar/CalendarGrid.vue';
import CalendarDetailView from '@/components/domain/Calendar/CalendarDetailView.vue';
import styles from '@/assets/styles/pages/Calendar.module.css';

// 현재 날짜 상태 관리
const currentDate = ref(new Date());
const selectedDate = ref(null);

// 현재 년도와 월 계산
const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => currentDate.value.getMonth());

// 월 변경 함수
const changeMonth = (direction) => {
  const newDate = new Date(currentDate.value);
  newDate.setMonth(currentDate.value.getMonth() + direction);
  currentDate.value = newDate;
  selectedDate.value = null; // 월 변경 시 선택된 날짜 초기화
};

// 날짜 클릭 핸들러
const handleDateClick = (date) => {
  // 해당 날짜에 데이터가 있는지 확인
  const hasTransactionData = transactionData.value[date];
  const hasLoanData = loanData.value[date];

  if (hasTransactionData || hasLoanData) {
    selectedDate.value = selectedDate.value === date ? null : date; // 토글
  } else {
    selectedDate.value = null; // 데이터가 없으면 상세보기 숨김
  }
};

// 더미 데이터 - 실제로는 API에서 가져올 데이터
const transactionData = ref({
  9: { income: 42000, expense: -7800 },
  15: { expense: -15000 },
  22: { income: 35000 },
  28: { expense: -23000 },
});

const loanData = ref({
  9: {
    type: '대출',
    label: '대출',
    bankName: '국민은행',
    loanName: '주택담보대출',
  },
});

// 선택된 날짜의 상세 데이터
const selectedDateData = computed(() => {
  if (!selectedDate.value) return null;

  return {
    date: selectedDate.value,
    transactions: transactionData.value[selectedDate.value] || null,
    loan: loanData.value[selectedDate.value] || null,
  };
});
</script>

<template>
  <div :class="styles.pageWrapper">
    <div :class="styles.page">
      <!-- 로고 배경 -->
      <div :class="styles.logoBackground"></div>

      <CalendarHeader
        :year="currentYear"
        :month="currentMonth"
        @change-month="changeMonth"
      />

      <CalendarGrid
        :year="currentYear"
        :month="currentMonth"
        :transaction-data="transactionData"
        :loan-data="loanData"
        :selected-date="selectedDateData"
        @date-click="handleDateClick"
      />

      <!-- 날짜의 상세내역 -->
      <CalendarDetailView v-if="selectedDateData" :data="selectedDateData" />
    </div>
  </div>
</template>
