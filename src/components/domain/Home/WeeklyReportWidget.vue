<script setup>
import styles from '@/assets/styles/components/home/WeeklyReportWidget.module.css';
import { defineProps, computed } from 'vue';

// 부모 컴포넌트로부터 받을 데이터(props)를 정의합니다.
const props = defineProps({
  reportData: {
    type: Object,
    required: true,
    default: () => ({
      startDate: '2025-08-04',
      endDate: '2025-08-10',
      achievementRate: 57.0,
      dailyLimit: 40000,
      averageDailySpending: 34000,
      spendingByDay: {
        MON: 35000,
        TUE: 45000,
        WED: 45000,
        THU: 35000,
        FRI: 35000,
        SAT: 45000,
        SUN: 45000,
      },
    }),
  },
});

// "8월 2주차" 와 같은 텍스트를 계산
const periodText = computed(() => {
  if (!props.reportData.startDate) return '';
  const date = new Date(props.reportData.startDate);
  const month = date.getMonth() + 1;
  const weekOfMonth = Math.ceil(date.getDate() / 7);
  return `${month}월 ${weekOfMonth}주차`;
});

// 숫자에 콤마를 찍어주는 헬퍼 함수
const formatCurrency = (value) => {
  return value ? value.toLocaleString('ko-KR') : '0';
};

const chartMetrics = computed(() => {
  if (!props.reportData)
    return {
      yAxisLabels: [60000, 45000, 30000, 15000, 0],
      chartData: [],
      limitLinePosition: 0,
    };

  const dailyLimit = props.reportData.dailyLimit;
  const spendingValues = Object.values(props.reportData.spendingByDay);

  const maxValue = Math.max(...spendingValues, dailyLimit);

  let yAxisTop = Math.ceil(maxValue / 15000) * 15000;
  if (yAxisTop === 0) yAxisTop = 60000;

  const yAxisLabels = [];
  for (let i = yAxisTop; i >= 0; i -= 15000) {
    yAxisLabels.push(i);
  }

  const dayLabels = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];
  const dayKeys = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];

  const chartData = dayKeys.map((key, index) => {
    const spentOnDay = props.reportData.spendingByDay[key] || 0;
    return {
      day: dayLabels[index],
      height:
        yAxisTop > 0
          ? (props.reportData.spendingByDay[key] / yAxisTop) * 100
          : 0,
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

const selectWeek = () => {
  // TODO: 과거 리포트 목록을 보여주는 드롭다운 또는 모달을 띄우는 로직
  console.log('주차 선택 기능 호출');
};
</script>

<template>
  <div :class="styles.widgetCard">
    <div :class="styles.widgetHeader">
      <h3>주간 리포트</h3>
    </div>

    <div :class="styles.widgetContent">
      <div :class="styles.reportSummary">
        <div :class="styles.reportPeriod">
          <button @click="selectWeek" :class="styles.periodSelector">
            {{ periodText }} <span :class="styles.arrow">▼</span>
          </button>
        </div>
        <div :class="styles.achievementRate">
          <p>목표 달성률</p>
          <p :class="styles.ratePercent">
            {{ reportData.achievementRate.toFixed(2) }}%
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
            {{ formatCurrency(reportData.averageDailySpending) }} 원
          </p>
        </div>
        <div :class="styles.divider"></div>
        <div :class="styles.summaryItem">
          <p>지난 주 소비 한도</p>
          <p :class="[styles.amount, styles.highlight]">
            {{ formatCurrency(reportData.dailyLimit) }} 원
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
