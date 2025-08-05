import axios from './index';

// 정책 목록 조회
export const getPolicies = async (params = {}) => {
  try {
    const response = await axios.get('/api/policies', { params });
    return response.data;
  } catch (error) {
    console.error('정책 목록 조회 실패:', error);
    throw error;
  }
};

// 정책 상세 정보 조회
export const getPolicyDetail = async (policyId) => {
  try {
    const response = await axios.get(`/api/policies/${policyId}`);
    return response.data;
  } catch (error) {
    console.error('정책 상세 정보 조회 실패:', error);

    // 임시 테스트 데이터 반환 (개발용)
    const mockData = {
      policyNumber: '39488',
      ministryName: '보건복지부',
      departmentName: '기초생활보장과',
      policyName: '긴급복지 주거지원',
      policyTags: ['주거'],
      policySummary:
        '생계곤란 등의 위기상황에 처하여 도움이 필요한 경우 일시적으로 신속하게 지원함으로써 위기상황에서 벗어날 수 있도록 지원합니다.',
      supportAge: null,
      supportTarget: ['저소득', '위기상황가구', '주거지원필요자'],
      supportType: '현금지급,현물지급',
      supportCycle: '월',
      isOnlineApplyAvailable: 'N',
      contactNumber: '129',
      detailPageUrl:
        'https://www.bokjiro.go.kr/ssis-tbu/twataa/wlfareInfo/moveTWAT52011M.do?wlfareInfoId=WLF00000917&wlfareInfoReldBztpCd=01',
      policyId: policyId,
    };

    return mockData;
  }
};

// 정책 카테고리별 조회
export const getPoliciesByCategory = async (categoryId) => {
  try {
    const response = await axios.get(`/api/policies/category/${categoryId}`);
    return response.data;
  } catch (error) {
    console.error('카테고리별 정책 조회 실패:', error);
    throw error;
  }
};

// 정책 검색
export const searchPolicies = async (keyword) => {
  try {
    const response = await axios.get('/api/policies/search', {
      params: { keyword },
    });
    return response.data;
  } catch (error) {
    console.error('정책 검색 실패:', error);
    throw error;
  }
};
