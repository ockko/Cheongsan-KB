import { request } from '@/api/index';


// 정책 상세 정보 조회 (policyName으로 조회)
export const getPolicyDetail = async (policyName) => {
  try {
    // params 옵션을 사용하여 자동 인코딩 처리
    const params = { policyName };

    console.log('정책 상세 API 요청 시작:', '/cheongsan/policies/detail');
    console.log('정책 상세 파라미터:', params);

    const data = await request.get('/cheongsan/policies/detail', { params });

    console.log('정책 상세 API 응답 성공:', data);
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


  }
};


// 맞춤 정책 검색 (백엔드 API)
export const searchCustomPolicies = async (searchWrd) => {
  try {
    // params 옵션을 사용하여 자동 인코딩 처리
    const params = { searchWrd };

    console.log('검색 API 요청 시작:', '/cheongsan/policies/search');
    console.log('검색 파라미터:', params);

    const data = await request.get('/cheongsan/policies/search', { params });

    console.log('검색 API 응답 성공:', data);
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

    console.log('API 응답 성공:', data);
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
