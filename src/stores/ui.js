import { ref } from 'vue';
import { defineStore } from 'pinia';

export const useUiStore = defineStore('ui', () => {
  // --- State ---
  const isModalOpen = ref(false);
  const modalTitle = ref('');
  const modalMessage = ref('');
  const isErrorModal = ref(false); // 성공(파랑) / 에러(빨강) 모달 구분

  // --- Actions ---
  const openModal = ({ title, message, isError = false }) => {
    modalTitle.value = title;
    modalMessage.value = message;
    isErrorModal.value = isError;
    isModalOpen.value = true;
  };

  const closeModal = () => {
    isModalOpen.value = false;
    // 모달이 닫힐 때 내용을 초기화
    modalTitle.value = '';
    modalMessage.value = '';
    isErrorModal.value = false;
  };

  return {
    isModalOpen,
    modalTitle,
    modalMessage,
    isErrorModal,
    openModal,
    closeModal,
  };
});
