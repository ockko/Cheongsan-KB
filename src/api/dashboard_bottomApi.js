import { request } from './index.js';

/**
 * 대출 상환율 조회 API
 * @returns {Promise<Object>} 대출 상환율 정보
 */
export const getRepaymentRatio = async () => {
  try {
    const data = await request.get('/cheongsan/dashboard/loans/repaymentRatio');
    console.log('대출 상환율 조회:', data);
    return data;
  } catch (error) {
    console.error('대출 상환율 조회 실패:', error);
    throw error;
  }
};

/**
 * 대출 상환율 데이터를 TotalDebtRepaymentWidget 형식으로 변환
 * @param {Object} repaymentRatioData - API에서 받은 상환율 데이터
 * @returns {Object} TotalDebtRepaymentWidget에서 사용할 형식
 */
export const transformRepaymentRatioData = (repaymentRatioData) => {
  // 백엔드 DTO 구조에 맞게 변환
  const totalOriginalAmount = repaymentRatioData.totalOriginalAmount || 0;
  const totalRepaidAmount = repaymentRatioData.totalRepaidAmount || 0;
  const repaymentRatio = repaymentRatioData.repaymentRatio || 0;

  // 남은 부채 계산
  const remainingDebt = totalOriginalAmount - totalRepaidAmount;

  return {
    totalDebt: totalOriginalAmount,
    totalRepaid: totalRepaidAmount,
    repaymentRatio: repaymentRatio,
    remainingDebt: remainingDebt,
  };
};

/**
 * TotalDebtRepaymentWidget용 통합 데이터 조회 함수
 * @returns {Promise<Object>} TotalDebtRepaymentWidget에서 사용할 데이터
 */
export const getTotalDebtRepaymentData = async () => {
  try {
    const repaymentRatioData = await getRepaymentRatio();
    return transformRepaymentRatioData(repaymentRatioData);
  } catch (error) {
    console.error('TotalDebtRepayment 데이터 조회 실패:', error);
    // 에러 시 기본값 반환
    return {
      totalDebt: 0,
      totalRepaid: 0,
      repaymentRatio: 0,
      remainingDebt: 0,
    };
  }
};

/**
 * 사용자 대출 목록 조회 API
 * @param {string} sort - 정렬 방식 (기본값: 'createdAtDesc')
 * @returns {Promise<Array>} 대출 목록
 */
export const getUserDebtList = async (sort = 'createdAtDesc') => {
  try {
    const data = await request.get('/cheongsan/dashboard/loans', {
      params: { sort },
    });
    console.log('대출 목록 조회:', data);
    return data;
  } catch (error) {
    console.error('대출 목록 조회 실패:', error);
    throw error;
  }
};

/**
 * 대출 목록 데이터를 DebtListWidget 형식으로 변환
 * @param {Array} debtListData - API에서 받은 대출 목록 데이터
 * @returns {Array} DebtListWidget에서 사용할 형식
 */
export const transformDebtListData = (debtListData) => {
  if (!Array.isArray(debtListData)) return [];

  return debtListData.map((debt) => ({
    debtId: debt.debtId || 0,
    debtName: debt.debtName || '대출명 없음',
    organizationName: debt.organizationName || '기관명 없음',
    originalAmount: debt.originalAmount || 0,
    currentBalance: debt.currentBalance || 0,
    repaymentRate: debt.repaymentRate || 0,
    interestRate: debt.interestRate || 0,
    repaymentType: debt.repaymentType || 'UNKNOWN',
    gracePeriodMonths: debt.gracePeriodMonths || 0,
    loanStartDate: debt.loanStartDate || null,
    loanEndDate: debt.loanEndDate || null,
    nextPaymentDate: debt.nextPaymentDate || null,
  }));
};

/**
 * DebtListWidget용 통합 데이터 조회 함수
 * @param {string} sort - 정렬 방식
 * @returns {Promise<Array>} DebtListWidget에서 사용할 데이터
 */
export const getDebtListData = async (sort = 'createdAtDesc') => {
  try {
    const debtListData = await getUserDebtList(sort);
    return transformDebtListData(debtListData);
  } catch (error) {
    console.error('DebtList 데이터 조회 실패:', error);
    // 에러 시 빈 배열 반환
    return [];
  }
};
