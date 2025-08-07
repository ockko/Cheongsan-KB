<script setup>
import styles from '@/assets/styles/components/Home/DebtListWidget.module.css';
// import LoanAddModal from '@/components/domain/Home/LoanAddModal.vue';
import DebtDetailModal from '@/components/domain/Home/DebtDetailModal.vue';
import { ref, computed, defineProps } from 'vue';

const props = defineProps({
  debts: {
    type: Array,
    required: true,
  },
});

const isAddModalOpen = ref(false);
const isDetailModalOpen = ref(false);
const selectedDebt = ref(null);

// 상세 모달을 여는 메소드
const openDetailModal = (debt) => {
  selectedDebt.value = debt;
  isDetailModalOpen.value = true;
};

// 정렬 옵션을 저장할 반응형 변수
const sortOption = ref('startedAtDesc');

// 선택된 정렬 옵션에 따라 부채 목록을 동적으로 정렬하는 computed 속성
const sortedDebts = computed(() => {
  const debtsCopy = [...props.debts];

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
    case 'recommended': // 우리 앱 추천 순
    default:
      return debtsCopy.sort(
        (a, b) => new Date(b.loanStartDate) - new Date(a.loanStartDate)
      );
  }
});
</script>

<template>
  <div :class="styles.widgetCard">
    <div :class="styles.widgetHeader">
      <h3>총 대출 항목</h3>
    </div>
    <div :class="styles.widgetContent">
      <div :class="styles.controls">
        <button :class="styles.addButton">대출 상품 추가</button>
        <select :class="styles.sortSelect" v-model="sortOption">
          <option value="startedAtDesc">최신 순</option>
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

  <!-- <LoanAddModal v-if="isAddModalOpen" @close="isAddModalOpen = false" /> -->

  <DebtDetailModal
    v-if="isDetailModalOpen"
    :debt-details="selectedDebt"
    @close="isDetailModalOpen = false"
  />
</template>
