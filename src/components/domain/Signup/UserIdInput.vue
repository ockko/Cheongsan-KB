<script setup>
import { ref, defineEmits, defineProps } from 'vue';
import axios from 'axios';
import { getApiBaseUrl } from '@/config/url';
import styles from '@/assets/styles/components/Signup/UserIdInput.module.css';

const props = defineProps({
  modelValue: String,
  disabled: Boolean,
});

const emit = defineEmits(['update:modelValue', 'validation-change']);

const isChecking = ref(false);
const isChecked = ref(false);
const isAvailable = ref(false);

const updateValue = (event) => {
  const value = event.target.value;
  emit('update:modelValue', value);

  // 아이디가 변경되면 중복확인 상태 초기화
  isChecked.value = false;
  isAvailable.value = false;
  emit('validation-change', { isChecked: false, isAvailable: false });
};

const checkUserId = async () => {
  if (!props.modelValue) {
    alert('아이디를 입력해주세요.');
    return;
  }

  isChecking.value = true;

  try {
    const response = await axios.get(
      `${getApiBaseUrl()}/cheongsan/auth/checkUserId/${props.modelValue}`
    );

    // API 응답이 true면 중복, false면 사용 가능
    const isDuplicate = response.data;

    if (isDuplicate) {
      alert('이미 사용 중인 아이디입니다.');
      isAvailable.value = false;
    } else {
      alert('사용 가능한 아이디입니다.');
      isAvailable.value = true;
    }

    isChecked.value = true;
    emit('validation-change', {
      isChecked: true,
      isAvailable: isAvailable.value,
    });
  } catch (error) {
    console.error('아이디 중복 확인 실패:', error);
    alert('아이디 중복 확인에 실패했습니다. 다시 시도해주세요.');
  } finally {
    isChecking.value = false;
  }
};
</script>

<template>
  <div :class="styles.inputGroup">
    <div :class="styles.label">아이디</div>
    <div :class="styles.inputRow">
      <input
        :value="modelValue"
        @input="updateValue"
        type="text"
        placeholder="아이디"
        :class="styles.input"
        :disabled="disabled"
      />
      <button
        type="button"
        @click="checkUserId"
        :class="styles.checkButton"
        :disabled="isChecking || disabled || !modelValue"
      >
        {{ isChecking ? '확인 중...' : '중복확인' }}
      </button>
    </div>
  </div>
</template>
