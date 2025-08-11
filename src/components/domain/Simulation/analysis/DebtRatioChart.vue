<script setup>
import { computed } from "vue";
import styles from "@/assets/styles/components/Analysis.module.css";

const props = defineProps({
  dsr: { type: [Number, String], default: 0 },
  totalComparison: { type: Object, default: () => ({}) },
});

const dsrPercent = computed(() => {
  const v = Number(props.dsr);
  if (Number.isNaN(v)) return 0;
  return Math.min(100, Math.max(0, v));
});

const CIRCUMFERENCE = 2 * Math.PI * 70;

const getDebtColor = (percent) => {
  if (percent <= 20) return "#10b981";
  if (percent <= 40) return "#f59e0b";
  return "#dc2626";
};

const getDashArray = (percent) => {
  const p = Math.min(100, Math.max(0, Number(percent) || 0));
  const filled = (CIRCUMFERENCE * p) / 100;
  const empty = CIRCUMFERENCE - filled;
  return `${filled} ${empty}`;
};

const formatPercent = (percent) => {
  const n = Number(percent);
  return `${Number.isNaN(n) ? 0 : n.toFixed(1)}%`;
};
</script>

<template>
  <div :class="styles.chartContainer">
    <div :class="styles.alert">
      <FontAwesomeIcon :icon="['fas', 'bell']" :class="styles.faBell" />
      <span>
        변경된 DSR 비율은
        <span :class="styles.highlight">{{ formatPercent(dsrPercent) }}</span>
        입니다.
      </span>
    </div>

    <div :class="styles.donutChartContainer">
      <svg :class="styles.donutChart" viewBox="0 0 200 200">
        <circle
          cx="100"
          cy="100"
          r="70"
          fill="none"
          stroke="#f3f4f6"
          stroke-width="20"
        />

        <circle
          cx="100"
          cy="100"
          r="70"
          fill="none"
          :stroke="getDebtColor(dsrPercent)"
          stroke-width="20"
          :stroke-dasharray="getDashArray(dsrPercent)"
          stroke-dashoffset="0"
          :class="styles.debtArc"
          transform="rotate(-90 100 100)"
        />

        <text
          x="100"
          y="95"
          text-anchor="middle"
          font-size="24"
          font-weight="bold"
          :fill="getDebtColor(dsrPercent)"
        >
          {{ formatPercent(dsrPercent) }}
        </text>
        <text
          x="100"
          y="115"
          text-anchor="middle"
          font-size="12"
          fill="#6b7280"
        >
          DSR 비율
        </text>
      </svg>

      <div :class="styles.debtLegend">
        <div :class="styles.legendItem">
          <div :class="[styles.legendColor, styles.safeColor]"></div>
          <span>안전 (20% 이하)</span>
        </div>
        <div :class="styles.legendItem">
          <div :class="[styles.legendColor, styles.warningColor]"></div>
          <span>주의 (20–40%)</span>
        </div>
        <div :class="styles.legendItem">
          <div :class="[styles.legendColor, styles.dangerColor]"></div>
          <span>위험 (40% 이상)</span>
        </div>
      </div>
    </div>
  </div>
</template>
