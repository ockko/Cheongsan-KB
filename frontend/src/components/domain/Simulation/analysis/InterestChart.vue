<script setup>
import styles from '@/assets/styles/components/Analysis.module.css';
import * as echarts from 'echarts';
import { onMounted, onBeforeUnmount, ref, watch } from 'vue';

const props = defineProps({
  monthlyChartData: {
    type: Object,
    default: () => ({ dates: [], existing: [], after: [] }),
  },
});

const chartRef = ref(null);
let chart;

const formatCurrency = (n) => {
  const v = Number(n) || 0;
  if (v >= 100000000) return `${(v / 100000000).toFixed(1)}억원`;
  if (v >= 10000) return `${Math.round(v / 10000)}만원`;
  return `${v.toLocaleString()}원`;
};

const render = () => {
  if (!chartRef.value) return;
  if (!chart) chart = echarts.init(chartRef.value);

  const existingData = props.monthlyChartData.existing;
  const afterData = props.monthlyChartData.after;

  // 기존 이자와 신규 대출 포함 이자가 같아지는 지점 찾기
  let sliceIndex = existingData.length;
  for (let i = 1; i < existingData.length; i++) {
    if (Math.abs(existingData[i] - afterData[i]) < 1000) {
      // 1000원 이하 차이면 같다고 간주
      sliceIndex = i + 1;
      break;
    }
  }

  const dates = props.monthlyChartData.dates.slice(0, sliceIndex);
  const existing = props.monthlyChartData.existing.slice(0, sliceIndex);
  const after = props.monthlyChartData.after.slice(0, sliceIndex);

  const allData = [...existing, ...after];
  const maxY = Math.max(...allData) * 1.1;
  const minY = 0;

  // x축 라벨 간격 계산 (최대 10개)
  const maxLabels = 8;
  const interval = Math.ceil(dates.length / maxLabels);

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        if (!params || params.length === 0) return '';
        // x축 날짜 가져오기
        const rawDate = params[0].axisValue; // "2025-08-15" 같은 문자열
        const dateObj = new Date(rawDate);
        const year = dateObj.getFullYear();
        const month = dateObj.getMonth() + 1; // 0부터 시작하므로 +1
        const formattedDate = `${year}년 ${month}월`;

        const lines = params.map(
          (p) => `${p.seriesName}: ${formatCurrency(p.data)}`
        );

        return `<b>${formattedDate}</b><br/>` + lines.join('<br/>');
      },
    },

    legend: { data: ['이전 이자', '신규 대출 포함 이자'], bottom: 0 },
    grid: { left: 60, right: 20, top: 50, bottom: 80 },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false,
      axisLabel: {
        rotate: 45,
        interval: interval - 1, // interval 맞춰서 10개 이하 표시
        formatter: (value, index) => {
          if (index === 0) return '현재';
          if (index % interval === 0) return `${index}개월`;
          return '';
        },
      },
    },
    yAxis: {
      type: 'value',
      min: minY,
      max: maxY,
      nameLocation: 'end',
      nameGap: 25,
      axisLabel: { formatter: (v) => formatCurrency(v) },
    },
    series: [
      {
        name: '이전 이자',
        type: 'line',
        data: existing,
        areaStyle: { color: 'rgba(0,123,255,0.2)' },
        lineStyle: { width: 2, color: '#007bff' },
        smooth: true,
      },
      {
        name: '신규 대출 포함 이자',
        type: 'line',
        data: after,
        areaStyle: { color: 'rgba(255,87,34,0.2)' },
        lineStyle: { width: 2, color: '#ff5722' },
        smooth: true,
      },
    ],
  };

  chart.setOption(option, true);
};

onMounted(() => {
  render();
  const ro = new ResizeObserver(() => chart && chart.resize());
  ro.observe(chartRef.value);
  chartRef.value._ro = ro;
});

onBeforeUnmount(() => {
  if (chartRef.value?._ro) chartRef.value._ro.disconnect();
  if (chart) {
    chart.dispose();
    chart = null;
  }
});

watch(
  () => props.monthlyChartData,
  () => render(),
  { deep: true }
);
</script>
<template>
  <div :class="styles.areaChartContainer">
    <div :class="styles.alert">
      <i class="fas fa-bell" :class="styles.faBell"></i>
      <span>
        기존 대출과 신규 대출을 포함한
        <span :class="styles.highlight">월별 이자 추이</span>입니다.
      </span>
    </div>

    <div ref="chartRef" :class="styles.chart"></div>
  </div>
</template>
