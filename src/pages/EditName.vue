<script setup>
import FullscreenModal from '@/components/domain/mypage/FullscreenModal.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useMyPageStore } from '@/stores/mypage';
import { useUiStore } from '@/stores/ui';
import styles from '@/assets/styles/pages/mypage/EditName.module.css';

const store = useMyPageStore();
const router = useRouter();
const uiStore = useUiStore();

const inputValue = ref('');
const save = async () => {
  if (!inputValue.value.trim()) {
    uiStore.openModal({
      title: '입력 오류',
      message: '이름을 입력해 주세요.',
      isError: true,
    });
    return;
  }

  try {
    await store.updateNickname(inputValue.value.trim());

    uiStore.openModal({
      title: '변경 완료',
      message: '이름이 정상적으로 변경되었습니다.',
      isError: false,
      onConfirmCallback: () => {
        router.back(); // 확인 버튼 누른 후 이전 페이지로 이동
      },
    });
  } catch (error) {
    uiStore.openModal({
      title: '변경 실패',
      message: '이름 변경 실패: ' + (error.message || '알 수 없는 오류'),
      isError: true,
    });
  }
};

const onClose = () => {
  router.back();
};
</script>

<template>
  <FullscreenModal title="이름 변경" @close="onClose">
    <template #description>
      <p :class="styles.description">이름을 입력해 주세요.</p>
    </template>

    <template #input>
      <div :class="styles.inputWrapper">
        <input
          v-model="inputValue"
          type="text"
          maxlength="10"
          placeholder="새 이름 입력"
        />
        <i
          :class="[styles.clearBtn, 'fa-solid', 'fa-circle-xmark']"
          v-if="inputValue.length > 0"
          @click="inputValue = ''"
        ></i>
        <span :class="styles.charCount">{{ inputValue.length }} / 10</span>
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
