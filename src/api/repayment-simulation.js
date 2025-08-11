import { request } from '@/api/index';
import { useSimulationStore } from '@/stores/repayment-simulation';
export const analyze = async (rawValue, router) => {
  alert(
    rawValue.value === ''
      ? '추가 상환 가능액: 없음'
      : `추가 상환 가능액: ${Number(rawValue.value).toLocaleString()}원`
  );

  const amount = rawValue.value === '' ? 0 : Number(rawValue.value);

  await request.post(`/cheongsan/simulation/repayments/analyze`, null, {
    params: { monthlyAvailableAmount: amount },
  });

  router.push({
    path: '/repayment-simulation/result',
    query: {
      monthlyAvailableAmount: amount,
    },
  });
};

export const fetchAnalyzeResult = async (route) => {
  const monthlyAvailableAmount = route.query.monthlyAvailableAmount;
  const store = useSimulationStore();
  const res = await request.get('/cheongsan/simulation/repayments/result', {
    params: { monthlyAvailableAmount },
  });

  console.log('result API 응답:', res);
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
    console.log(res);
    return res; // RepaymentResponseDTO 객체
  } catch (error) {
    console.error('전략 상세 정보 조회 실패', error);
    return null;
  }
};

export const applyPlan = async (strategyType) => {
  try {
    await request.put('/cheongsan/simulation/repayments/apply', null, {
      params: { strategyName: strategyType },
    });
    alert('전략이 적용되었습니다.');
    close();
  } catch (error) {
    console.error(error);
    alert('전략 적용 중 오류가 발생했습니다.');
  }
};
