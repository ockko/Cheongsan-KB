import { defineStore } from 'pinia';
import { calendarApi } from '@/api/calendarApi';
import { computed, ref } from 'vue';

export const useCalendarStore = defineStore('calendar', () => {
  // State
  const monthlyTransactions = ref([]);
  const monthlyRepayments = ref([]);
  const selectedDateDetails = ref({
    transactions: [],
    repayments: [],
  });
  const loading = ref(false);
  const error = ref(null);

  // Getters - computed
  const transactionDataByDate = computed(() => {
    const data = {};
    monthlyTransactions.value.forEach((transaction) => {
      const date = new Date(transaction.transactionDate).getDate();
      data[date] = {
        income: transaction.totalIncome || 0,
        expense: transaction.totalExpense || 0,
        netAmount: transaction.netAmount || 0,
      };
    });
    return data;
  });

  const loanDataByDate = computed(() => {
    const data = {};
    monthlyRepayments.value.forEach((repayment) => {
      const date = new Date(repayment.repaymentDate).getDate();
      if (!data[date]) {
        data[date] = [];
      }
      data[date].push({
        debtId: repayment.debtId,
        debtName: repayment.debtName,
        type: '대출',
        label: '대출',
      });
    });
    return data;
  });

  // Actions
  const setError = (errorMessage) => {
    error.value = errorMessage;
    console.error('Calendar Store Error:', errorMessage);
  };

  const clearError = () => {
    error.value = null;
  };

  // 월별 데이터 로드
  const loadMonthlyData = async (year, month) => {
    loading.value = true;
    clearError();

    try {
      // 두 API를 병렬로 호출
      const [transactionsData, repaymentsData] = await Promise.all([
        calendarApi.getMonthlyTransactions(year, month),
        calendarApi.getMonthlyRepayments(year, month),
      ]);

      monthlyTransactions.value = transactionsData;
      monthlyRepayments.value = repaymentsData;
    } catch (err) {
      setError(`월별 데이터 로드 실패: ${err.message}`);
    } finally {
      loading.value = false;
    }
  };

  // 특정 날짜 상세 데이터 로드
  const loadDailyDetails = async (dateString) => {
    loading.value = true;
    clearError();

    try {
      // 두 API를 병렬로 호출
      const [transactionsData, repaymentsData] = await Promise.all([
        calendarApi.getDailyTransactions(dateString),
        calendarApi.getDailyRepayments(dateString),
      ]);

      selectedDateDetails.value = {
        transactions: transactionsData,
        repayments: repaymentsData,
      };
    } catch (err) {
      setError(`일별 상세 데이터 로드 실패: ${err.message}`);
    } finally {
      loading.value = false;
    }
  };

  // 날짜 포맷 유틸리티 (YYYY-MM-DD)
  const formatDateString = (year, month, day) => {
    const paddedMonth = month.toString().padStart(2, '0');
    const paddedDay = day.toString().padStart(2, '0');
    return `${year}-${paddedMonth}-${paddedDay}`;
  };

  // 상태 초기화
  const reset = () => {
    monthlyTransactions.value = [];
    monthlyRepayments.value = [];
    selectedDateDetails.value = { transactions: [], repayments: [] };
    error.value = null;
    loading.value = false;
  };

  return {
    // State
    monthlyTransactions,
    monthlyRepayments,
    selectedDateDetails,
    loading,
    error,

    // Getters
    transactionDataByDate,
    loanDataByDate,

    // Actions
    loadMonthlyData,
    loadDailyDetails,
    formatDateString,
    reset,
    clearError,
  };
});
