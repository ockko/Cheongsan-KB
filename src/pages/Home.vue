<script setup>
import styles from '@/assets/styles/pages/Home.module.css';
import DailySpendingWidget from '@/components/domain/Home/DailySpendingWidget.vue';
import DebtListWidget from '@/components/domain/Home/DebtListWidget.vue';
import OverdueAlertWidget from '@/components/domain/Home/OverdueAlertWidget.vue';
import RepaymentPlanWidget from '@/components/domain/Home/RepaymentPlanWidget.vue';
import TotalDebtRepaymentWidget from '@/components/domain/Home/TotalDebtRepaymentWidget.vue';
import WeeklyReportWidget from '@/components/domain/Home/WeeklyReportWidget.vue';
import { ref } from 'vue';

const debtList = ref([
  {
    debtId: 1,
    debtName: '짱 무서운 대출',
    organizationName: '태현론',
    originalAmount: 100000000.0,
    currentBalance: 3556000000.0,
    repaymentRate: -34.56,
    interestRate: 9.2,
    loanStartDate: '2024-08-01',
    loanEndDate: '2027-08-01',
  },
  {
    debtId: 2,
    debtName: '가계일반자금대출(일시상환)',
    organizationName: '국민은행',
    originalAmount: 5400000.0,
    currentBalance: 3088000.0,
    repaymentRate: 0.428,
    interestRate: 3.8,
    loanStartDate: '2024-07-14',
    loanEndDate: '2026-07-14',
  },
  {
    debtId: 3,
    debtName: '학자금 대출(분할상환)',
    organizationName: '국민은행',
    originalAmount: 1226000.0,
    currentBalance: 500000.0,
    repaymentRate: 0.592,
    interestRate: 2.7,
    loanStartDate: '2023-06-01',
    loanEndDate: '2027-06-01',
  },
]);
</script>

<template>
  <div :class="styles.page">
    <div :class="styles.widgetsContainer">
      <DailySpendingWidget :class="styles.widget" />
      <OverdueAlertWidget :class="styles.widget" />
    </div>
    <WeeklyReportWidget />
    <div :class="styles.widgetsContainer">
      <TotalDebtRepaymentWidget :class="styles.widget" />
      <RepaymentPlanWidget :class="styles.widget" />
    </div>
    <DebtListWidget :debts="debtList" />
  </div>
</template>
