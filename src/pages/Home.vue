<script setup>
import styles from '@/assets/styles/pages/Home.module.css';
import DailySpendingWidget from '@/components/domain/home/DailySpendingWidget.vue';
import DebtListWidget from '@/components/domain/home/DebtListWidget.vue';
import OverdueAlertWidget from '@/components/domain/home/OverdueAlertWidget.vue';
import RepaymentPlanWidget from '@/components/domain/home/RepaymentPlanWidget.vue';
import TotalDebtRepaymentWidget from '@/components/domain/home/TotalDebtRepaymentWidget.vue';
import WeeklyReportWidget from '@/components/domain/home/WeeklyReportWidget.vue';
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
    <DebtListWidget />
  </div>
</template>
