<script setup>
import styles from '@/assets/styles/pages/InitialSetup/InitialSetup1.module.css';
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const nickname = ref('');
const router = useRouter();

function goNext() {
  const nicknameValue = nickname.value.trim();

  const nicknameRegex = /^[가-힣a-zA-Z0-9_]{2,10}$/;

  if (!nicknameRegex.test(nicknameValue)) {
    alert(
      '닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용 가능하며 2~10자 이내여야 합니다.'
    );
    return;
  }

  // 유효성 통과 시 다음 페이지로 이동
  router.push('/initialSetup/page2');
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
