import { defineStore } from 'pinia';
import { useUiStore } from '@/stores/ui';
import {
  getBudgetRecommendation,
  saveDailyLimit,
  getBudgetStatus,
} from '@/api/budget';
import { useSpendingStore } from '@/stores/spending';
import { ref } from 'vue';

export const useBudgetStore = defineStore('budget', () => {
  // --- State ---
  const recommendedLimit = ref(0);
  const maximumLimit = ref(0);
  const currentLimit = ref(0);
  const lastUpdatedAt = ref(null);
  const isEditable = ref(true);

  // --- Actions ---

  // 추천/최대/현재 한도 정보를 불러오는 액션
  async function fetchBudgetRecommendation() {
    try {
      const data = await getBudgetRecommendation();
      recommendedLimit.value = data.recommendedDailyLimit;
      maximumLimit.value = data.maximumDailyLimit;
      currentLimit.value = data.currentDailyLimit;
    } catch (error) {
      console.error('스토어에서 추천 한도 정보 로딩 실패:', error);
    }
  }

  // 수정 가능 여부를 확인하는 액션
  async function fetchBudgetStatus() {
    try {
      const data = await getBudgetStatus();
      lastUpdatedAt.value = data.dailyLimitDate;

      // 이번 주에 이미 수정했는지 확인하는 로직
      if (lastUpdatedAt.value) {
        const now = new Date();
        const lastUpdateDate = new Date(lastUpdatedAt.value);
        const startOfWeek = new Date(
          now.setDate(now.getDate() - now.getDay() + 1)
        ).setHours(0, 0, 0, 0); // 이번 주 월요일
        const endOfWeek = new Date(
          now.setDate(now.getDate() - now.getDay() + 7)
        ).setHours(23, 59, 59, 999); // 이번 주 일요일

        if (lastUpdateDate >= startOfWeek && lastUpdateDate <= endOfWeek) {
          isEditable.value = false;
        } else {
          isEditable.value = true;
        }
      }
    } catch (error) {
      console.error('스토어에서 예산 설정 상태 로딩 실패:', error);
    }
  }

  // 최종 한도를 저장하는 액션
  async function saveFinalDailyLimit(newLimit) {
    const uiStore = useUiStore();
    const spendingStore = useSpendingStore();

    try {
      const updatedData = await saveDailyLimit(newLimit);
      spendingStore.updateDailyLimit(updatedData.dailyLimit);
      currentLimit.value = newLimit;
      lastUpdatedAt.value = new Date().toISOString();
      isEditable.value = false; // 저장 후에는 수정 불가능으로 변경
      uiStore.openModal({
        title: '저장 완료',
        message: '일일 소비 한도가 성공적으로 저장되었습니다.',
      });
    } catch (error) {
      console.error('스토어에서 한도 저장 실패:', error);
      uiStore.openModal({
        title: '저장 실패',
        message: error.response?.data?.message || '저장에 실패했습니다.',
        isError: true,
      });
    }
  }

  return {
    recommendedLimit,
    maximumLimit,
    currentLimit,
    isEditable,
    fetchBudgetRecommendation,
    fetchBudgetStatus,
    saveFinalDailyLimit,
  };
});
