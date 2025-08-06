<script setup>
import styles from '@/assets/styles/components/Home/DailySpendingWidget.module.css';
import SettingsModal from '@/components/domain/Home/SettingsModal.vue';
import { ref, defineProps, computed } from 'vue';

const isModalOpen = ref(false);

// 부모 컴포넌트로부터 받을 데이터(props)를 정의합니다.
const props = defineProps({
  spendingData: {
    type: Object,
    required: true,
    // 데이터가 아직 로드되지 않았을 때를 대비한 기본값
    default: () => ({ limit: 0, spent: 0, remaining: 0 }),
  },
});

// 지출액이 한도를 초과했는지 여부를 계산하는 computed 속성
const isOverLimit = computed(() => {
  // 한도가 0일 경우 초과로 보지 않음
  if (props.spendingData.limit === 0) {
    return false;
  }
  return props.spendingData.spent > props.spendingData.limit;
});

// 숫자에 콤마(,)를 찍어주는 헬퍼 함수
const formatCurrency = (value) => {
  if (typeof value !== 'number') return '0';
  return value.toLocaleString('ko-KR');
};
</script>

<template>
  <div :class="styles.widgetCard">
    <div :class="styles.widgetHeader">
      <h3>오늘의 지출</h3>
    </div>

    <div :class="styles.widgetContent">
      <p :class="styles.spentAmount">
        {{ formatCurrency(spendingData.spent) }} 원
      </p>

      <div :class="styles.limitInfo">
        <span>일일 사용 한도 : </span>
        <div>
          <span :class="styles.limitAmount"
            >{{ formatCurrency(spendingData.limit) }}원</span
          >
          <button
            :class="styles.settingsButton"
            @click="isModalOpen = true"
            aria-label="한도 설정"
          >
            ⚙️
          </button>
        </div>
      </div>

      <progress
        :class="[styles.spendingProgress, { [styles.overLimit]: isOverLimit }]"
        :value="spendingData.spent"
        :max="spendingData.limit"
      ></progress>

      <p :class="styles.remainingAmount">
        {{ formatCurrency(spendingData.remaining) }} 남음
      </p>
    </div>
  </div>

  <SettingsModal v-if="isModalOpen" @close="isModalOpen = false" />
</template>
