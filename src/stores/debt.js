import { defineStore } from 'pinia';
import { getOverdueLoans } from '@/api/debt';
import { ref } from 'vue';

export const useDebtStore = defineStore('debt', () => {
  // --- State ---
  const overdueLoans = ref([]);
  const isLoading = ref(false);

  // --- Actions ---

  // 연체 대출 목록 데이터를 불러오는 액션
  async function fetchOverdueLoans() {
    isLoading.value = true;
    try {
      const data = await getOverdueLoans();
      overdueLoans.value = data;
    } catch (error) {
      console.error('스토어에서 연체 대출 목록 로딩 실패:', error);
      // 에러 발생 시 목록을 비워둠
      overdueLoans.value = [];
    } finally {
      isLoading.value = false;
    }
  }

  return {
    overdueLoans,
    isLoading,
    fetchOverdueLoans,
  };
});
