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
});

// 요일 헤더
const weekdays = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

// 달력 날짜 계산
const calendarDays = computed(() => {
  const firstDay = new Date(props.year, props.month, 1);
  const lastDay = new Date(props.year, props.month + 1, 0);
  const startDate = new Date(firstDay);
  startDate.setDate(firstDay.getDate() - firstDay.getDay());

  const days = [];
  const currentDate = new Date(startDate);

  // 6주 표시 (42일)
  for (let i = 0; i < 42; i++) {
    const day = currentDate.getDate();
    const isCurrentMonth = currentDate.getMonth() === props.month;
    const isToday =
      currentDate.getFullYear() === new Date().getFullYear() &&
      currentDate.getMonth() === new Date().getMonth() &&
      currentDate.getDate() === new Date().getDate();

    days.push({
      date: day,
      fullDate: new Date(currentDate),
      isCurrentMonth,
      isToday,
      transactions: props.transactionData[day] || null,
      loan: props.loanData[day] || null,
    });

    currentDate.setDate(currentDate.getDate() + 1);
  }

  return days;
});
</script>

<template>
  <div :class="styles.calendarContainer">
    <!-- 로고 배경 -->
    <div :class="styles.logoBackground"></div>

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
      />
    </div>
  </div>
</template>
