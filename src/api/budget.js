import { request } from '@/api/index';

// 1. 추천/최대/현재 한도 조회 API
export const getBudgetRecommendation = async () => {
  try {
    const response = await request.get(
      '/cheongsan/dashboard/budget/recommendation'
    );
    return response.data;
  } catch (error) {
    console.error('추천 한도 정보 조회 실패:', error);
    throw error;
  }
};

// 2. 최종 일일 소비 한도 저장 API
export const saveDailyLimit = async (dailyLimit) => {
  try {
    const response = await request.patch('/cheongsan/dashboard/budget', {
      dailyLimit: dailyLimit,
    });
    return response.data;
  } catch (error) {
    console.error('일일 한도 저장 실패:', error);
    throw error;
  }
};

// 3. 예산 설정 상태(수정 가능 여부) 조회 API
export const getBudgetStatus = async () => {
  try {
    const response = await request.get('/cheongsan/dashboard/budget/status');
    return response.data;
  } catch (error) {
    console.error('예산 설정 상태 조회 실패:', error);
    throw error;
  }
};
