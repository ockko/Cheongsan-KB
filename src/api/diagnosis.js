// 진단 관련 API 서비스
import apiClient from './index.js';
import { getAccessToken } from '@/config/tokens.js';

// 토큰을 포함한 기본 설정 생성
const createAuthConfig = (customConfig = {}) => {
  const accessToken = getAccessToken();
  const config = {
    timeout: 10000,
    ...customConfig,
  };

  if (accessToken) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${accessToken}`,
    };
  }

  return config;
};
/**
 * 자가진단 결과를 백엔드로 전송
 * @param {Object} diagnosisData - 진단 데이터
 * @param {number} diagnosisData.q1 - 연체 현황 (1~4)
 * @param {number} diagnosisData.q2 - 채무 규모 (1~3)
 * @param {number} diagnosisData.q3 - 소득 여부 (1~2)
 * @returns {Promise<Object>} 진단 결과 ID와 추천 제도 정보
 */
export const submitDiagnosis = async (diagnosisData) => {
  try {
    const config = createAuthConfig();

    const requestBody = {
      sessionId: 'test-high-risk-001',
      answers: [
        {
          questionId: 1,
          optionId: diagnosisData.q1,
        },
        {
          questionId: 2,
          optionId: diagnosisData.q2,
        },
        {
          questionId: 3,
          optionId: diagnosisData.q3,
        },
      ],
    };

    const response = await apiClient.post(
      '/cheongsan/diagnosis/submit',
      requestBody,
      config
    );

    return response.data;
  } catch (error) {
    console.error('진단 결과 전송 실패:', error);
    throw new Error('진단 결과를 저장하는데 실패했습니다.');
  }
};

/**
 * 사용자의 진단 결과를 조회
 * @param {number} userId - 사용자 ID (선택적)
 * @returns {Promise<Object>} 진단 결과 및 추천 제도 정보
 */
export const getDiagnosisResult = async (userId = null) => {
  try {
    const config = createAuthConfig();
    const url = userId
      ? `/cheongsan/diagnosis/result/${userId}`
      : '/cheongsan/diagnosis/result';
    const response = await apiClient.get(url, config);

    return response.data;
  } catch (error) {
    console.error('진단 결과 조회 실패:', error);
    throw new Error('진단 결과를 불러오는데 실패했습니다.');
  }
};

/**
 * 추천 제도 상세 정보 조회
 * @param {number} recommendationId - 추천 제도 ID
 * @returns {Promise<Object>} 제도 상세 정보
 */
export const getRecommendationDetail = async (recommendationId) => {
  try {
    const config = createAuthConfig();

    const response = await apiClient.get(
      `/diagnosis/result/${recommendationId}`,
      config
    );

    return response.data;
  } catch (error) {
    console.error('추천 제도 상세 정보 조회 실패:', error);
    throw new Error('추천 제도 정보를 불러오는데 실패했습니다.');
  }
};

/**
 * 백엔드 추천 로직에 따른 제도 매핑
 * 백엔드에서 반환하는 ID 값에 따른 제도명 매핑
 */
export const RECOMMENDATION_MAPPING = {
  0: '예방적 상담',
  1: '개인파산',
  2: '개인회생',
  3: '개인워크아웃',
  4: '프리워크아웃',
  5: '신속채무조정',
};

/**
 * 추천 ID를 한글 제도명으로 변환
 * @param {number} recommendationId - 백엔드에서 반환한 추천 ID
 * @returns {string} 한글 제도명
 */
export const getRecommendationName = (recommendationId) => {
  return RECOMMENDATION_MAPPING[recommendationId] || '알 수 없는 제도';
};
