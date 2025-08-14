import { request } from '../index';

export const fetchUserLoans = async () => {
  try {
    return await request.get('/cheongsan/initialSetup/loans');
  } catch (error) {
    console.error('get API 호출 실패:', error);
    throw error;
  }
};

export const updateLoanRepaymentInfo = async (
  debtId,
  { gracePeriodMonths, repaymentMethod, repaymentDay }
) => {
  try {
    // 상환일만 입력받은 뒤, 년/월은 저장일 기준 다음 달로 계산하여 yyyy-mm-dd형식으로 저장.
    const today = new Date();
    today.setMonth(today.getMonth() + 1);
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(repaymentDay).padStart(2, '0');
    const nextPaymentDate = `${yyyy}-${mm}-${day}`;

    // 백엔드 컨트롤러에 맞춰 엔드포인트와 데이터 형식 수정
    return await request.patch(`/cheongsan/initialSetup/loans/${debtId}`, {
      gracePeriodMonths: gracePeriodMonths,
      repaymentMethod: repaymentMethod,
      nextPaymentDate: nextPaymentDate,
    });
  } catch (error) {
    console.error('상환정보 저장 API 호출 실패:', error);
    throw error;
  }
};
