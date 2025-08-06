import axios from 'axios';

const instance = axios.create({
  timeout: 1000,
});

export default instance;
// index.js는 axios통신을 위한 초기셋팅입니다. 이 파일은 백엔드 개발에 따라 수정이 될 수 있으므로 수정시 나중에 다시 공지하겠습니다.
