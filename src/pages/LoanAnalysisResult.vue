<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";

import styles from "@/assets/styles/pages/LoanAnalysisResult.module.css";
import BackHeader from "@/components/common/BackHeader.vue";
import AlertBox from "@/components/domain/simulation/analysis/AlertBox.vue";
import ImpactAnalysisHeader from "@/components/domain/simulation/analysis/ImpactAnalysisHeader.vue";
import LoanChangeChart from "@/components/domain/simulation/analysis/LongChangeChart.vue";
import InterestChart from "@/components/domain/simulation/analysis/InterestChart.vue";
import DebtRatioChart from "@/components/domain/simulation/analysis/DebtRatioChart.vue";
import Recommendations from "@/components/domain/simulation/analysis/Recommendations.vue";
import LoanRecommendations from "@/components/domain/simulation/analysis/LoanRecommendations.vue";

const route = useRoute();
const analysisData = ref(null);

const readFromState = () => {
  const s = window.history.state;
  const r = s?.usr?.result ?? s?.result ?? null;
  return r?.data ?? r ?? null;
};

const fetchAnalysisData = async () => {
  try {
    // 1) router state 우선
    const fromState = readFromState();
    if (fromState) {
      analysisData.value = fromState;
      return;
    }

    // 2) sessionStorage
    const raw = sessionStorage.getItem("analysisData");
    if (raw && raw !== "undefined" && raw !== "null" && raw.trim() !== "") {
      try {
        analysisData.value = JSON.parse(raw);
        console.log(
          "[analysis] set from session, keys:",
          Object.keys(analysisData.value || {})
        );
        return;
      } catch (e) {
        sessionStorage.removeItem("analysisData");
      }
    }
  } catch (e) {
    console.error("[analysis] error:", e);
  }
};

onMounted(() => {
  analysisData.value =
    route?.state?.result ||
    JSON.parse(sessionStorage.getItem("analysisData") || "null");
});

const interestComparison = computed(() => {
  const d = analysisData.value || {};
  if (!d) return null;

  return {
    existingTotalInterest:
      d?.existingTotalInterest ??
      d?.baselineTotalInterest ??
      d?.interest?.baselineTotal ??
      d?.totalInterestBaseline ??
      0,

    newTotalInterest:
      d?.newTotalInterest ??
      d?.withNewTotalInterest ??
      d?.interest?.withNewTotal ??
      d?.totalInterestWithNew ??
      0,
  };
});

const hasInterestInfo = computed(() => {
  const ic = interestComparison.value || {};
  return Boolean(
    (ic.existingMonthlyInterests && ic.existingMonthlyInterests.length) ||
      (ic.newMonthlyInterests && ic.newMonthlyInterests.length) ||
      ic.existingTotalInterest ||
      ic.newTotalInterest
  );
});

const dataReady = computed(() => {
  const d = analysisData.value || {};
  return Boolean(
    d &&
      typeof d === "object" &&
      (d.totalComparison ||
        hasInterestInfo.value ||
        d.dsr !== undefined ||
        d.recommendedLoans)
  );
});
</script>

<template>
  <div :class="styles.analysisPage">
    <BackHeader title="시뮬레이션" />

    <AlertBox />
    <ImpactAnalysisHeader />

    <LoanChangeChart
      v-if="dataReady"
      :totalComparison="
        analysisData.totalComparison || { originalTotal: 0, newLoanTotal: 0 }
      "
    />

    <InterestChart
      v-if="dataReady && analysisData.interestComparison"
      :interestComparison="analysisData.interestComparison"
    />

    <DebtRatioChart v-if="dataReady" :dsr="Number(analysisData.dsr ?? 0)" />

    <Recommendations />

    <LoanRecommendations
      v-if="dataReady"
      :products="analysisData.recommendedLoans || []"
    />
  </div>
</template>
