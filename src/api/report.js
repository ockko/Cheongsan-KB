import { request } from "@/api/index";

// 가장 최근의 주간 리포트 데이터 조회
export const getLatestWeeklyReport = async () => {
  try {
    const response = await request.get(
      "/cheongsan/dashboard/reports/weekly/latest"
    );
    return response;
  } catch (error) {
    console.error("최신 주간 리포트 조회 실패:", error);
    throw error;
  }
};

// 특정 날짜가 포함된 주의 주간 리포트 데이터 조회
export const getWeeklyReportByDate = async (date) => {
  try {
    const response = await request.get(
      `/cheongsan/dashboard/reports/weekly?date=${date}`
    );
    return response;
  } catch (error) {
    console.error("특정 주차 리포트 조회 실패:", error);
    throw error;
  }
};

// 사용자가 선택할 수 있는 모든 과거 리포트의 기간 목록 조회
export const getWeeklyReportHistoryList = async () => {
  try {
    const response = await request.get(
      "/cheongsan/dashboard/reports/weekly/history-list"
    );
    return response;
  } catch (error) {
    console.error("과거 리포트 목록 조회 실패:", error);
    throw error;
  }
};
