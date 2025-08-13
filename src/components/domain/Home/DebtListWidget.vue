<script setup>
import styles from '@/assets/styles/components/home/DebtListWidget.module.css';
import LoanAddModal from '@/components/domain/home/LoanAddModal.vue';
import DebtDetailModal from '@/components/domain/home/DebtDetailModal.vue';
import { ref, computed, onMounted } from 'vue';
import { getDebtListData } from '@/api/dashboard-bottomApi.js';

// 컴포넌트 내부에서 데이터를 관리
const debts = ref([]);
const loading = ref(true);
const error = ref(null);

const isAddModalOpen = ref(false);
const isDetailModalOpen = ref(false);
const selectedDebt = ref(null);

// 상세 모달을 여는 메소드
const openDetailModal = (debt) => {
  selectedDebt.value = debt;
  isDetailModalOpen.value = true;
};

// 추가 모달을 여는 메소드
const openAddModal = () => {
  isAddModalOpen.value = true;
};

// 정렬 옵션을 저장할 반응형 변수
const sortOption = ref('createdAtDesc');

// 데이터 로드 함수
const loadDebtData = async () => {
  try {
    loading.value = true;
    error.value = null;
    const data = await getDebtListData(sortOption.value);
    debts.value = data;
  } catch (err) {
    console.error('대출 목록 로드 실패:', err);
    error.value = '데이터를 불러오는데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

// 정렬 옵션 변경 시 데이터 다시 로드
const handleSortChange = async () => {
  await loadDebtData();
};

// 대출 목록 새로고침 함수
const refreshDebtList = async () => {
  await loadDebtData();
};

// 선택된 정렬 옵션에 따라 부채 목록을 동적으로 정렬하는 computed 속성
const sortedDebts = computed(() => {
  const debtsCopy = [...debts.value];

  switch (sortOption.value) {
    case 'repaymentRateDesc': // 상환율 높은 순
      return debtsCopy.sort((a, b) => b.repaymentRate - a.repaymentRate);
    case 'startedAtAsc': // 오래된 순
      // 날짜 비교를 위해 Date 객체로 변환하여 비교
      return debtsCopy.sort(
        (a, b) => new Date(a.loanStartDate) - new Date(b.loanStartDate)
      );
    case 'startedAtDesc': // 최신 순
      return debtsCopy.sort(
        (a, b) => new Date(b.loanStartDate) - new Date(a.loanStartDate)
      );
    case 'interestRateDesc': // 이자율 높은 순
      return debtsCopy.sort((a, b) => b.interestRate - a.interestRate);
    case 'recommended': // 우리 앱 추천 순
    default:
      return debtsCopy.sort(
        (a, b) => new Date(b.loanStartDate) - new Date(a.loanStartDate)
      );
  }
});

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadDebtData();
});
</script>

<template>
  <div :class="styles.widgetCard">
    <div :class="styles.widgetHeader">
      <h3>총 대출 항목</h3>
    </div>
    <div :class="styles.widgetContent">
      <!-- 로딩 상태 -->
      <div v-if="loading" :class="styles.loading">데이터를 불러오는 중...</div>

      <!-- 에러 상태 -->
      <div v-else-if="error" :class="styles.error">
        {{ error }}
        <button @click="loadDebtData" :class="styles.retryButton">
          재시도
        </button>
      </div>

      <!-- 데이터 표시 -->
      <div v-else>
        <div :class="styles.controls">
          <button :class="styles.addButton" @click="openAddModal()">
            대출 상품 추가
          </button>
          <select
            :class="styles.sortSelect"
            v-model="sortOption"
            @change="handleSortChange"
          >
            <option value="createdAtDesc">최신 순</option>
            <option value="startedAtAsc">오래된 순</option>
            <option value="interestRateDesc">이자율 높은 순</option>
            <option value="repaymentRateDesc">상환율 높은 순</option>
            <option value="recommended">우리 앱 추천 순</option>
          </select>
        </div>
        <div :class="styles.listHeader">
          <span>대출명</span>
          <span>상환율</span>
        </div>
        <ul>
          <li
            v-for="debt in sortedDebts"
            :key="debt.debtId"
            :class="styles.debtItem"
            @click="openDetailModal(debt)"
          >
            <div>
              <div :class="styles.nameDetails">
                <span :class="styles.organizationName">{{
                  debt.organizationName
                }}</span>
                <span :class="styles.debtName">{{ debt.debtName }}</span>
              </div>
            </div>
            <div :class="styles.repaymentInfo">
              <div :class="styles.progressContainer">
                <progress
                  :class="styles.repaymentProgress"
                  :value="debt.repaymentRate * 100"
                  max="100"
                ></progress>
                <span :class="styles.repaymentPercent"
                  >{{ (debt.repaymentRate * 100).toFixed(0) }}%</span
                >
              </div>
              <span :class="styles.arrow">&gt;</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>

  <LoanAddModal
    :visible="isAddModalOpen"
    @close="isAddModalOpen = false"
    @refresh-debt-list="refreshDebtList"
  />

  <DebtDetailModal
    v-if="isDetailModalOpen"
    :debt-details="selectedDebt"
    @close="isDetailModalOpen = false"
  />
</template>
