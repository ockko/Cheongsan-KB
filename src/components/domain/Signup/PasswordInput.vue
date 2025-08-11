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

// 비밀번호 유효성 검사 정규식 (8~16자, 영문 대소문자, 숫자, 특수문자 포함)
const passwordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;

const passwordValidation = ref({
  isValid: false,
  showError: false,
});

const passwordConfirmValidation = ref({
  isValid: false,
  showError: false,
});

// 비밀번호 유효성 검사
const validatePassword = () => {
  passwordValidation.value.isValid = passwordRegex.test(props.password);
  passwordValidation.value.showError =
    props.password && !passwordValidation.value.isValid;

  // 비밀번호가 변경되면 확인 필드도 다시 검사
  if (props.passwordConfirm) {
    validatePasswordConfirm();
  }

  emitValidation();
};

// 비밀번호 확인 유효성 검사
const validatePasswordConfirm = () => {
  passwordConfirmValidation.value.isValid =
    props.passwordConfirm && props.password === props.passwordConfirm;
  passwordConfirmValidation.value.showError =
    props.passwordConfirm && !passwordConfirmValidation.value.isValid;

  emitValidation();
};

// 유효성 검사 결과를 부모에게 전달
const emitValidation = () => {
  emit('validation-change', {
    passwordValid: passwordValidation.value.isValid,
    passwordConfirmValid: passwordConfirmValidation.value.isValid,
  });
};

const updatePassword = (event) => {
  emit('update:password', event.target.value);
  validatePassword();
};

const updatePasswordConfirm = (event) => {
  emit('update:passwordConfirm', event.target.value);
  validatePasswordConfirm();
};

// props 변경 감지하여 유효성 검사 실행
watch(() => props.password, validatePassword);
watch(() => props.passwordConfirm, validatePasswordConfirm);
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
      <!-- 비밀번호 유효성 검사 오류 메시지 -->
      <div v-if="passwordValidation.showError" :class="styles.errorMessage">
        <img src="/images/red-info.png" alt="비밀번호 유효성 검사 경고" />
        8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해주세요
      </div>
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
      <!-- 비밀번호 확인 오류 메시지 -->
      <div
        v-if="passwordConfirmValidation.showError"
        :class="styles.errorMessage"
      >
        <img src="/images/red-info.png" alt="비밀번호 유효성 검사 경고" />
        비밀번호가 일치하지 않습니다.
      </div>
    </div>
  </div>
</template>
