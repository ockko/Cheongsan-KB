<script setup>
import { computed } from 'vue';
import styles from '@/assets/styles/components/Calendar/CalendarHeader.module.css';

const props = defineProps({
  year: {
    type: Number,
    required: true,
  },
  month: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(['change-month']);

// 월 이름 배열
const monthNames = [
  '1월',
  '2월',
  '3월',
  '4월',
  '5월',
  '6월',
  '7월',
  '8월',
  '9월',
  '10월',
  '11월',
  '12월',
];

const monthName = computed(() => monthNames[props.month]);

const previousMonth = () => {
  emit('change-month', -1);
};

const nextMonth = () => {
  emit('change-month', 1);
};
</script>

<template>
  <div :class="styles.header">
    <div class="text-light" style="color: var(--color-main)">{{ year }}년</div>

    <div :class="styles.monthNavigation">
      <button :class="styles.navButton" @click="previousMonth" type="button">
        <img src="/images/left-arrow.png" alt="이전 달" :class="styles.arrow" />
      </button>

      <div :class="styles.monthDisplay">{{ monthName }}</div>

      <button :class="styles.navButton" @click="nextMonth" type="button">
        <img
          src="/images/right-arrow.png"
          alt="다음 달"
          :class="styles.arrow"
        />
      </button>
    </div>
  </div>
</template>
