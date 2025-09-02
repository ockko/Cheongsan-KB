<script setup>
import FullscreenModal from '@/components/domain/mypage/FullscreenModal.vue';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useMyPageStore } from '@/stores/mypage';
import { useUiStore } from '@/stores/ui';
import styles from '@/assets/styles/pages/mypage/EditPassword.module.css';

const store = useMyPageStore();
const router = useRouter();
const uiStore = useUiStore();

const step = ref(1);
const descriptionText = ref('보안을 위해 현재 비밀번호를 입력해주세요.');
const inputValue = ref('');
const oldPassword = ref('');
const newPassword = ref('');

const inputPlaceholder = computed(() => {
  if (step.value === 1) return '현재 비밀번호 입력';
  if (step.value === 2) return '변경할 비밀번호 입력';
  if (step.value === 3) return '변경할 비밀번호 확인 입력';
  return '';
});
const save = async () => {
  if (step.value === 1) {
    if (!inputValue.value) {
      uiStore.openModal({
        title: '입력 오류',
        message: '현재 비밀번호를 입력해주세요.',
        isError: true,
      });
      return;
    }
    try {
      const isValid = await store.verifyPassword(inputValue.value);
      if (!isValid) {
        uiStore.openModal({
          title: '인증 실패',
          message: '현재 비밀번호가 올바르지 않습니다. 다시 입력해주세요.',
          isError: true,
        });
        inputValue.value = '';
        return;
      }
      oldPassword.value = inputValue.value;
      descriptionText.value = '변경할 비밀번호를 입력해주세요.';
      step.value = 2;
      inputValue.value = '';
    } catch (error) {
      uiStore.openModal({
        title: '오류',
        message: '비밀번호 검증 실패: ' + (error.message || '알 수 없는 오류'),
        isError: true,
      });
    }
  } else if (step.value === 2) {
    if (inputValue.value.length < 8) {
      uiStore.openModal({
        title: '입력 오류',
        message: '비밀번호는 8자 이상이어야 합니다.',
        isError: true,
      });
      return;
      x``;
    }
    newPassword.value = inputValue.value;
    descriptionText.value = '변경할 비밀번호를 확인해주세요.';
    step.value = 3;
    inputValue.value = '';
  } else if (step.value === 3) {
    if (inputValue.value !== newPassword.value) {
      uiStore.openModal({
        title: '입력 오류',
        message: '비밀번호가 일치하지 않습니다.',
        isError: true,
      });
      inputValue.value = '';
      return;
    }

    try {
      await store.changePassword({
        oldPassword: oldPassword.value,
        newPassword: newPassword.value,
      });
      uiStore.openModal({
        title: '변경 완료',
        message: '비밀번호가 성공적으로 변경되었습니다.',
        isError: false,
        onConfirmCallback: () => {
          router.back(); // 확인 버튼 누른 후 이전 페이지로 이동
        },
      });
    } catch (error) {
      uiStore.openModal({
        title: '변경 실패',
        message:
          '비밀번호 변경 실패: ' +
          (error.response?.data?.message || error.message || '알 수 없는 오류'),
        isError: true,
      });
    }
  }
};

const onClose = () => {
  router.back();
};
</script>

<template>
  <FullscreenModal title="비밀번호 변경" @close="onClose">
    <template #description>
      <p :class="styles.description">{{ descriptionText }}</p>
    </template>

    <template #input>
      <div :class="styles.inputWrapper">
        <input
          v-model="inputValue"
          type="password"
          :placeholder="inputPlaceholder"
          maxlength="20"
        />
        <i
          v-if="inputValue.length > 0"
          :class="[styles.clearBtn, 'fa-solid', 'fa-circle-xmark']"
          @click="inputValue = ''"
        ></i>
        <span :class="styles.charCount">{{ inputValue.length }} / 20</span>
      </div>
    </template>

    <template #footer>
      <div
        :class="[
          styles.customFooter,
          { [styles.active]: inputValue.trim().length > 0 },
        ]"
      >
        <button @click="save" :disabled="inputValue.trim().length === 0">
          {{ step === 3 ? '저장하기' : '다음' }}
        </button>
      </div>
    </template>
  </FullscreenModal>
</template>
