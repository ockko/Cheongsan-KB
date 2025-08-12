import { defineStore } from 'pinia';
import { getDailySpending } from '@/api/spending';
import { ref } from 'vue';

export const useSpendingStore = defineStore('spending', () => {
  // --- State ---
  const spendingData = ref({
    dailyLimit: 0,
    spent: 0,
    remaining: 0,
  });

  // --- Actions ---

  // 오늘의 지출 현황 데이터를 불러오는 액션
  async function fetchDailySpending() {
    try {
      const data = await getDailySpending();
      spendingData.value = {
        dailyLimit: data.dailyLimit,
        spent: data.spent,
        remaining: data.remaining,
      };
    } catch (error) {
      console.error('스토어에서 지출 현황 로딩 실패:', error);
    }
  }

  return {
    spendingData,
    fetchDailySpending,
  };
});
