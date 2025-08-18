<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useMyPageStore } from '@/stores/mypage';
import { useUiStore } from '@/stores/ui';
import FullscreenModal from '@/components/domain/mypage/FullscreenModal.vue';
import PasswordConfirmPopup from '@/components/domain/mypage/PasswordConfirmPopup.vue';
import FinalConfirmPopup from '@/components/domain/mypage/FinalConfirmPopup.vue';
import styles from '@/assets/styles/pages/mypage/MemberWithdraw.module.css';
const store = useMyPageStore();
const router = useRouter();
const uiStore = useUiStore();
const showPasswordPopup = ref(false);
const showFinalPopup = ref(false);
const lastConfirmedPassword = ref('');
const isWithdrawn = ref(false);
const goBack = () => {
  router.back();
};

const handlePasswordConfirmed = (password) => {
  lastConfirmedPassword.value = password;
  showPasswordPopup.value = false;
  showFinalPopup.value = true;
};

const handleWithdraw = async () => {
  try {
    const success = await store.deleteAccount({
      password: lastConfirmedPassword.value,
    });

    if (success) {
      isWithdrawn.value = true;
      uiStore.openModal({
        title: '탈퇴 완료',
        message: '회원 탈퇴가 정상적으로 완료되었습니다.',
        isError: false,
        onConfirmCallback: () => {
          router.push('/'); // 확인 버튼 누른 후 메인 페이지로 이동 등
        },
      });
    } else {
      uiStore.openModal({
        title: '탈퇴 실패',
        message: '회원 탈퇴에 실패했습니다.',
        isError: true,
      });
    }
  } catch (error) {
    console.error('회원 탈퇴 실패:', error);
    uiStore.openModal({
      title: '탈퇴 실패',
      message: '회원 탈퇴 중 오류가 발생했습니다. 다시 시도해주세요.',
      isError: true,
    });
  }
};
</script>

<template>
  <FullscreenModal title="회원 탈퇴" @close="goBack">
    <template #description>
      <section :class="styles.content">
        <p :class="styles.noticeTitle">⚠ 탈퇴 전 확인하세요</p>
        <p :class="styles.noticeSub">탈퇴하시면 모든 데이터가 삭제됩니다.</p>
      </section>
    </template>

    <template #footer>
      <div :class="styles.customFooter">
        <button @click="showPasswordPopup = true">탈퇴하기</button>
      </div>
    </template>

    <!-- 비밀번호 확인 팝업 -->
    <PasswordConfirmPopup
      v-if="showPasswordPopup"
      @close="showPasswordPopup = false"
      @confirmed="handlePasswordConfirmed"
    />

    <!-- 최종 확인 팝업 -->
    <FinalConfirmPopup
      v-if="showFinalPopup"
      :isWithdrawn="isWithdrawn"
      @close="showFinalPopup = false"
      @confirmed="handleWithdraw"
    />
  </FullscreenModal>
</template>
