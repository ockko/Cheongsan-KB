<script setup>
import styles from '@/assets/styles/components/Home/SettingsModal.module.css';
import { ref, defineEmits, computed } from 'vue';

const emit = defineEmits(['close']); // 'close' 이벤트를 부모에게 보낼 것을 정의

// 부모로부터 최대 한도값을 받아옴 (실제로는 API 호출 결과)
const maximumDailyLimit = ref(50000);
const currentDailyLimit = ref(40000);

// 슬라이더의 현재 진행률을 퍼센트로 계산하는 computed 속성
const sliderProgress = computed(() => {
  if (maximumDailyLimit.value === 0) return 0;
  return (currentDailyLimit.value / maximumDailyLimit.value) * 100;
});

// 슬라이더 값을 업데이트하는 함수 (v-model만으로도 충분하지만, 명시적으로 둘 수 있음)
const updateSliderProgress = (event) => {
  currentDailyLimit.value = event.target.value;
};

// 숫자에 콤마(,)를 찍어주는 헬퍼 함수
const formatCurrency = (value) => {
  if (typeof value !== 'number' && typeof value !== 'string') return '0';
  return Number(value).toLocaleString('ko-KR');
};

const applyChanges = () => {
  // TODO: 여기에 '적용' 버튼 클릭 시 실행될 API 호출 로직을 구현합니다.
  console.log('적용된 한도:', currentDailyLimit.value);

  // API 호출 성공 후 모달을 닫습니다.
  emit('close');
};
</script>

<template>
  <div :class="styles.modalOverlay" @click.self="$emit('close')">
    <div :class="styles.modalContent">
      <h3 :class="styles.modalTitle">일일 소비 한도 설정</h3>
      <p :class="styles.modalDescription">
        건강한 소비 습관을 위한 첫걸음,<br />
        나만의 일일 한도를 설정해 보세요.
      </p>

      <div :class="styles.limitLabels">
        <span>0원</span>
        <span :class="styles.maxLimit"
          >{{ formatCurrency(maximumDailyLimit) }}원</span
        >
      </div>
      <input
        type="range"
        min="0"
        :max="maximumDailyLimit"
        v-model="currentDailyLimit"
        :class="styles.slider"
        @input="updateSliderProgress"
        :style="{ '--slider-progress': sliderProgress + '%' }"
        step="500"
      />
      <div :class="styles.currentValueBox">
        {{ formatCurrency(currentDailyLimit) }}원
      </div>

      <div :class="styles.buttonGroup">
        <button
          :class="[styles.button, styles.cancelButton]"
          @click="$emit('close')"
        >
          취소
        </button>
        <button
          :class="[styles.button, styles.applyButton]"
          @click="applyChanges"
        >
          적용
        </button>
      </div>
    </div>
  </div>
</template>
