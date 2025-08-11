<script setup>
import { computed } from "vue";
import styles from "@/assets/styles/components/Analysis.module.css";

const props = defineProps({
  totalComparison: {
    type: Object,
    default: () => ({ originalTotal: 0, newLoanTotal: 0 }),
  },
});

const currentAmount = computed(() =>
  Number(props.totalComparison?.originalTotal ?? 0)
);

const futureAmount = computed(() =>
  Number(props.totalComparison?.newLoanTotal ?? 0)
);

const difference = computed(() => futureAmount.value - currentAmount.value);

const formatCurrency = (amount) => {
  const n = Number(amount || 0);
  const absAmount = Math.abs(n);
  if (absAmount >= 100000000) {
    return `${(absAmount / 100000000).toFixed(1)}억원`;
  } else if (absAmount >= 10000) {
    return `${Math.round(absAmount / 10000)}만원`;
  } else {
    return `${absAmount.toLocaleString()}원`;
  }
};

const getBarHeight = (amount) => {
  const a = Number(amount || 0);
  const maxAmount = Math.max(currentAmount.value, futureAmount.value);
  const minHeight = 60;
  const maxHeight = 120;

  if (!maxAmount) return minHeight;

  const ratio = a / maxAmount;
  return minHeight + (maxHeight - minHeight) * ratio;
};
</script>

<template>
  <div :class="styles.chartContainer">
    <div :class="styles.alert">
      <FontAwesomeIcon :icon="['fas', 'bell']" :class="styles.faBell" />
      <span>
        대출금이
        <span :class="styles.highlight">{{ formatCurrency(difference) }}</span>
        {{ difference >= 0 ? "증가" : "감소" }}합니다.
      </span>
    </div>

    <div :class="styles.barChart">
      <div :class="styles.barContainer">
        <div :class="styles.barGroup">
          <div :class="styles.barValue">
            {{ formatCurrency(currentAmount) }}
          </div>
          <div
            :class="[styles.bar, styles.currentBar]"
            :style="{ height: `${getBarHeight(currentAmount)}px` }"
          ></div>
          <div :class="styles.barLabel">현재</div>
        </div>

        <img
          src="@/assets/icons/chartArrow.svg"
          alt="증가화살표"
          style="width: 100px; height: 100px"
        />

        <div :class="styles.barGroup">
          <div :class="styles.barValue">
            {{ formatCurrency(futureAmount) }}
          </div>
          <div
            :class="[styles.bar, styles.futureBar]"
            :style="{ height: `${getBarHeight(futureAmount)}px` }"
          ></div>
          <div :class="styles.barLabel">예상</div>
        </div>
      </div>
    </div>
  </div>
</template>
