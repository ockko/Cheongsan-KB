<script setup>
import { computed, watch, onMounted, onUnmounted } from 'vue';
import { useModalStore } from '@/stores/modal';
import styles from '@/assets/styles/components/policy/DiagnosisStageModal.module.css';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false,
  },
  detailData: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['close']);

// 모달 스토어 사용
const modalStore = useModalStore();

// props.isVisible 변화를 감지하여 스토어 상태 업데이트
watch(
  () => props.isVisible,
  (newValue) => {
    if (newValue) {
      modalStore.openDiagnosisStageModal();
    } else {
      modalStore.closeDiagnosisStageModal();
    }
  },
  { immediate: true }
);

const closeModal = () => {
  emit('close');
};

// 뒤로가기 이벤트 처리
const handlePopState = () => {
  if (props.isVisible) {
    closeModal();
  }
};

// 컴포넌트 마운트 시 뒤로가기 이벤트 리스너 추가
onMounted(() => {
  window.addEventListener('popstate', handlePopState);
});

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('popstate', handlePopState);
});

// 모달이 열릴 때와 닫힐 때 history 상태 관리
watch(
  () => props.isVisible,
  (newValue) => {
    if (newValue) {
      // 모달이 열릴 때 history에 상태 추가
      window.history.pushState(
        { modal: 'diagnosisStage' },
        '',
        window.location.href
      );
    } else {
      // 모달이 닫힐 때 history 상태 정리
      if (
        window.history.state &&
        window.history.state.modal === 'diagnosisStage'
      ) {
        window.history.back();
      }
    }
  }
);

// 대상자 목록 생성 (eligibleDebtors와 eligibleDebts 결합)
const targetList = computed(() => {
  const targets = [];

  // eligibleDebtors 처리
  if (props.detailData?.eligibleDebtors) {
    if (Array.isArray(props.detailData.eligibleDebtors)) {
      targets.push(...props.detailData.eligibleDebtors);
    } else if (typeof props.detailData.eligibleDebtors === 'string') {
      // 문자열인 경우 쉼표로 분리
      const debtors = props.detailData.eligibleDebtors
        .split(',')
        .map((item) => item.trim());
      targets.push(...debtors);
    }
  }

  // eligibleDebts 처리
  if (props.detailData?.eligibleDebts) {
    if (Array.isArray(props.detailData.eligibleDebts)) {
      targets.push(...props.detailData.eligibleDebts);
    } else if (typeof props.detailData.eligibleDebts === 'string') {
      // 문자열인 경우 쉼표로 분리
      const debts = props.detailData.eligibleDebts
        .split(',')
        .map((item) => item.trim());
      targets.push(...debts);
    }
  }

  return targets.length > 0 ? targets : ['대상자 정보가 없습니다.'];
});

// 장점 목록 생성
const advantagesList = computed(() => {
  if (props.detailData?.advantages) {
    if (Array.isArray(props.detailData.advantages)) {
      return props.detailData.advantages;
    } else if (typeof props.detailData.advantages === 'string') {
      // 문자열인 경우 쉼표로 분리
      return props.detailData.advantages.split(',').map((item) => item.trim());
    }
  }
  return [];
});

// 주의사항 목록 생성
const cautionsList = computed(() => {
  if (props.detailData?.cautions) {
    if (Array.isArray(props.detailData.cautions)) {
      return props.detailData.cautions;
    } else if (typeof props.detailData.cautions === 'string') {
      // 문자열인 경우 쉼표로 분리
      return props.detailData.cautions.split(',').map((item) => item.trim());
    }
  }
  return [];
});
</script>

<template>
  <div
    v-if="isVisible"
    :class="styles.diagnosisStageModalOverlay"
    @click="closeModal"
  >
    <div :class="styles.diagnosisStageModalContent" @click.stop>
      <!-- 모달 내용 -->
      <div :class="styles.diagnosisStageModalBody">
        <!-- 뒤로가기 버튼 -->
        <button @click="closeModal" :class="styles.diagnosisStageBackButton">
          <i class="fa fa-arrow-left"></i>
        </button>

        <!-- 제목 섹션 -->
        <div :class="styles.diagnosisStageTitleSection">
          <div :class="styles.diagnosisStageInstitutionInfo">
            <div :class="styles.diagnosisStageInstitutionIcon">
              <img src="/images/court2.png" alt="기관 아이콘" />
            </div>
            <span :class="styles.diagnosisStageInstitutionName">{{
              props.detailData?.institution || props.detailData?.operatingEntity
            }}</span>
          </div>
          <h1 :class="styles.diagnosisStageStageTitle">
            {{ props.detailData?.programName }}
          </h1>
          <p :class="styles.diagnosisStageStageDescription">
            {{ props.detailData?.description }}
          </p>
        </div>

        <!-- 대상자 섹션 -->
        <div :class="styles.diagnosisStageSection">
          <div :class="styles.diagnosisStageDivider"></div>
          <h2 :class="styles.diagnosisStageSectionTitle">대상자</h2>
          <ul :class="styles.diagnosisStageSectionList">
            <li
              v-for="(item, index) in targetList"
              :key="`target-${index}`"
              :class="styles.diagnosisStageListItem"
            >
              <div :class="styles.diagnosisStageForListContent">
                <span :class="styles.diagnosisStageInfoIcon">
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <circle
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="#003E65"
                      stroke-width="2"
                    />
                    <path
                      d="M12 16V12"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                    <path
                      d="M12 8H12.01"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.diagnosisStageListText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 장점 섹션 -->
        <div :class="styles.diagnosisStageSection">
          <div :class="styles.diagnosisStageDivider"></div>
          <h2 :class="styles.diagnosisStageSectionTitle">장점</h2>
          <ul :class="styles.diagnosisStageSectionList">
            <li
              v-for="(item, index) in advantagesList"
              :key="`advantage-${index}`"
              :class="styles.diagnosisStageListItem"
            >
              <div :class="styles.diagnosisStageAdvantagesListContent">
                <span :class="styles.diagnosisStageCheckIcon">
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M20 6L9 17L4 12"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.diagnosisStageListText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 주의사항 섹션 -->
        <div :class="styles.diagnosisStageSection">
          <div :class="styles.diagnosisStageDivider"></div>
          <h2 :class="styles.diagnosisStageSectionTitle">주의사항</h2>
          <ul :class="styles.diagnosisStageSectionList">
            <li
              v-for="(item, index) in cautionsList"
              :key="`warning-${index}`"
              :class="styles.diagnosisStageListItem"
            >
              <div :class="styles.diagnosisStageWarnListContent">
                <span :class="styles.diagnosisStageWarningIcon">
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M10.29 3.86L1.82 18A2 2 0 0 0 3.54 21H20.46A2 2 0 0 0 22.18 18L13.71 3.86A2 2 0 0 0 10.29 3.86Z"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                    <path
                      d="M12 9V13"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                    <path
                      d="M12 17H12.01"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.diagnosisStageListText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
