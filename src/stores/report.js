import { defineStore } from 'pinia';
import {
  getLatestWeeklyReport,
  getWeeklyReportByDate,
  getWeeklyReportHistoryList,
} from '@/api/report';
import { ref } from 'vue';

export const useReportStore = defineStore('report', () => {
  // --- State ---
  const currentReport = ref(null); // 현재 화면에 표시될 리포트 데이터
  const reportHistory = ref([]); // 드롭다운에 표시될 과거 리포트 목록
  const isLoading = ref(false);

  // --- Actions ---

  // 가장 최신 리포트를 불러오는 액션
  const fetchLatestReport = async () => {
    isLoading.value = true;
    try {
      currentReport.value = await getLatestWeeklyReport();
    } catch (error) {
      console.error('스토어에서 최신 리포트 로딩 실패:', error);
    } finally {
      isLoading.value = false;
    }
  };

  // 특정 주의 리포트를 불러오는 액션
  const fetchReportByDate = async (date) => {
    isLoading.value = true;
    try {
      currentReport.value = await getWeeklyReportByDate(date);
    } catch (error) {
      console.error('스토어에서 특정 주차 리포트 로딩 실패:', error);
    } finally {
      isLoading.value = false;
    }
  };

  // 과거 리포트 목록을 불러오는 액션
  const fetchReportHistoryList = async () => {
    try {
      reportHistory.value = await getWeeklyReportHistoryList();
    } catch (error) {
      console.error('스토어에서 과거 리포트 목록 로딩 실패:', error);
    }
  };

  return {
    currentReport,
    reportHistory,
    isLoading,
    fetchLatestReport,
    fetchReportByDate,
    fetchReportHistoryList,
  };
});
