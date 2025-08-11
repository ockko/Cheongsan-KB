import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
  fetchAnalyzeResult,
  fetchStrategyDetail,
  applyPlan,
} from '@/api/repayment-simulation';
import { useAuthStore } from './auth';

export const useSimulationStore = defineStore('simulation', () => {
  const authStore = useAuthStore();

  // userName은 authStore에서 닉네임을 가져옴
  const userName = computed(() => authStore.state.user.nickName || ''); // 닉네임 없으면 빈 문자열

  const repayments = ref([]);

  function setRepayments(list) {
    repayments.value = list;
  }

  const existingRepaymentAmount = ref(0);
  const additionalRepaymentAmount = ref(0);
  const totalRepaymentAmount = ref(0);

  const strategyMetaList = [
    {
      strategyType: 'TCS_RECOMMEND',
      name: '티모청 추천',
      summary: '사금융 우선 + 고금리 우선',
    },
    {
      strategyType: 'HIGH_INTEREST_FIRST',
      name: '고금리 우선',
      summary: '금리가 높은 부채부터 상환',
    },
    {
      strategyType: 'SMALL_AMOUNT_FIRST',
      name: '소액 우선',
      summary: '금액이 적은 부채부터 상환',
    },
    {
      strategyType: 'OLDEST_FIRST',
      name: '오래된 순 우선',
      summary: '가장 오래된 순 우선',
    },
  ];

  const strategy = ref({});
  const strategyList = ref([]);
  const isModalOpen = ref(false);
  const selectedStrategy = ref({});

  const formatNumber = (num) => {
    return Number(num).toLocaleString('ko-KR');
  };

  async function loadAnalyzeResult(route) {
    const result = await fetchAnalyzeResult(route);
    strategyList.value = result.strategyList;
    existingRepaymentAmount.value = result.existingRepaymentAmount;
    additionalRepaymentAmount.value = result.additionalRepaymentAmount;
    totalRepaymentAmount.value = result.totalRepaymentAmount;

    strategy.value = await fetchStrategyDetail('TCS_RECOMMEND');
  }

  async function openModal(strategyType) {
    const detail = await fetchStrategyDetail(strategyType);
    if (detail) {
      selectedStrategy.value = detail;
      isModalOpen.value = true;
    } else {
      alert('상세 정보를 불러오지 못했습니다.');
    }
  }

  const latestDebtFreeDate = computed(() => {
    if (!strategy.value?.debtFreeDates) return null;

    const latestDate = Object.values(strategy.value.debtFreeDates)
      .map((date) => new Date(date))
      .reduce((max, current) => (current > max ? current : max));

    return latestDate.toISOString().split('T')[0];
  });

  const strategyName = computed(() => {
    const found = strategyMetaList.find(
      (meta) => meta.strategyType === strategy.value?.strategyType
    );
    return found ? found.name : '';
  });

  async function applySelectedPlan(strategyType) {
    await applyPlan(strategyType);
  }

  return {
    repayments,
    setRepayments,

    userName,
    existingRepaymentAmount,
    additionalRepaymentAmount,
    totalRepaymentAmount,

    strategyMetaList,
    strategy,
    strategyList,
    isModalOpen,
    selectedStrategy,

    formatNumber,
    loadAnalyzeResult,
    openModal,

    latestDebtFreeDate,
    strategyName,

    applySelectedPlan,
  };
});
