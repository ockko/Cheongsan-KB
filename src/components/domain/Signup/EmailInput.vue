<script setup>
import { computed, defineEmits, defineProps } from 'vue';
import styles from '@/assets/styles/components/Signup/EmailInput.module.css';

const props = defineProps({
  emailLocal: String,
  emailDomain: String,
  disabled: Boolean,
});

const emit = defineEmits([
  'update:emailLocal',
  'update:emailDomain',
  'validation-change',
]);

// 이메일 유효성 검사
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// 완전한 이메일 주소 계산
const fullEmail = computed(() => {
  if (props.emailLocal && props.emailDomain) {
    return `${props.emailLocal}@${props.emailDomain}`;
  }
  return '';
});

// 이메일 유효성 검사 결과
const isEmailValid = computed(() => {
  return fullEmail.value && emailRegex.test(fullEmail.value);
});

const updateEmailLocal = (event) => {
  emit('update:emailLocal', event.target.value);
  emitValidation();
};

const updateEmailDomain = (event) => {
  emit('update:emailDomain', event.target.value);
  emitValidation();
};

// 유효성 검사 결과를 부모에게 전달
const emitValidation = () => {
  emit('validation-change', {
    isValid: isEmailValid.value,
    fullEmail: fullEmail.value,
  });
};
</script>

<template>
  <div :class="styles.inputGroup">
    <div :class="styles.label">이메일</div>
    <div :class="styles.emailRow">
      <input
        :value="emailLocal"
        @input="updateEmailLocal"
        type="text"
        placeholder="이메일"
        :class="styles.emailInput"
        :disabled="disabled"
      />
      <span :class="styles.emailSeparator">@</span>
      <input
        :value="emailDomain"
        @input="updateEmailDomain"
        type="text"
        placeholder=""
        :class="styles.emailInput"
        :disabled="disabled"
      />
    </div>
  </div>
</template>
