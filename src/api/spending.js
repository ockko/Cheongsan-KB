import { request } from '@/api/index';

// 오늘의 지출 현황 데이터를 조회하는 API
export const getDailySpending = async () => {
  try {
    const response = await request.get('/cheongsan/dashboard/daily-spending');
    return response;
  } catch (error) {
    console.error('오늘의 지출 현황 조회 실패:', error);
    throw error;
  }
};
