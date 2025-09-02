// API 호출 URL 설정 파일
export const API_CONFIG = {
  // BASE_URL: 'https://cheongsan.shop',
  BASE_URL: 'http://localhost:8080', // 로컬 개발시
};

// API BASE URL 가져오기 함수
export const getApiBaseUrl = () => {
  return API_CONFIG.BASE_URL;
};
