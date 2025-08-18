<script setup>
import { useRouter } from 'vue-router';
import styles from '@/assets/styles/components/mypage/FinalConfirmPopup.module.css';
const router = useRouter();
const props = defineProps({
  isWithdrawn: {
    type: Boolean,
    default: false,
  },
  errorMessage: {
    type: String,
    default: '',
  },
  loading: {
    type: Boolean,
    default: false,
  },
});
const emit = defineEmits(['close', 'confirmed']);

const confirm = () => {
  emit('confirmed');
};

const onBackdropClick = () => {
  emit('close');
};

const onPopupClick = (event) => {
  event.stopPropagation();
};

const onCompleteConfirm = async () => {
  await router.push('/'); // 메인 페이지 등 이동
  emit('close'); // 팝업 닫기
};
</script>
<template>
  <div :class="styles.popupBackdrop" @click="onBackdropClick">
    <div :class="styles.popup" @click="onPopupClick">
      <h3 v-if="!isWithdrawn" :class="['text-light']">탈퇴 확인</h3>
      <h3 v-else>탈퇴 완료</h3>

      <div :class="styles.icon">
        <i class="fa-solid fa-arrow-right-from-bracket"></i>
      </div>

      <div v-if="!isWithdrawn" :class="styles.description">
        서비스를 탈퇴합니다.
      </div>
      <div v-else>
        <p :class="styles.description">탈퇴가 완료되었습니다.</p>
        <div :class="styles.popupButtons">
          <button :class="styles.btnComplete" @click="onCompleteConfirm">
            확인
          </button>
        </div>
      </div>

      <p v-if="errorMessage" :class="styles.errorMsg">{{ errorMessage }}</p>

      <div v-if="!isWithdrawn && !errorMessage" :class="styles.popupButtons">
        <button
          :class="styles.btnWithdraw"
          @click="confirm"
          :disabled="loading"
        >
          {{ loading ? '처리 중...' : '회원 탈퇴' }}
        </button>
        <button
          :class="styles.btnCancel"
          @click="emit('close')"
          :disabled="loading"
        >
          취소
        </button>
      </div>

      <!-- 탈퇴 실패 시 취소 버튼만 -->
      <div
        v-else-if="!isWithdrawn && errorMessage"
        :class="styles.popupButtons"
      >
        <button :class="styles.btnCancel" @click="emit('close')">닫기</button>
      </div>
    </div>
  </div>
</template>
