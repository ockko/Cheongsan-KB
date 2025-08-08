import { request } from '@/api/index';

export const calendarApi = {
  // 1. 월별 거래내역 조회
  getMonthlyTransactions: async (year, month) => {
    try {
      const data = await request.get('/cheongsan/calendar/transactions', {
        params: { year, month },
      });
      console.log('월별 거래내역 조회:', data);
      return data;
    } catch (error) {
      console.error('월별 거래내역 조회 실패:', error);
      throw error;
    }
  },

  // 2. 월별 대출 상환일자 조회
  getMonthlyRepayments: async (year, month) => {
    try {
      const data = await request.get('/cheongsan/calendar/repayments', {
        params: { year, month },
      });
      console.log('월별 대출 상환일자 조회:', data);
      return data;
    } catch (error) {
      console.error('월별 대출 상환일자 조회 실패:', error);
      throw error;
    }
  },

  // 3. 일별 대출 상환일자 조회
  getDailyRepayments: async (date) => {
    try {
      const data = await request.get(`/cheongsan/calendar/repayments/${date}`);
      console.log('일별 대출 상환일자 조회:', data);
      return data;
    } catch (error) {
      console.error('일별 대출 상환일자 조회 실패:', error);
      throw error;
    }
  },

  // 4. 일별 거래 내역 조회
  getDailyTransactions: async (date) => {
    try {
      const data = await request.get(
        `/cheongsan/calendar/transactions/${date}`
      );
      console.log('일별 거래 내역 조회:', data);
      return data;
    } catch (error) {
      console.error('일별 거래 내역 조회 실패:', error);
      throw error;
    }
  },
};
