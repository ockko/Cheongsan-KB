import axios from 'axios';

// 하드코딩된 토큰 (로그인 기능 구현 전까지)
const HARDCODED_TOKEN = '';

// Calendar API 전용 axios 인스턴스
const calendarAPI = axios.create({
  baseURL: 'https://cheongsan.shop/cheongsan',
  //   baseURL: 'http://localhost:8080/cheongsan',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${HARDCODED_TOKEN}`,
  },
});

// 요청 인터셉터
calendarAPI.interceptors.request.use(
  (config) => {
    console.log('Calendar API 요청:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('Calendar API 요청 오류:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터
calendarAPI.interceptors.response.use(
  (response) => {
    console.log('Calendar API 응답:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error(
      'Calendar API 응답 오류:',
      error.response?.status,
      error.message
    );
    return Promise.reject(error);
  }
);

export const calendarApi = {
  // 1. 월별 거래내역 조회
  getMonthlyTransactions: async (year, month) => {
    try {
      const response = await calendarAPI.get('/calendar/transactions', {
        params: { year, month },
      });
      console.log('월별 거래내역 조회: ', response);
      return response.data;
    } catch (error) {
      console.error('월별 거래내역 조회 실패:', error);
      throw error;
    }
  },

  // 2. 월별 대출 상환일자 조회
  getMonthlyRepayments: async (year, month) => {
    try {
      const response = await calendarAPI.get('/calendar/repayments', {
        params: { year, month },
      });
      console.log('월별 대출 상환일자 조회: ', response);
      return response.data;
    } catch (error) {
      console.error('월별 대출 상환일자 조회 실패:', error);
      throw error;
    }
  },

  // 3. 일별 대출 상환일자 조회
  getDailyRepayments: async (date) => {
    try {
      const response = await calendarAPI.get(`/calendar/repayments/${date}`);
      console.log('일별 대출 상환일자 조회: ', response);
      return response.data;
    } catch (error) {
      console.error('일별 대출 상환일자 조회 실패:', error);
      throw error;
    }
  },

  // 4. 일별 거래 내역 조회
  getDailyTransactions: async (date) => {
    try {
      const response = await calendarAPI.get(`/calendar/transactions/${date}`);
      console.log('일별 거래 내역 조회 ', response);
      return response.data;
    } catch (error) {
      console.error('일별 거래 내역 조회 실패:', error);
      throw error;
    }
  },
};
