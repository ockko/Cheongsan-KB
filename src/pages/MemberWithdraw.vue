<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useMyPageStore } from '@/stores/mypage';
import FullscreenModal from '@/components/domain/mypage/FullscreenModal.vue';
import PasswordConfirmPopup from '@/components/domain/mypage/PasswordConfirmPopup.vue';
import FinalConfirmPopup from '@/components/domain/mypage/FinalConfirmPopup.vue';
import styles from '@/assets/styles/pages/mypage/MemberWithdraw.module.css';
const store = useMyPageStore();
const router = useRouter();

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
  console.log('비밀번호: ', lastConfirmedPassword.value);
  const success = await store.deleteAccount({
    password: lastConfirmedPassword.value,
  });
  if (success) {
    isWithdrawn.value = true;
  } else {
    alert('회원 탈퇴 실패');
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
