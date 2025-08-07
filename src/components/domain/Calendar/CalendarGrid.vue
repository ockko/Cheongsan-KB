<script setup>
import { computed } from 'vue';
import CalendarDay from './CalendarDay.vue';
import styles from '@/assets/styles/components/Calendar/CalendarGrid.module.css';

const props = defineProps({
  year: {
    type: Number,
    required: true,
  },
  month: {
    type: Number,
    required: true,
  },
  transactionData: {
    type: Object,
    default: () => ({}),
  },
  loanData: {
    type: Object,
    default: () => ({}),
  },
  selectedDate: {
    type: Object, // selectedDateData 객체 전체를 받음
    default: null,
  },
});

const emit = defineEmits(['date-click']);

// 요일 헤더
const weekdays = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

// 선택된 날짜 번호 계산
const selectedDateNumber = computed(() => {
  return props.selectedDate ? props.selectedDate.date : null;
});

// 달력 날짜 계산
const calendarDays = computed(() => {
  const firstDay = new Date(props.year, props.month, 1);
  const lastDay = new Date(props.year, props.month + 1, 0);
  const startDate = new Date(firstDay);
  startDate.setDate(firstDay.getDate() - firstDay.getDay());

  const days = [];
  const currentDate = new Date(startDate);

  // 5주 표시 (35일)
  for (let i = 0; i < 35; i++) {
    const day = currentDate.getDate();
    const isCurrentMonth = currentDate.getMonth() === props.month;
    const isToday =
      currentDate.getFullYear() === new Date().getFullYear() &&
      currentDate.getMonth() === new Date().getMonth() &&
      currentDate.getDate() === new Date().getDate();

    const transactions = isCurrentMonth
      ? props.transactionData[day] || null
      : null;
    const loan = isCurrentMonth ? props.loanData[day] || null : null;

    days.push({
      date: day,
      fullDate: new Date(currentDate),
      isCurrentMonth,
      isToday,
      transactions: transactions,
      loan: loan,
      isSelected: selectedDateNumber.value === day && isCurrentMonth,
    });

    currentDate.setDate(currentDate.getDate() + 1);
  }

  return days;
});

// 날짜 클릭 핸들러
const handleDayClick = (day) => {
  if (day.isCurrentMonth) {
    emit('date-click', day.date);
  }
};
</script>

<template>
  <div :class="styles.calendarContainer">
    <!-- 요일 헤더 -->
    <div :class="styles.weekdaysHeader">
      <div v-for="weekday in weekdays" :key="weekday" :class="styles.weekday">
        {{ weekday }}
      </div>
    </div>

    <!-- 달력 그리드 -->
    <div :class="styles.calendarGrid">
      <CalendarDay
        v-for="(day, index) in calendarDays"
        :key="index"
        :day="day"
        :has-selected-date="selectedDateNumber !== null"
        @click="handleDayClick(day)"
      />
    </div>
  </div>
</template>
