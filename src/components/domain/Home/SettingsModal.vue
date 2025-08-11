<script setup>
import styles from '@/assets/styles/components/Home/SettingsModal.module.css';
import { ref, defineEmits, computed, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useBudgetStore } from '@/stores/budget';

const emit = defineEmits(['close']);

const budgetStore = useBudgetStore();
const { recommendedLimit, maximumLimit, currentLimit, isEditable } =
  storeToRefs(budgetStore);

// 슬라이더의 현재 값을 담을 로컬 변수
const sliderValue = ref(0);

onMounted(async () => {
  await budgetStore.fetchBudgetRecommendation();
  await budgetStore.fetchBudgetStatus();
  // --- 슬라이더 초기값 설정 로직 ---
  // 사용자가 직접 설정한 한도(currentLimit)가 유효한 값(0보다 큼)인지 확인
  if (currentLimit.value > 0) {
    // 유효한 값이 있으면, 그 값을 슬라이더의 초기값으로 설정
    sliderValue.value = currentLimit.value;
  } else {
    // 유효한 값이 없으면(최초 설정 등), 시스템 추천값을 초기값으로 설정
    sliderValue.value = recommendedLimit.value;
  }
});

// 슬라이더의 현재 진행률을 퍼센트로 계산
const sliderProgress = computed(() => {
  if (maximumLimit.value === 0) return 0;
  return (sliderValue.value / maximumLimit.value) * 100;
});

// 숫자에 콤마(,)를 찍어주는 헬퍼 함수
const formatCurrency = (value) => {
  if (typeof value !== 'number' && typeof value !== 'string') return '0';
  return Number(value).toLocaleString('ko-KR');
};

const applyChanges = async () => {
  // 스토어의 저장 액션 호출
  await budgetStore.saveFinalDailyLimit(sliderValue.value);
  // 성공 여부와 관계없이 모달 닫기 (스토어에서 alert로 피드백)
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
          >최대 {{ formatCurrency(maximumLimit) }}원</span
        >
      </div>
      <input
        type="range"
        min="0"
        :max="maximumLimit"
        v-model="sliderValue"
        :class="styles.slider"
        @input="updateSliderProgress"
        :style="{ '--slider-progress': sliderProgress + '%' }"
        step="500"
        :disabled="!isEditable"
      />
      <div :class="styles.currentValueBox">
        {{ formatCurrency(sliderValue) }}원
      </div>
      <p v-if="!isEditable" :class="styles.notice">
        이번 주 한도는 이미 수정했어요.
      </p>

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
          :disabled="!isEditable"
        >
          적용
        </button>
      </div>
    </div>
  </div>
</template>
