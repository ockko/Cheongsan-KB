<script setup>
import styles from "@/assets/styles/components/Analysis.module.css";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import * as echarts from "echarts";
import { computed, onMounted, onBeforeUnmount, ref, watch } from "vue";

const props = defineProps({
  interestComparison: { type: Object, required: true },
});

const chartRef = ref(null);
let chart;

const formatCurrency = (n) => {
  const v = Math.abs(Number(n) || 0);
  if (v >= 100000000) return `${(v / 100000000).toFixed(1)}억원`;
  if (v >= 10000) return `${Math.round(v / 10000)}만원`;
  return `${v.toLocaleString()}원`;
};

const base = computed(() =>
  Number(props.interestComparison?.existingInterest || 0)
);
const withNew = computed(() =>
  Number(props.interestComparison?.newLoanInterest || 0)
);
const deltaInterest = computed(() => withNew.value - base.value);

const render = () => {
  if (!chartRef.value) return;
  if (!chart) chart = echarts.init(chartRef.value);

  const yStart = 0.2;
  const yValue = 0.6;

  // 6등분용 ‘예쁜’ 스텝 계산 (만원단위 보기 좋게 반올림)
  const niceCeil = (v) => {
    if (v <= 0) return 1;
    const p = Math.pow(10, Math.floor(Math.log10(v)));
    const m = Math.ceil(v / p);
    const k = m <= 1 ? 1 : m <= 2 ? 2 : m <= 5 ? 5 : 10;
    return k * p;
  };

  const d = Number(deltaInterest.value || 0); // 원 단위
  const isNeg = d < 0;
  const step = niceCeil(Math.abs(d) / 5); // 5구간 → 6라벨
  const axisMax = step * 5;

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "line" },
      formatter: () => `Δ ${d >= 0 ? "+" : "-"}${formatCurrency(d)}`,
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: "value",
      // 0 포함하도록 한쪽만 열기
      min: isNeg ? -axisMax : 0,
      max: isNeg ? 0 : axisMax,
      splitNumber: 5, // ⇒ 라벨은 6개
      name: "금액(만원)",
      nameLocation: "end",
      nameGap: 25,
      axisLabel: {
        formatter: (value) => `${(value / 10000).toLocaleString()}만원`,
      },
      axisPointer: {
        label: {
          formatter: ({ value }) => `${(value / 10000).toLocaleString()}만원`,
        },
      },
    },
    yAxis: { type: "value", show: false, min: 0, max: 0.8 },
    series: [
      {
        type: "scatter",
        symbolSize: 16,
        data: [[0, yStart]],
        name: "기존 총 이자",
      },
      {
        type: "scatter",
        symbolSize: 16,
        data: [[d, yValue]],
        name: "신규 포함 총 이자",
      },
      {
        type: "line",
        data: [
          [0, yStart],
          [d, yValue],
        ],
        lineStyle: { width: 4 },
        showSymbol: false,
        tooltip: { show: false },
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
  () => props.interestComparison,
  () => render(),
  { deep: true }
);
</script>

<template>
  <div :class="styles.chartContainer">
    <div :class="styles.alert">
      <FontAwesomeIcon :icon="['fas', 'bell']" :class="styles.faBell" />
      <span>
        총 이자 비용이
        <b>{{ formatCurrency(deltaInterest) }}</b>
        {{ deltaInterest >= 0 ? "증가" : "감소" }}했습니다.
      </span>
    </div>

    <div :class="styles.dumbbellChartContainer">
      <div ref="chartRef" :class="styles.dumbbellChart"></div>
    </div>
  </div>
</template>
