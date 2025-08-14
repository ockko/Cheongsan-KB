<script setup>
import styles from '@/assets/styles/components/home/WeeklyReportWidget.module.css';
import { computed, onMounted, ref, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useReportStore } from '@/stores/report';

const reportStore = useReportStore();
const { currentReport, reportHistory, isLoading } = storeToRefs(reportStore);

const selectedStartDate = ref('');

watch(
  currentReport,
  (newReport) => {
    if (newReport) {
      selectedStartDate.value = newReport.startDate;
    }
  },
  { immediate: true }
);

onMounted(() => {
  reportStore.fetchLatestReport();
  reportStore.fetchReportHistoryList();
});

// 사용자가 드롭다운에서 특정 주차를 선택했을 때 실행될 메소드
const handleWeekSelect = (event) => {
  const selectedStartDate = event.target.value;
  if (selectedStartDate) {
    reportStore.fetchReportByDate(selectedStartDate);
  }
};

// 숫자에 콤마를 찍어주는 헬퍼 함수
const formatCurrency = (value) => {
  return value ? value.toLocaleString('ko-KR') : '0';
};

const chartMetrics = computed(() => {
  if (!currentReport.value)
    return {
      yAxisLabels: [],
      chartData: [],
      limitLinePosition: 0,
      yAxisTop: 0,
    };

  const { dailyLimit, spendingByDay } = currentReport.value;
  const spendingValues = Object.values(spendingByDay);
  const maxValue = Math.max(...spendingValues, dailyLimit, 1);

  let yAxisTop = Math.ceil(maxValue / 15000) * 15000;
  if (yAxisTop === 0) yAxisTop = 60000;

  const yAxisLabels = [];
  for (let i = yAxisTop; i >= 0; i -= 15000) {
    yAxisLabels.push(i);
  }

  const dayLabels = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];
  const dayKeys = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];

  const chartData = dayKeys.map((key, index) => {
    const spentOnDay = spendingValues[index] || 0;
    return {
      day: dayLabels[index],
      height: yAxisTop > 0 ? (spentOnDay / yAxisTop) * 100 : 0,
      isOverLimit: spentOnDay > dailyLimit,
    };
  });

  const limitLinePosition = yAxisTop > 0 ? (dailyLimit / yAxisTop) * 100 : 0;

  return { yAxisLabels, chartData, limitLinePosition, yAxisTop };
});

const calculateLinePosition = (labelValue) => {
  const yAxisTop = chartMetrics.value.yAxisTop;
  if (yAxisTop > 0) {
    return (labelValue / yAxisTop) * 100;
  }
  return 0;
};
</script>

<template>
  <div :class="styles.widgetCard">
    <div :class="styles.widgetHeader">
      <h3>주간 리포트</h3>
    </div>

    <div v-if="isLoading" :class="styles.loading">
      리포트를 불러오는 중입니다...
    </div>

    <div v-else-if="currentReport" :class="styles.widgetContent">
      <div :class="styles.reportSummary">
        <div :class="styles.reportPeriod">
          <select
            v-model="selectedStartDate"
            @change="handleWeekSelect"
            :class="styles.periodSelector"
          >
            <option
              v-for="history in reportHistory"
              :key="history.reportId"
              :value="history.startDate"
            >
              {{ history.month }}월 {{ history.weekOfMonth }}주차
            </option>
          </select>
        </div>
        <div :class="styles.achievementRate">
          <p>목표 달성률</p>
          <p :class="styles.ratePercent">
            {{ currentReport.achievementRate.toFixed(0) }}%
          </p>
        </div>
      </div>

      <div :class="styles.chartContainer">
        <div :class="styles.yAxis">
          <span v-for="label in chartMetrics.yAxisLabels" :key="label">
            {{ formatCurrency(label) }}
          </span>
        </div>
        <div :class="styles.chartArea">
          <div :class="styles.chartBars">
            <div
              v-for="label in chartMetrics.yAxisLabels"
              :key="'line-' + label"
              :class="styles.gridLine"
              :style="{ bottom: calculateLinePosition(label) + '%' }"
            ></div>
            <div
              :class="styles.limitLine"
              :style="{ bottom: chartMetrics.limitLinePosition + '%' }"
            ></div>
            <div
              v-for="(item, index) in chartMetrics.chartData"
              :key="index"
              :class="[styles.bar, { [styles.overLimit]: item.isOverLimit }]"
              :style="{ height: item.height + '%' }"
            ></div>
          </div>
          <div :class="styles.xAxis">
            <span
              v-for="item in chartMetrics.chartData"
              :key="item.day"
              :class="styles.dayLabel"
              >{{ item.day }}</span
            >
          </div>
        </div>
      </div>

      <div :class="styles.bottomSummary">
        <div :class="[styles.summaryItem, styles.average]">
          <p>일일 평균 지출</p>
          <p :class="styles.amount">
            {{ formatCurrency(currentReport.averageDailySpending) }} 원
          </p>
        </div>
        <div :class="styles.divider"></div>
        <div :class="styles.summaryItem">
          <p>지난 주 소비 한도</p>
          <p :class="[styles.amount, styles.highlight]">
            {{ formatCurrency(currentReport.dailyLimit) }} 원
          </p>
        </div>
      </div>
    </div>

    <div v-else :class="styles.noData">리포트 데이터가 없습니다.</div>
  </div>
</template>
