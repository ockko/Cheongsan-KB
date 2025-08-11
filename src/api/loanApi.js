import axios from "axios";
import { useAuthStore } from "@/stores/auth";

// 토큰을 포함한 기본 설정 생성
const createAuthConfig = (customConfig = {}) => {
  const authStore = useAuthStore();
  const token = authStore.getToken();

  const config = {
    timeout: 10000,
    ...customConfig,
  };

  if (token) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${token}`,
    };
  }

  return config;
};

// 대출 시뮬레이션 분석
export const analyzeLoan = async (data) => {
  try {
    const config = createAuthConfig();

    console.log(
      "대출 분석 API 요청 시작:",
      "http://localhost:8080/cheongsan/simulation/loan"
    );
    console.log("대출 분석 요청 데이터:", data);
    console.log("대출 분석 요청 설정:", config);

    const response = await axios.post(
      "http://localhost:8080/cheongsan/simulation/loan",
      data,
      config
    );

    console.log("대출 분석 API 응답 성공:", response.data);
    return response.data;
  } catch (error) {
    console.error("대출 시뮬레이션 분석 실패:", error);

    // 에러 타입별 상세 로깅
    if (error.response) {
      console.error("서버 응답 에러:", {
        status: error.response.status,
        statusText: error.response.statusText,
        data: error.response.data,
      });
    } else if (error.request) {
      console.error("네트워크 에러:", error.request);
    } else {
      console.error("요청 설정 에러:", error.message);
    }

    throw error;
  }
};

export const loanApi = {
  analyzeLoan,
};
