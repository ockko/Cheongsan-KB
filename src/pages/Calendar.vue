<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useCalendarStore } from '@/stores/calendar';
import CalendarHeader from '@/components/domain/Calendar/CalendarHeader.vue';
import CalendarGrid from '@/components/domain/Calendar/CalendarGrid.vue';
import CalendarDetailView from '@/components/domain/Calendar/CalendarDetailView.vue';
import styles from '@/assets/styles/pages/Calendar.module.css';

// Store 사용
const calendarStore = useCalendarStore();

// 현재 날짜 상태 관리
const currentDate = ref(new Date());
const selectedDate = ref(null);

// 현재 년도와 월 계산
const currentYear = computed(() => currentDate.value.getFullYear());
// const currentMonth = computed(() => currentDate.value.getMonth());
const currentMonth = computed(() => currentDate.value.getMonth() + 1); // API는 1-12, Date는 0-11

// 월 변경 함수
const changeMonth = async (direction) => {
  const newDate = new Date(currentDate.value);
  newDate.setMonth(currentDate.value.getMonth() + direction);
  currentDate.value = newDate;
  selectedDate.value = null; // 월 변경 시 선택된 날짜 초기화

  // 새로운 월의 데이터 로드
  await loadMonthlyData();
};

// 월별 데이터 로드
const loadMonthlyData = async () => {
  await calendarStore.loadMonthlyData(currentYear.value, currentMonth.value);
};

// 날짜 클릭 핸들러
const handleDateClick = async (date) => {
  // 해당 날짜에 데이터가 있는지 확인
  const hasTransactionData = calendarStore.transactionDataByDate[date];
  const hasLoanData = calendarStore.loanDataByDate[date];

  if (hasTransactionData || hasLoanData) {
    if (selectedDate.value === date) {
      // 같은 날짜 클릭 시 토글
      selectedDate.value = null;
    } else {
      // 새로운 날짜 선택
      selectedDate.value = date;

      // 상세 데이터 로드
      const dateString = calendarStore.formatDateString(
        currentYear.value,
        currentMonth.value,
        date
      );
      await calendarStore.loadDailyDetails(dateString);
    }
  } else {
    selectedDate.value = null; // 데이터가 없으면 상세보기 숨김
  }
};

// 선택된 날짜의 상세 데이터
const selectedDateData = computed(() => {
  if (!selectedDate.value || !calendarStore.selectedDateDetails) return null;

  const { transactions, repayments } = calendarStore.selectedDateDetails;

  return {
    date: selectedDate.value,
    transactions: transactions,
    loans: repayments,
  };
});

// 컴포넌트 마운트 시 초기 데이터 로드
onMounted(async () => {
  await loadMonthlyData();
});

// 에러 상태 감시
watch(
  () => calendarStore.error,
  (newError) => {
    if (newError) {
      console.error('Calendar Error:', newError);
      // 필요시 사용자에게 에러 알림 표시
    }
  }
);
</script>

<template>
  <div :class="styles.pageWrapper">
    <div :class="styles.page">
      <!-- 로딩 표시 -->
      <div v-if="calendarStore.loading" :class="styles.loading">
        데이터를 불러오는 중...
      </div>

      <CalendarHeader
        :year="currentYear"
        :month="currentMonth - 1"
        @change-month="changeMonth"
      />
      <div :class="styles.calendarGridWrapper">
        <!-- 로고 배경을 CalendarGrid 영역에만 적용 -->
        <div :class="styles.logoBackground"></div>

        <CalendarGrid
          :year="currentYear"
          :month="currentMonth - 1"
          :transaction-data="calendarStore.transactionDataByDate"
          :loan-data="calendarStore.loanDataByDate"
          :selected-date="selectedDateData"
          @date-click="handleDateClick"
        />
      </div>

      <!-- 날짜의 상세내역 -->
      <CalendarDetailView v-if="selectedDateData" :data="selectedDateData" />

      <!-- 에러 표시 -->
      <div v-if="calendarStore.error" :class="styles.error">
        {{ calendarStore.error }}
        <button @click="calendarStore.clearError">닫기</button>
      </div>
    </div>
  </div>
</template>
