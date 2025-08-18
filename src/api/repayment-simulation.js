import { request } from '@/api/index';
import { useSimulationStore } from '@/stores/repayment-simulation';
import { useUiStore } from '@/stores/ui';

export const analyze = async (rawValue, router) => {
  const uiStore = useUiStore();

  const amount = rawValue.value === '' ? 0 : Number(rawValue.value);

  try {
    await request.post(`/cheongsan/simulation/repayments/analyze`, null, {
      params: { monthlyAvailableAmount: amount },
    });

    router.push({
      path: '/repayment-simulation/result',
      query: { monthlyAvailableAmount: amount },
    });
  } catch (error) {
    uiStore.openModal({
      title: '오류',
      message: '상세 정보를 불러오지 못했습니다.',
      isError: true,
    });
  }
};

export const fetchAnalyzeResult = async (route) => {
  const monthlyAvailableAmount = route.query.monthlyAvailableAmount;
  const store = useSimulationStore();
  const res = await request.get('/cheongsan/simulation/repayments/result', {
    params: { monthlyAvailableAmount },
  });

  store.setRepayments(res.repayments || []);

  return {
    existingRepaymentAmount: res.existingRepaymentAmount,
    additionalRepaymentAmount: res.additionalRepaymentAmount,
    totalRepaymentAmount: res.totalRepaymentAmount,
  };
};

export const fetchStrategyDetail = async (strategyType) => {
  try {
    const res = await request.get('/cheongsan/simulation/repayments/detail', {
      params: { strategyType },
    });
    return res; // RepaymentResponseDTO 객체
  } catch (error) {
    return null;
  }
};

export const applyPlan = async (strategyType) => {
  const uiStore = useUiStore();
  try {
    await request.put('/cheongsan/simulation/repayments/apply', null, {
      params: { strategyName: strategyType },
    });

    uiStore.openModal({
      title: '전략 적용 완료',
      message: '전략이 적용되었습니다.',
      isError: false,
    });

    close();
  } catch (error) {
    uiStore.openModal({
      title: '오류 발생',
      message: '전략 적용 중 오류가 발생했습니다.',
      isError: true,
    });
  }
};
