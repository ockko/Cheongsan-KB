<script setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSimulationStore } from '@/stores/repayment-simulation';
import DebtChart from '@/components/domain/simulation/DebtChart.vue';
import PlanModal from '@/components/domain/simulation/PlanModal.vue';
import styles from '@/assets/styles/pages/simulation/RepaymentSimulationResult.module.css';

const store = useSimulationStore();
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  await store.loadAnalyzeResult(route);
});

const goBack = () => {
  router.back();
};
</script>
<template>
  <div :class="styles.simulator">
    <header :class="styles.simulatorHeader">
      <div :class="styles.backButton" @click="goBack">
        <i class="fa fa-arrow-left"></i>
      </div>
      <p :class="styles.textRegular">상환 시뮬레이션</p>
    </header>

    <p class="text-light" :class="styles.info">
      ⓘ {{ store.userName }}님의 월 상환액을 기준으로 상환 플랜을 제시합니다.
    </p>
    <div :class="styles.repaymentCard">
      <div :class="styles.amountBox">
        <div :class="styles.amountItem">
          <div :class="styles.resLabel">기존 상환액</div>
          <div :class="[styles.value, styles.blue]">
            {{ store.formatNumber(store.existingRepaymentAmount) }}
          </div>
        </div>
        <div :class="styles.symbol">+</div>
        <div :class="styles.amountItem">
          <div :class="styles.resLabel">추가 상환액</div>
          <div :class="[styles.value, styles.blue]">
            {{ store.formatNumber(store.additionalRepaymentAmount) }}
          </div>
        </div>
      </div>

      <div :class="styles.totalBox">
        <div :class="styles.equal">=</div>
        <div :class="styles.amountItem">
          <div :class="styles.resLabel">총 월 상환액</div>
          <div :class="[styles.value, styles.red]">
            {{ store.formatNumber(store.totalRepaymentAmount) }}
          </div>
        </div>
      </div>
    </div>

    <div :class="styles.recommendedResults">분석 결과</div>
    <div :class="styles.recommendedPlan">
      <h3 :class="styles.strategyTitle">{{ store.strategyName }}</h3>
      <p>최종 빚 졸업일: {{ store.latestDebtFreeDate }}</p>
      <p>총 상환 기간: {{ store.strategy.totalMonths }}개월</p>
      <p>절약 이자: {{ store.formatNumber(store.strategy.interestSaved) }}원</p>
      <button
        :class="styles.selectButton"
        @click="store.applySelectedPlan(store.strategy.strategyType)"
      >
        이 전략 선택하기
      </button>
    </div>

    <hr class="divider" />

    <section :class="styles.strategyComparison">
      <h3 :class="styles.title">전략별 상세 비교</h3>
      <ul :class="styles.strategyCardList">
        <li
          v-for="strategy in store.strategyMetaList"
          :key="strategy.strategyType"
          :class="styles.strategyCard"
        >
          <div :class="styles.strategyText">
            <strong>{{ strategy.name }}</strong
            ><br />
            {{ strategy.summary }}
          </div>
          <button
            @click="() => store.openModal(strategy.strategyType)"
            :class="styles.selectButtonMini"
          >
            상세
          </button>
        </li>
      </ul>

      <PlanModal
        :isOpen="store.isModalOpen"
        :strategy="store.selectedStrategy"
        @close="store.isModalOpen = false"
      />
    </section>

    <hr :class="styles.divider1" />
    <DebtChart :repaymentData="store.strategyList" />
  </div>
</template>
