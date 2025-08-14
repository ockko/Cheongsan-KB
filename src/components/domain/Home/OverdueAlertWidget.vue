<script setup>
import styles from '@/assets/styles/components/home/OverdueAlertWidget.module.css';
import { computed, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useDebtStore } from '@/stores/debt';

const props = defineProps({
  class: {
    type: String,
    default: '',
  },
});

const debtStore = useDebtStore();
const { overdueLoans, isLoading } = storeToRefs(debtStore);

onMounted(() => {
  debtStore.fetchOverdueLoans();
});

// overdueDays를 기준으로 내림차순 정렬
const sortedOverdueLoans = computed(() =>
  [...overdueLoans.value].sort((a, b) => b.overdueDays - a.overdueDays)
);

// D-day(overdueDays) 값에 따라 아이콘을 반환하는 함수
const getDdayIcon = (overdueDays) => {
  if (overdueDays > 7) return '🚨'; // 연체 7일 초과
  return '❗'; // 연체 7일 이하
};
</script>

<template>
  <div :class="[styles.widgetCard, props.class]">
    <div v-if="sortedOverdueLoans.length > 0" :class="styles.contentWrapper">
      <p :class="styles.title">
        ※ 연체 중인 대출이
        <span :class="styles.highlight">{{ sortedOverdueLoans.length }}</span
        >건<br />존재합니다.
      </p>
      <ul>
        <li
          v-for="(loan, index) in sortedOverdueLoans"
          :key="'overdue-' + index"
          :class="styles.loanItem"
        >
          <div :class="styles.dDay">
            <span :class="styles.icon">{{
              getDdayIcon(loan.overdueDays)
            }}</span>
            <span> D+{{ loan.overdueDays }} </span>
          </div>
          <span>{{ loan.debtName }}</span>
        </li>
      </ul>
    </div>

    <div v-else :class="styles.contentWrapper">
      <p :class="styles.subtitle">※ 연체 중인 대출이 존재하지 않습니다.</p>
    </div>
  </div>
</template>
