<script setup>
import { computed, defineEmits, defineProps } from 'vue';
import styles from '@/assets/styles/components/Signup/PasswordInput.module.css';

const props = defineProps({
  password: String,
  passwordConfirm: String,
  disabled: Boolean,
});

const emit = defineEmits([
  'update:password',
  'update:passwordConfirm',
  'validation-change',
]);

// 비밀번호 확인 에러 상태 계산
const isPasswordConfirmError = computed(() => {
  return (
    props.passwordConfirm &&
    props.password &&
    props.password !== props.passwordConfirm
  );
});

const updatePassword = (event) => {
  const newPassword = event.target.value;
  emit('update:password', newPassword);

  // 새로운 값을 기준으로 유효성 검사
  emit('validation-change', {
    passwordValid: !!newPassword,
    passwordConfirmValid:
      !!props.passwordConfirm && newPassword === props.passwordConfirm,
  });
};

const updatePasswordConfirm = (event) => {
  const newPasswordConfirm = event.target.value;
  emit('update:passwordConfirm', newPasswordConfirm);

  // 새로운 값을 기준으로 유효성 검사
  emit('validation-change', {
    passwordValid: !!props.password,
    passwordConfirmValid:
      !!newPasswordConfirm && props.password === newPasswordConfirm,
  });
};
</script>

<template>
  <div :class="styles.passwordInputSection">
    <!-- 비밀번호 입력 -->
    <div :class="styles.passwordInputGroup">
      <div :class="styles.passwordInputLabel">비밀번호</div>
      <input
        :value="password"
        @input="updatePassword"
        type="password"
        placeholder="비밀번호"
        :class="styles.passwordInputField"
        :disabled="disabled"
      />
    </div>

    <!-- 비밀번호 확인 입력 -->
    <div :class="styles.passwordInputGroup">
      <div :class="styles.passwordInputLabel">비밀번호 확인</div>
      <input
        :value="passwordConfirm"
        @input="updatePasswordConfirm"
        type="password"
        placeholder="비밀번호 확인"
        :class="styles.passwordInputField"
        :disabled="disabled"
      />

      <!-- 비밀번호 확인 에러 메시지 -->
      <div
        v-if="isPasswordConfirmError"
        :class="styles.passwordInputErrorMessage"
      >
        <img
          src="/images/red-info.png"
          alt="경고"
          style="width: 18px; height: 18px"
        />
        비밀번호가 일치하지 않습니다.
      </div>
    </div>
  </div>
</template>
