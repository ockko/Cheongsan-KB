import { ref } from 'vue';
import { defineStore } from 'pinia';

export const useModalStore = defineStore('modal', () => {
  // 모달 상태들
  const isPolicyDetailModalOpen = ref(false);
  const isDiagnosisStageModalOpen = ref(false);

  // 전체 모달 상태 (하나라도 열려있으면 true)
  const isAnyModalOpen = ref(false);

  // 정책 상세 모달 관련 함수들
  const openPolicyDetailModal = () => {
    isPolicyDetailModalOpen.value = true;
    updateAnyModalOpen();
  };

  const closePolicyDetailModal = () => {
    isPolicyDetailModalOpen.value = false;
    updateAnyModalOpen();
  };

  // 진단 단계 모달 관련 함수들
  const openDiagnosisStageModal = () => {
    isDiagnosisStageModalOpen.value = true;
    updateAnyModalOpen();
  };

  const closeDiagnosisStageModal = () => {
    isDiagnosisStageModalOpen.value = false;
    updateAnyModalOpen();
  };

  // 전체 모달 상태 업데이트
  const updateAnyModalOpen = () => {
    isAnyModalOpen.value =
      isPolicyDetailModalOpen.value || isDiagnosisStageModalOpen.value;
  };

  // 모든 모달 닫기
  const closeAllModals = () => {
    isPolicyDetailModalOpen.value = false;
    isDiagnosisStageModalOpen.value = false;
    updateAnyModalOpen();
  };

  // 모달 상태 초기화
  const resetModalState = () => {
    isPolicyDetailModalOpen.value = false;
    isDiagnosisStageModalOpen.value = false;
    isAnyModalOpen.value = false;
  };

  return {
    // 상태
    isPolicyDetailModalOpen,
    isDiagnosisStageModalOpen,
    isAnyModalOpen,

    // 액션
    openPolicyDetailModal,
    closePolicyDetailModal,
    openDiagnosisStageModal,
    closeDiagnosisStageModal,
    closeAllModals,
    resetModalState,
  };
});
