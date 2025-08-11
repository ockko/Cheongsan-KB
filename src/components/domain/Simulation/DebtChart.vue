<template>
  <div :class="styles.debtChartWrapper">
    <h3 :class="styles.chartTitle">부채 감소 추이</h3>
    <Line :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Line } from 'vue-chartjs';
import dayjs from 'dayjs';
import styles from '@/assets/styles/components/simulation/DebtChart.module.css';
import { useSimulationStore } from '@/stores/repayment-simulation';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale
);

const props = defineProps({
  repayments: {
    type: Array,
    default: () => [],
  },
});

const store = useSimulationStore();

const sourceRepayments = computed(() => {
  return store.repayments.length > 0 ? store.repayments : [];
});

const chartData = ref({ labels: [], datasets: [] });
const lastPaymentDate = computed(() => {
  let latest = null;

  sourceRepayments.value.forEach((strategy) => {
    Object.values(strategy.repaymentHistory).forEach((loanHistory) => {
      const lastEntry = loanHistory[loanHistory.length - 1];
      if (!latest || dayjs(lastEntry.paymentDate).isAfter(dayjs(latest))) {
        latest = lastEntry.paymentDate;
      }
    });
  });
  return latest ? dayjs(latest) : dayjs();
});

const slicedMonths = computed(() => {
  if (!lastPaymentDate.value) return [];

  const start = dayjs().startOf('month');

  // 마지막 0원 되는 달 찾기
  let lastZeroMonth = null;
  sourceRepayments.value.forEach((strategy) => {
    Object.values(strategy.repaymentHistory).forEach((loanHistory) => {
      for (let i = loanHistory.length - 1; i >= 0; i--) {
        if (loanHistory[i].remainingPrincipal === 0) {
          const zeroMonth = dayjs(loanHistory[i].paymentDate).startOf('month');
          if (!lastZeroMonth || zeroMonth.isAfter(lastZeroMonth)) {
            lastZeroMonth = zeroMonth;
          }
          break;
        }
      }
    });
  });

  // 마지막 날짜는 0원이 되는 달과 lastPaymentDate 중 늦은 날짜로 선택
  const lastMonth =
    lastZeroMonth && lastZeroMonth.isAfter(lastPaymentDate.value)
      ? lastZeroMonth
      : lastPaymentDate.value.startOf('month');

  // 마지막 달 + 6개월 여유 추가
  const endMonth = lastMonth.add(6, 'month');

  const monthsCount = endMonth.diff(start, 'month') + 1;

  const months = [];
  for (let i = 0; i < monthsCount; i++) {
    months.push(start.add(i, 'month').format('YYYY-MM'));
  }
  return months;
});

watch(
  sourceRepayments,
  (repayments) => {
    if (!Array.isArray(repayments)) return;

    //const allMonthsSet = new Set();

    repayments.forEach((strategy) => {
      Object.values(strategy.repaymentHistory).forEach((loanHistory) => {
        loanHistory.forEach((entry) => {
          const month = entry.paymentDate.slice(0, 7);
          //allMonthsSet.add(month);
        });
      });
    });
    const filteredMonths = slicedMonths.value;

    const strategiesData = repayments.map((strategy) => {
      const monthlyTotals = {};

      Object.values(strategy.repaymentHistory).forEach((loanHistory) => {
        loanHistory.forEach((entry) => {
          const month = entry.paymentDate.slice(0, 7);
          if (!monthlyTotals[month]) monthlyTotals[month] = 0;
          monthlyTotals[month] += entry.remainingPrincipal;
        });
      });

      return {
        label: mapStrategyLabel(strategy.strategyType),
        dataMap: monthlyTotals,
      };
    });

    chartData.value = {
      labels: filteredMonths,
      datasets: strategiesData.map((strategy, index) => {
        const data = filteredMonths.map((m) => strategy.dataMap[m] ?? null);

        const maxLabels = 5;
        const total = filteredMonths.length;
        const interval = total > maxLabels ? Math.ceil(total / maxLabels) : 1;

        const lastZeroIndex = filteredMonths.findIndex(
          (month) => strategy.dataMap[month] === 0
        );

        const pointRadius = filteredMonths.map((_, i) =>
          i % interval === 0 || i === lastZeroIndex ? 4 : 0
        );
        const pointHoverRadius = filteredMonths.map((_, i) =>
          i % interval === 0 || i === lastZeroIndex ? 6 : 0
        );
        return {
          label: strategy.label,
          data,
          borderColor: getColor(index),
          tension: 0.3,
          fill: false,
          pointRadius,
          pointHoverRadius,
        };
      }),
    };
  },
  { immediate: true }
);

const maxRemainingPrincipal = computed(() => {
  if (!sourceRepayments.length) return 0;

  let maxVal = 0;
  sourceRepayments.value.forEach((strategy) => {
    Object.values(strategy.repaymentHistory).forEach((loanHistory) => {
      loanHistory.forEach((entry) => {
        if (entry.remainingPrincipal > maxVal)
          maxVal = entry.remainingPrincipal;
      });
    });
  });

  return maxVal;
});

const chartOptions = computed(() => {
  const totalLabels = chartData.value.labels.length;
  // x축 라벨 최대 5개 나오도록 간격 계산 (최소 1)
  const maxLabels = 5;
  const interval =
    totalLabels > maxLabels ? Math.ceil(totalLabels / maxLabels) : 1;

  return {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom' },
      tooltip: {
        callbacks: {
          label: (context) => `\u20a9${context.formattedValue}`,
        },
      },
    },
    scales: {
      x: {
        ticks: {
          callback: (val, index) => {
            if (index === 0) return '현재';
            if (index % interval === 0) return `${index}개월`;
            return '';
          },
          maxRotation: 0,
          autoSkip: false,
        },
        grid: {
          drawTicks: true,
          drawOnChartArea: true,
          color: function (context) {
            const index = context.tick?.index;
            return index !== undefined && index % interval === 0
              ? '#ccc'
              : 'transparent';
          },
        },
      },

      y: {
        beginAtZero: true,
        suggestedMax:
          Math.ceil(maxRemainingPrincipal.value / 1000000) * 1000000 + 1000000,
        ticks: {
          stepSize: 1000000,
          maxTicksLimit: 10,
          callback: (value) => {
            if (value === 0) return '';
            if (value >= 100000000) {
              const 억 = Math.floor(value / 100000000);
              const 만원 = Math.floor((value % 100000000) / 10000);
              return 만원 > 0
                ? `${억}억 ${만원.toLocaleString()}만원`
                : `${억}억`;
            } else {
              return `${(value / 10000).toLocaleString()}만원`;
            }
          },
        },
        grid: {
          drawOnChartArea: false,
        },
      },
    },
  };
});

function getColor(index) {
  const colors = ['#003e65', '#98cbfd', '#dc2121', '#4e6073'];
  return colors[index % colors.length];
}

function mapStrategyLabel(type) {
  switch (type) {
    case 'TCS_RECOMMEND':
      return '우리 앱 추천';
    case 'HIGH_INTEREST_FIRST':
      return '고금리 우선';
    case 'SMALL_AMOUNT_FIRST':
      return '소액 우선';
    case 'OLDEST_FIRST':
      return '오래된 순';
    default:
      return type;
  }
}
</script>
