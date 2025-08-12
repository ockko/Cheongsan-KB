import { request } from "@/api/index";

export const analyzeLoan = async (payload) => {
  try {
    // request.post가 이미 data만 반환하므로 바로 result에 담김
    const result = await request.post("/cheongsan/simulation/loan", payload);
    return result;
  } catch (error) {
    console.error("대출 분석 요청 실패:", error);
    throw error;
  }
};

export const loanApi = {
  analyzeLoan,
};
