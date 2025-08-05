<script setup>
import styles from '@/assets/styles/pages/Home.module.css';
import DailySpendingWidget from '@/components/domain/Home/DailySpendingWidget.vue';
import DebtListWidget from '@/components/domain/Home/DebtListWidget.vue';
import OverdueAlertWidget from '@/components/domain/Home/OverdueAlertWidget.vue';
import RepaymentPlanWidget from '@/components/domain/Home/RepaymentPlanWidget.vue';
import TotalDebtStatusWidget from '@/components/domain/Home/TotalDebtStatusWidget.vue';
import WeeklyReportWidget from '@/components/domain/Home/WeeklyReportWidget.vue';
</script>
<template>
  <div :class="styles.page">
    <DailySpendingWidget />
    <OverdueAlertWidget />
    <WeeklyReportWidget />
    <TotalDebtStatusWidget />
    <RepaymentPlanWidget />
    <DebtListWidget />
  </div>
</template>
