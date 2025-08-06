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

// 맞춤 지원 정책 목록 조회
export const getCustomPolicies = async (token = null) => {
  try {
    const config = {};

    // 토큰이 제공된 경우 헤더에 추가
    if (token) {
      config.headers = {
        Authorization: `Bearer ${token}`,
      };
    }

    const response = await axios.get(
      'https://cheongsan.shop/cheongsan/policies/list',
      config
    );
    return response.data;
  } catch (error) {
    console.error('맞춤 지원 정책 목록 조회 실패:', error);

    // 개발용 Mock 데이터 반환
    console.log('Mock 데이터를 사용합니다.');
    return [
      {
        jurMnofNm: '국토교통부',
        summary:
          '도심 내 저소득층이 현 생활권에서 안정적으로 거주할 수 있도록 임대료가 저렴한 임대주택을 지원하여 주거안정을 도모합니다.',
        serviceId: 'WLF00003269',
        serviceName: '기존주택 전세임대주택 지원사업',
        supportCycle: '수시',
        tagList: ['주거', '다자녀', '장애인', '저소득', '한부모·조손'],
      },
      {
        jurMnofNm: '보건복지부',
        summary:
          '생계곤란 등의 위기상황에 처하여 도움이 필요한 경우 일시적으로 신속하게 지원함으로써 위기상황에서 벗어날 수 있도록 지원합니다.',
        serviceId: 'WLF00000917',
        serviceName: '긴급복지 주거지원',
        supportCycle: '월',
        tagList: ['주거', '저소득', '위기상황가구'],
      },
      {
        jurMnofNm: '고용노동부',
        summary: '청년들의 안정적인 일자리 취업을 지원하는 프로그램입니다.',
        serviceId: 'WLF00004567',
        serviceName: '청년 일자리 지원',
        supportCycle: '연',
        tagList: ['고용', '청년', '일자리'],
      },
    ];
  }
};
