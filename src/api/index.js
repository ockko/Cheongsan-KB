import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080', // 프록시를 사용하므로 빈 문자열로 설정
  timeout: 10000, // 타임아웃을 10초로 증가
  headers: {
    'Content-Type': 'application/json',
  },
});

export default instance;
// index.js는 axios통신을 위한 초기셋팅입니다. 이 파일은 백엔드 개발에 따라 수정이 될 수 있으므로 수정시 나중에 다시 공지하겠습니다.
