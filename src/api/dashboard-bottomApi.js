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

/**
 * 상환 계획 요약 조회 API
 * @returns {Promise<Object>} 상환 계획 요약 정보
 */
export const getRepaymentSummary = async () => {
  try {
    const data = await request.get('/cheongsan/dashboard/repaymentSummary');
    console.log('상환 계획 요약 조회:', data);
    return data;
  } catch (error) {
    console.error('상환 계획 요약 조회 실패:', error);
    throw error;
  }
};

/**
 * 상환 계획 데이터를 RepaymentPlanWidget 형식으로 변환
 * @param {Object} repaymentSummaryData - API에서 받은 상환 계획 데이터
 * @returns {Object} RepaymentPlanWidget에서 사용할 형식
 */
export const transformRepaymentSummaryData = (repaymentSummaryData) => {
  // 백엔드 DTO 구조에 맞게 변환
  const strategyType = repaymentSummaryData.strategyType || 'UNKNOWN';
  const totalMonths = repaymentSummaryData.totalMonths || 0;
  const interestSaved = repaymentSummaryData.interestSaved || 0;
  const totalPayment = repaymentSummaryData.totalPayment || 0;
  const originalPayment = repaymentSummaryData.originalPayment || 0;
  const totalPrepaymentFee = repaymentSummaryData.totalPrepaymentFee || 0;
  const sortedLoanNames = repaymentSummaryData.sortedLoanNames || [];
  const repaymentHistory = repaymentSummaryData.repaymentHistory || {};
  const debtFreeDates = repaymentSummaryData.debtFreeDates || {};

  // 월 상환액 계산 (총 상환액을 총 개월로 나누기)
  const monthlyPayment = totalMonths > 0 ? totalPayment / totalMonths : 0;

  // 최종 빚졸업일 찾기 (가장 늦은 날짜)
  const finalDebtFreeDate =
    Object.values(debtFreeDates).length > 0
      ? Object.values(debtFreeDates).reduce((latest, date) => {
          return new Date(date) > new Date(latest) ? date : latest;
        })
      : null;

  return {
    strategyType,
    totalMonths,
    interestSaved,
    totalPayment,
    originalPayment,
    totalPrepaymentFee,
    sortedLoanNames,
    repaymentHistory,
    debtFreeDates,
    monthlyPayment,
    finalDebtFreeDate,
  };
};

/**
 * RepaymentPlanWidget용 통합 데이터 조회 함수
 * @returns {Promise<Object>} RepaymentPlanWidget에서 사용할 데이터
 */
export const getRepaymentPlanData = async () => {
  try {
    const repaymentSummaryData = await getRepaymentSummary();
    return transformRepaymentSummaryData(repaymentSummaryData);
  } catch (error) {
    console.error('RepaymentPlan 데이터 조회 실패:', error);
    // 에러 시 null 값 반환하여 위젯에서 "데이터 없음" 상태로 처리
    return {
      strategyType: null,
      totalMonths: null,
      interestSaved: null,
      totalPayment: null,
      originalPayment: null,
      totalPrepaymentFee: null,
      sortedLoanNames: [],
      repaymentHistory: {},
      debtFreeDates: {},
      monthlyPayment: null,
      finalDebtFreeDate: null,
    };
  }
};

/**
 * 대출 상품 등록 API
 * @param {Object} loanData - 대출 상품 데이터
 * @returns {Promise<Object>} 등록 결과
 */
export const registerDebt = async (loanData) => {
  try {
    // 입력 데이터 검증 및 변환
    const originalAmount = Number(loanData.originalAmount);
    const interestRate = Number(loanData.interestRate);
    const totalMonths = Number(loanData.totalRepaymentPeriod);
    const currentBalance = Number(loanData.remainingAmount);

    // Long 타입 필드 처리 - 빈 값이거나 0인 경우 null로 설정
    const gracePeriodMonths =
      loanData.gracePeriod && Number(loanData.gracePeriod) > 0
        ? Number(loanData.gracePeriod)
        : null;
    const nextPaymentDay =
      loanData.nextPaymentDay && Number(loanData.nextPaymentDay) > 0
        ? Number(loanData.nextPaymentDay)
        : null;

    // 숫자 변환 검증
    if (
      isNaN(originalAmount) ||
      isNaN(interestRate) ||
      isNaN(totalMonths) ||
      isNaN(currentBalance)
    ) {
      throw new Error('숫자 필드에 유효하지 않은 값이 포함되어 있습니다.');
    }

    // Long 타입 필드 검증 (null 허용)
    if (gracePeriodMonths !== null && isNaN(gracePeriodMonths)) {
      throw new Error('거치 기간에 유효하지 않은 값이 포함되어 있습니다.');
    }

    if (nextPaymentDay !== null && isNaN(nextPaymentDay)) {
      throw new Error('상환일에 유효하지 않은 값이 포함되어 있습니다.');
    }

    // 날짜 형식 검증 및 변환
    const year = loanData.loanYear.toString().trim();
    const month = loanData.loanMonth.toString().trim().padStart(2, '0');
    const day = loanData.loanDay.toString().trim().padStart(2, '0');

    if (!year || !month || !day) {
      throw new Error('대출 시작일을 올바르게 입력해주세요.');
    }

    // 날짜 유효성 검증
    const monthNum = parseInt(month);
    const dayNum = parseInt(day);

    if (monthNum < 1 || monthNum > 12) {
      throw new Error('월은 1-12 사이의 값이어야 합니다.');
    }

    if (dayNum < 1 || dayNum > 31) {
      throw new Error('일은 1-31 사이의 값이어야 합니다.');
    }

    const loanStartDate = `${year}-${month}-${day}`;
    console.log('생성된 날짜:', loanStartDate);

    // LoanAddModal에서 받은 데이터를 백엔드 DTO 형식으로 변환
    const debtRegisterData = {
      debtName: loanData.name.trim(),
      organizationName: loanData.institution.trim(),
      resAccount: loanData.resAccount.trim(),
      originalAmount: Number(originalAmount),
      interestRate: Number(interestRate),
      loanStartDate: loanStartDate,
      totalMonths: Number(totalMonths),
      currentBalance: Number(currentBalance),
      gracePeriodMonths: gracePeriodMonths, // null 허용
      repaymentMethod: loanData.repaymentMethod.trim(),
      nextPaymentDay: nextPaymentDay, // null 허용
    };

    console.log('전송할 데이터:', debtRegisterData);

    const response = await request.post(
      '/cheongsan/dashboard/loans',
      debtRegisterData,
      {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          Accept: 'text/plain, application/json',
        },
      }
    );
    console.log('대출 상품 등록 성공:', response);
    return {
      success: true,
      message: '대출 상품이 성공적으로 등록되었습니다.',
      data: response,
    };
  } catch (error) {
    console.error('대출 상품 등록 실패:', error);
    console.error('에러 응답:', error.response?.data);

    // 백엔드에서 정의한 에러 메시지에 따라 처리
    if (error.response?.status === 400) {
      // 400 에러의 경우 백엔드에서 보낸 구체적인 에러 메시지 사용
      const errorMessage =
        error.response?.data?.message ||
        error.response?.data ||
        '잘못된 요청입니다.';
      throw new Error(errorMessage);
    } else if (error.response?.status === 409) {
      throw new Error('이미 등록된 대출 계좌입니다.');
    } else if (error.response?.status === 500) {
      throw new Error('서버 오류가 발생했습니다.');
    } else {
      throw new Error('대출 상품 등록에 실패했습니다.');
    }
  }
};
