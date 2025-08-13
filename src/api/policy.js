import { request } from '@/api/index';

// 정책 목록 조회
export const getPolicies = async (params = {}) => {
  try {
    const data = await request.get('/api/policies', { params });
    return data;
  } catch (error) {
    console.error('정책 목록 조회 실패:', error);
    throw error;
  }
};

// 정책 상세 정보 조회 (policyName으로 조회)
export const getPolicyDetail = async (policyName) => {
  try {
    // params 옵션을 사용하여 자동 인코딩 처리
    const params = { policyName };
    const data = await request.get('/cheongsan/policies/detail', { params });

    return data;
  } catch (error) {
    console.error('정책 상세 정보 조회 실패:', error);

    // 에러 타입별 상세 로깅
    if (error.response) {
      console.error('서버 응답 에러:', {
        status: error.response.status,
        statusText: error.response.statusText,
        data: error.response.data,
      });
    } else if (error.request) {
      console.error('네트워크 에러:', error.request);
    } else {
      console.error('요청 설정 에러:', error.message);
    }

    // 개발용 Mock 데이터 반환
    console.log('Mock 데이터를 사용합니다.');
    return {
      policyNumber: '40785',
      ministryName: '보건복지부',
      departmentName: '기초생활보장과',
      policyName: policyName,
      policyTags: ['주거'],
      policySummary:
        '생계곤란 등의 위기상황에 처하여 도움이 필요한 경우 일시적으로 신속하게 지원함으로써 위기상황에서 벗어날 수 있도록 지원합니다.',
      supportAge: null,
      supportTarget: ['저소득'],
      supportType: '현금지급,현물지급',
      supportCycle: '월',
      isOnlineApplyAvailable: 'N',
      contactNumber: '129',
      detailPageUrl:
        'https://www.bokjiro.go.kr/ssis-tbu/twataa/wlfareInfo/moveTWAT52011M.do?wlfareInfoId=WLF00000917&wlfareInfoReldBztpCd=01',
      policyId: 'WLF00000917',
    };
  }
};

// 정책 카테고리별 조회
export const getPoliciesByCategory = async (categoryId) => {
  try {
    const data = await request.get(`/api/policies/category/${categoryId}`);
    return data;
  } catch (error) {
    console.error('카테고리별 정책 조회 실패:', error);
    throw error;
  }
};

// 정책 검색
export const searchPolicies = async (keyword) => {
  try {
    const data = await request.get('/api/policies/search', {
      params: { keyword },
    });
    return data;
  } catch (error) {
    console.error('정책 검색 실패:', error);
    throw error;
  }
};

// 맞춤 정책 검색 (백엔드 API)
export const searchCustomPolicies = async (searchWrd) => {
  try {
    // params 옵션을 사용하여 자동 인코딩 처리
    const params = { searchWrd };

    const data = await request.get('/cheongsan/policies/search', { params });

    return data;
  } catch (error) {
    console.error('맞춤 정책 검색 실패:', error);

    // 에러 타입별 상세 로깅
    if (error.response) {
      console.error('서버 응답 에러:', {
        status: error.response.status,
        statusText: error.response.statusText,
        data: error.response.data,
      });
    } else if (error.request) {
      console.error('네트워크 에러:', error.request);
    } else {
      console.error('요청 설정 에러:', error.message);
    }

    // 검색 실패 시 빈 배열 반환
    return [];
  }
};

// 맞춤 지원 정책 목록 조회
export const getCustomPolicies = async () => {
  try {
    console.log('API 요청 시작:', '/cheongsan/policies/list');

    const data = await request.get('/cheongsan/policies/list');
    return data;
  } catch (error) {
    console.error('맞춤 지원 정책 목록 조회 실패:', error);

    // 에러 타입별 상세 로깅
    if (error.response) {
      // 서버가 응답을 반환한 경우
      console.error('서버 응답 에러:', {
        status: error.response.status,
        statusText: error.response.statusText,
        data: error.response.data,
      });
    } else if (error.request) {
      // 요청이 전송되었지만 응답을 받지 못한 경우
      console.error('네트워크 에러:', error.request);
    } else {
      // 요청 설정 중 에러가 발생한 경우
      console.error('요청 설정 에러:', error.message);
    }
  }
};
