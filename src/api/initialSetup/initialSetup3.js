import { request } from '../index';

export async function fetchUserLoans() {
  try {
    return await request.get('/cheongsan/initialSetup/loans');
  } catch (error) {
    console.error('get API 호출 실패:', error);
    throw error;
  }
}

export async function updateLoanRepaymentInfo(
  debtId,
  { gracePeriodMonths, repaymentMethod, repaymentDay }
) {
  try {
    const today = new Date();
    today.setMonth(today.getMonth() + 1);
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');

    const day = String(repaymentDay).padStart(2, '0');
    const nextPaymentDate = `${yyyy}-${mm}-${day}`;

    return await request.patch(`/cheongsan/initialSetup/loans/repaymentInfo`, {
      debtId,
      gracePeriodMonth: gracePeriodMonths,
      repaymentMethod: repaymentMethod,
      nextPaymentDate: nextPaymentDate,
    });
  } catch (error) {
    console.error('상환정보 저장 API 호출 실패:', error);
    throw error;
  }
}
