<script setup>
import styles from '@/assets/styles/components/home/OverdueAlertWidget.module.css';
import { defineProps, computed } from 'vue';

// 부모 컴포넌트로부터 받을 부채 목록 데이터(props)를 정의합니다.
const props = defineProps({
  // API 응답 데이터 구조와 동일하게 loans를 받습니다.
  loans: {
    type: Array,
    required: true,
    default: () => [
      // 예시 데이터
      {
        debtName: '가계일반자금대출(일시상환)',
        organizationName: '국민은행',
        overdueDays: 2435,
      },
      {
        debtName: '베리 익스펜시브 새로운 대출',
        organizationName: '베리굿 대출기관',
        overdueDays: 23,
      },
      {
        debtName: '짱 무서운 대출',
        organizationName: '태현론',
        overdueDays: 6,
      },
    ],
  },
});

// API 응답은 연체된 대출만 포함하므로, 받은 데이터를 그대로 사용합니다.
// overdueDays를 기준으로 내름차순 정렬합니다.
const overdueLoans = computed(() =>
  [...props.loans].sort((a, b) => b.overdueDays - a.overdueDays)
);

// D-day(overdueDays) 값에 따라 아이콘을 반환하는 함수
const getDdayIcon = (overdueDays) => {
  if (overdueDays > 7) return '🚨'; // 연체 7일 초과
  return '❗'; // 연체 7일 이하
};
</script>

<template>
  <div :class="styles.widgetCard">
    <div v-if="overdueLoans.length > 0" :class="styles.contentWrapper">
      <p :class="styles.title">
        ※ 연체 중인 대출이
        <span :class="styles.highlight">{{ overdueLoans.length }}</span
        >건<br />존재합니다.
      </p>
      <ul>
        <li
          v-for="(loan, index) in overdueLoans"
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
