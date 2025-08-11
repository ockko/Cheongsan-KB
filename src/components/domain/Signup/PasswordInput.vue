<script setup>
import { ref, defineEmits, defineProps, watch } from 'vue';
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
  <div :class="styles.passwordSection">
    <!-- 비밀번호 입력 -->
    <div :class="styles.inputGroup">
      <label :class="styles.label">비밀번호</label>
      <input
        :value="password"
        @input="updatePassword"
        type="password"
        placeholder="비밀번호"
        :class="styles.input"
        :disabled="disabled"
      />
    </div>

    <!-- 비밀번호 확인 입력 -->
    <div :class="styles.inputGroup">
      <label :class="styles.label">비밀번호 확인</label>
      <input
        :value="passwordConfirm"
        @input="updatePasswordConfirm"
        type="password"
        placeholder="비밀번호 확인"
        :class="styles.input"
        :disabled="disabled"
      />
    </div>
  </div>
</template>
