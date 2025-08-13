<script setup>
import styles from '@/assets/styles/components/home/RepaymentPlanWidget.module.css';
import { ref, onMounted, watch, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { getRepaymentPlanData } from '@/api/dashboard-bottomApi.js';
import MyPlanModal from './MyPlanModal.vue';

// 컴포넌트 내부에서 데이터를 관리
const planData = ref({
  monthlyPayment: null,
  totalMonths: null,
  finalDebtFreeDate: null,
  strategyType: null,
  interestSaved: null,
  totalPayment: null,
  originalPayment: null,
  totalPrepaymentFee: null,
  sortedLoanNames: [],
  repaymentHistory: {},
  debtFreeDates: {},
});

const router = useRouter();
const loading = ref(true);
const error = ref(null);
const isModalOpen = ref(false);

// isModalOpen 값 변화 추적
watch(isModalOpen, (newVal) => {
  console.log('RepaymentPlanWidget isModalOpen changed to:', newVal);
});

// 데이터 로드 함수
const loadPlanData = async () => {
  try {
    loading.value = true;
    error.value = null;
    const data = await getRepaymentPlanData();
    planData.value = data;
  } catch (err) {
    console.error('상환 계획 데이터 로드 실패:', err);

    // bad_request 에러인지 확인
    if (err.response && err.response.status === 400) {
      error.value =
        '전략이 없습니다. 시뮬레이션을 통해 상환 전략을 설정해주세요.';
    } else {
      error.value = '데이터를 불러오는데 실패했습니다.';
    }
  } finally {
    loading.value = false;
  }
};

// 시뮬레이션 페이지로 이동하는 함수
const goToSimulation = () => {
  router.push('/simulation');
};

// 모달 열기 함수
const openPlanModal = () => {
  console.log('RepaymentPlanWidget openPlanModal 함수 호출됨');
  if (planData.value.strategyType) {
    isModalOpen.value = true;
  } else {
    console.log('전략이 없음, 모달을 열 수 없습니다');
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadPlanData();
});

// 숫자를 '만원' 단위 텍스트로 변환하는 헬퍼 함수
const formatToManwon = (value) => {
  if (typeof value !== 'number' || value === 0) return '0 원';
  // 10000으로 나누어 만원 단위로 변환 후 콤마 추가
  const manwon = Math.round(value / 10000);
  return `${manwon.toLocaleString('ko-KR')} 만원`;
};

// 날짜를 한국어 형식으로 변환하는 헬퍼 함수
const formatDateToKorean = (dateString) => {
  if (!dateString) return '날짜 없음';
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  return `${year}년 ${month}월`;
};
</script>

<template>
  <div
    :class="[styles.widgetCard, { [styles.disabled]: !planData.strategyType }]"
    @click="planData.strategyType ? openPlanModal() : null"
  >
    <div :class="styles.widgetHeader">
      <h3
        :class="[
          styles.clickableTitle,
          { [styles.disabledTitle]: !planData.strategyType },
        ]"
        :title="
          planData.strategyType
            ? '클릭하여 상세 상환 전략 보기'
            : '상환 전략이 없습니다'
        "
      >
        나의 청산 플랜
      </h3>
    </div>

    <div :class="styles.widgetContent">
      <!-- 로딩 상태 -->
      <div v-if="loading" :class="styles.loading">데이터를 불러오는 중...</div>

      <!-- 에러 상태 -->
      <div v-else-if="error" :class="styles.error">
        <div :class="styles.errorMessage">{{ error }}</div>
        <div :class="styles.errorActions">
          <button @click.stop="loadPlanData" :class="styles.retryButton">
            재시도
          </button>
        </div>
      </div>

      <!-- 데이터 표시 -->
      <div v-else>
        <!-- 데이터가 있는 경우 -->
        <div
          v-if="
            planData.monthlyPayment !== null && planData.totalMonths !== null
          "
        >
          <div :class="styles.planItem">
            <span :class="styles.label">월 상환액</span>
            <span :class="[styles.value, styles.highlight]">{{
              formatToManwon(planData.monthlyPayment)
            }}</span>
          </div>

          <div :class="styles.planItem">
            <span :class="styles.label">총 상환 기간</span>
            <span :class="styles.value">{{ planData.totalMonths }}개월</span>
          </div>

          <div :class="styles.planItem">
            <span :class="styles.label">최종 빚 졸업일</span>
            <span :class="styles.value">{{
              formatDateToKorean(planData.finalDebtFreeDate)
            }}</span>
          </div>
        </div>

        <!-- 데이터가 없는 경우 시뮬레이션 안내 -->
        <div v-else :class="styles.noDataMessage">
          <div :class="styles.noDataTitle">상환 계획이<br />없습니다</div>
          <button @click.stop="goToSimulation" :class="styles.simulationButton">
            시뮬레이션<br />
            시작하기
          </button>
        </div>
      </div>
    </div>

    <!-- MyPlanModal 컴포넌트 -->
    <MyPlanModal
      :isOpen="isModalOpen"
      :strategy="planData"
      @close="isModalOpen = false"
    />
  </div>
</template>
