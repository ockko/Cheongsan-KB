<script setup>
import styles from '@/assets/styles/components/common/BaseModal.module.css';
import { useUiStore } from '@/stores/ui';

const uiStore = useUiStore();
</script>

<template>
  <div
    v-if="uiStore.isModalOpen"
    :class="styles.modalOverlay"
    @click.self="uiStore.closeModal"
  >
    <div :class="styles.modalContent">
      <h3 :class="[styles.modalTitle, { 'error-title': uiStore.isErrorModal }]">
        {{ uiStore.modalTitle }}
      </h3>
      <p :class="styles.modalMessage">{{ uiStore.modalMessage }}</p>
      <button :class="styles.closeButton" @click="uiStore.confirmModal">
        확인
      </button>
    </div>
  </div>
</template>
