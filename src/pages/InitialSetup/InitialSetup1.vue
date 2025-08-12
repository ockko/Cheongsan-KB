<script setup>
import styles from '@/assets/styles/pages/InitialSetup/InitialSetup1.module.css';
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { saveNickname } from '@/api/initialSetup/initialSetup1.js';

const nickname = ref('');
const router = useRouter();

const nicknameRegex = /^[가-힣a-zA-Z0-9_]{2,10}$/;

async function goNext() {
  const nicknameValue = nickname.value.trim();

  if (!nicknameRegex.test(nicknameValue)) {
    alert(
      '닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용 가능하며 2~10자 이내여야 합니다.'
    );
    return;
  }

  try {
    const response = await saveNickname(nicknameValue);

    alert(response.message || '닉네임이 저장되었습니다.');

    // 성공 시 다음 페이지로 이동
    router.push('/initialSetup/page2');
  } catch (error) {
    console.error('API 호출 에러 전체:', error);
    console.error('응답 객체:', error.response);

    if (error.response && error.response.status === 404) {
      alert('해당 유저를 찾을 수 없습니다.');
    } else {
      alert('닉네임 저장 중 오류가 발생했습니다.');
      console.error(error);
    }
  }
}
</script>

<template>
  <div :class="styles.page">
    <ProgressHeader :current="1" :total="3" />
    <div :class="styles.container">
      <div :class="styles.titleBox">
        <h2>닉네임 설정</h2>
        <p>
          티끌모아청산에서 사용하실<br />
          닉네임을 입력해주세요.
        </p>
      </div>

      <div :class="styles.input">
        <input
          :class="styles.nicknameInput"
          v-model="nickname"
          maxlength="10"
          placeholder="닉네임을 입력하세요."
        />
        <p :class="styles.textLimit">(최대 10자)</p>
      </div>
      <button :class="styles.nextButton" @click="goNext">다음</button>
    </div>
  </div>
</template>
