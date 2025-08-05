<script setup>
import { computed } from 'vue';
import styles from '@/assets/styles/components/Calendar/CalendarDay.module.css';

const props = defineProps({
  day: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['click']);

// 금액 포맷팅 함수
const formatAmount = (amount) => {
  if (amount >= 0) {
    return `+${amount.toLocaleString()}`;
  } else {
    return amount.toLocaleString();
  }
};

// 클래스 계산
const dayClasses = computed(() => {
  const classes = [styles.dayCell];

  if (!props.day.isCurrentMonth) {
    classes.push(styles.otherMonth);
  }

  if (props.day.isToday) {
    classes.push(styles.today);
  }

  if (props.day.isSelected) {
    classes.push(styles.selected);
  }

  // 클릭 가능한 날짜인지 확인 (데이터가 있는 날짜)
  if ((props.day.transactions || props.day.loan) && props.day.isCurrentMonth) {
    classes.push(styles.clickable);
  }

  return classes;
});

// 클릭 핸들러
const handleClick = () => {
  emit('click');
};
</script>

<template>
  <div :class="dayClasses" @click="handleClick">
    <div :class="styles.dateNumber">{{ day.date }}</div>

    <!-- 거래 내역 표시 -->
    <div v-if="day.transactions" :class="styles.transactions">
      <!-- 수익 표시 -->
      <div
        v-if="day.transactions.income"
        :class="[styles.amount, styles.income]"
      >
        {{ formatAmount(day.transactions.income) }}
      </div>

      <!-- 지출 표시 -->
      <div
        v-if="day.transactions.expense"
        :class="[styles.amount, styles.expense]"
      >
        {{ formatAmount(day.transactions.expense) }}
      </div>
    </div>

    <!-- 대출 정보 표시 -->
    <div v-if="day.loan" :class="styles.loanTag">
      {{ day.loan.label }}
    </div>
  </div>
</template>
