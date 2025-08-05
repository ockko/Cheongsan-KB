<script setup>
import { ref, computed } from 'vue';
import CalendarHeader from '@/components/domain/Calendar/CalendarHeader.vue';
import CalendarGrid from '@/components/domain/Calendar/CalendarGrid.vue';
import styles from '@/assets/styles/pages/Calendar.module.css';

// 현재 날짜 상태 관리
const currentDate = ref(new Date());

// 현재 년도와 월 계산
const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => currentDate.value.getMonth());

// 월 변경 함수
const changeMonth = (direction) => {
  const newDate = new Date(currentDate.value);
  newDate.setMonth(currentDate.value.getMonth() + direction);
  currentDate.value = newDate;
};

// 더미 데이터 - 실제로는 API에서 가져올 데이터
const transactionData = ref({
  9: { income: 42000, expense: -7800 },
  15: { expense: -15000 },
  22: { income: 35000 },
  28: { expense: -23000 },
});

const loanData = ref({
  9: { type: '대출', label: '대출' },
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
      />
    </div>
  </div>
</template>
