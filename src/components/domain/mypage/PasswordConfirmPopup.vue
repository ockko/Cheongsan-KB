<script setup>
import { ref, computed } from 'vue';
import { useMyPageStore } from '@/stores/mypage';
import styles from '@/assets/styles/components/mypage/PasswordConfirmPopup.module.css';
import { useUiStore } from '@/stores/ui';
const store = useMyPageStore();
const password = ref('');
const emit = defineEmits(['close', 'confirmed']);

const checkPassword = async () => {
  const uiStore = useUiStore();

  if (!password.value.trim()) {
    uiStore.openModal({
      title: '입력 오류',
      message: '비밀번호를 입력하세요.',
      isError: true,
    });
    return;
  }

  const isValid = await store.verifyPassword(password.value);

  if (isValid) {
    emit('confirmed', password.value);
  } else {
    uiStore.openModal({
      title: '인증 실패',
      message: '비밀번호가 올바르지 않습니다.',
      isError: true,
    });
  }
};

const isDisabled = computed(() => !password.value.trim());
// 바깥 영역 클릭 시 닫기
const onBackdropClick = () => {
  emit('close');
};

// 팝업 내부 클릭 시 이벤트 전파 막기
const onPopupClick = (event) => {
  event.stopPropagation();
};
</script>
<template>
  <div :class="styles.popupBackdrop" @click="onBackdropClick">
    <div :class="styles.popup" @click="onPopupClick">
      <h3 class="text-light">비밀번호 확인</h3>
      <input
        type="password"
        v-model="password"
        placeholder="비밀번호 입력"
        :class="styles.pwinput"
      />
      <div :class="styles.popupButtons" v-if="!isWithdrawn && !errorMessage">
        <button
          @click="checkPassword"
          :class="[styles.withdrawButton, { [styles.disabled]: isDisabled }]"
          :disabled="isDisabled"
        >
          확인
        </button>
        <button
          :class="styles.btnCancel"
          @click="emit('close')"
          :disabled="loading"
        >
          취소
        </button>
      </div>
    </div>
  </div>
</template>
