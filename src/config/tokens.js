// 토큰 설정 파일
// 실제 운영 환경에서는 환경 변수나 보안 저장소를 사용하세요

export const TOKENS = {
  // 실제 토큰으로 교체하세요
  ACCESS_TOKEN:
    'yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbnRlaHl1bjQ4ODAiLCJpYXQiOjE3NTQ0NjkzNjgsImV4cCI6MTc1NDQ3Mjk2OH0.NMQ10HNM6OQR8C1RlP5gHsRL1ZofRYcb5FBXEPQ56Pk',
  REFRESH_TOKEN:
    'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbnRlaHl1bjQ4ODAiLCJpYXQiOjE3NTQ0NjkzNjgsImV4cCI6MTc1NTY3ODk2OH0.W7f6oNipz6Stg28_3qjWUMykk-VGUYxun7fljc2Zr4E',
};

// 토큰 가져오기 함수
export const getAccessToken = () => {
  return TOKENS.ACCESS_TOKEN;
};

export const getRefreshToken = () => {
  return TOKENS.REFRESH_TOKEN;
};
