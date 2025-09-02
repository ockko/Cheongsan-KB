// 진단 관련 API 서비스
import { request } from '@/api/index';

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

    const data = await request.post('/cheongsan/diagnosis/submit', requestBody);
    return data;
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
    const url = userId
      ? `/cheongsan/diagnosis/result/${userId}`
      : '/cheongsan/diagnosis/result';

    const data = await request.get(url);
    return data;
  } catch (error) {
    console.error('진단 결과 조회 실패:', error);
    throw new Error('진단 결과를 불러오는데 실패했습니다.');
  }
};

/**
 * 추천 제도 상세 정보 조회
 * @param {number} diagnosisId - 추천 제도 ID
 * @returns {Promise<Object>} 제도 상세 정보
 */
export const getRecommendationDetail = async (diagnosisId) => {
  try {
    const data = await request.get(
      `/cheongsan/diagnosis/result/${diagnosisId}`
    );
    return data;
  } catch (error) {
    console.error('추천 제도 상세 정보 조회 실패:', error);
    throw new Error('추천 제도 정보를 불러오는데 실패했습니다.');
  }
};
