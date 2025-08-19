<script setup>
import { useUiStore } from '@/stores/ui';
import styles from '@/assets/styles/pages/InitialSetup/InitialSetup1.module.css';
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { saveNickname } from '@/api/initialSetup/initialSetup1.js';
import { useAuthStore } from '@/stores/auth.js';

const nickname = ref('');
const router = useRouter();
const authStore = useAuthStore();
const uiStore = useUiStore();

const nicknameRegex = /^[가-힣a-zA-Z0-9_]{2,10}$/;

const goNext = async () => {
  const nicknameValue = nickname.value.trim();

  if (!nicknameRegex.test(nicknameValue)) {
    uiStore.openModal({
      title: '입력 오류',
      message:
        '닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용 가능하며 2~10자 이내여야 합니다.',
      isError: true,
    });
    return;
  }

  try {
    const response = await saveNickname(nicknameValue);

    authStore.state.user.nickName = nicknameValue;
    localStorage.setItem('auth', JSON.stringify(authStore.state));

    authStore.state.user.nickName = nicknameValue;
    localStorage.setItem('auth', JSON.stringify(authStore.state));

    uiStore.openModal({
      title: '닉네임 저장 완료',
      message: response.message || '닉네임이 저장되었습니다.',
      isError: false,
      onConfirmCallback: () => {
        // 성공 시 다음 페이지로 이동
        router.push('/initialSetup/page2');
      },
    });
  } catch (error) {
    console.error('API 호출 에러 전체:', error);
    console.error('응답 객체:', error.response);
    console.error('에러 상태 코드:', error.response?.status);
    console.error('에러 데이터:', error.response?.data);

    let message = '닉네임 저장 중 오류가 발생했습니다.';
    if (error.response?.status === 404) {
      message = '해당 유저를 찾을 수 없습니다.';
    } else if (error.response?.status === 500) {
      message = '서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
    }

    uiStore.openModal({
      title: '저장 실패',
      message,
      isError: true,
    });
  }
};
</script>

<template>
  <div :class="styles.initialSetup1Page">
    <ProgressHeader :current="1" :total="3" />
    <div :class="styles.initialSetup1Container">
      <div :class="styles.initialSetup1TitleBox">
        <h2>닉네임 설정</h2>
        <p>
          티끌모아청산에서 사용하실<br />
          닉네임을 입력해주세요.
        </p>
      </div>

      <div :class="styles.initialSetup1Input">
        <input
          :class="styles.initialSetup1NicknameInput"
          v-model="nickname"
          maxlength="10"
          placeholder="닉네임을 입력하세요."
        />
        <p :class="styles.initialSetup1TextLimit">
          (한글, 영문, 숫자, 밑줄(_) 포함 2~10자)
        </p>
      </div>
      <button :class="styles.initialSetup1NextButton" @click="goNext">
        다음
      </button>
    </div>
  </div>
</template>
