<script setup>
import FullscreenModal from '@/components/domain/mypage/FullscreenModal.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useMyPageStore } from '@/stores/mypage';
import styles from '@/assets/styles/pages/mypage/EditEmail.module.css';
const store = useMyPageStore();
const router = useRouter();

const inputValue = ref('');
const save = async () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(inputValue.value.trim())) {
    alert('유효한 이메일 주소를 입력해 주세요.');
    return;
  }

  try {
    await store.updateEmail(inputValue.value.trim());
    router.back();
  } catch (error) {
    alert('이메일 변경 실패: ' + (error.message || '알 수 없는 오류'));
  }
};
const onClose = () => {
  router.back();
};
</script>
<template>
  <FullscreenModal title="이메일 변경" @close="onClose">
    <template #description>
      <p :class="styles.description">이메일을 입력해 주세요.</p>
    </template>

    <template #input>
      <div :class="styles.inputWrapper">
        <input v-model="inputValue" type="text" placeholder="새 이메일 입력" />
        <i
          :class="[styles.clearBtn, 'fa-solid', 'fa-circle-xmark']"
          v-if="inputValue.length > 0"
          @click="inputValue = ''"
        ></i>
      </div>
    </template>

    <template #footer>
      <div
        :class="[
          styles.customFooter,
          { [styles.active]: inputValue.trim().length > 0 },
        ]"
        ref="footerRef"
      >
        <button @click="save" :disabled="!inputValue.trim().length">
          저장하기
        </button>
      </div>
    </template>
  </FullscreenModal>
</template>
