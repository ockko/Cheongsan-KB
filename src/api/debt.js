import { request } from "@/api/index";

// 연체 중인 대출 목록 데이터를 조회하는 API
export const getOverdueLoans = async () => {
  try {
    const response = await request.get("/cheongsan/dashboard/delinquentLoans");
    return response;
  } catch (error) {
    console.error("연체 대출 조회 실패:", error);
    throw error;
  }
};
