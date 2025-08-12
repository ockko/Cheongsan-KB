<script setup>
import styles from '@/assets/styles/components/home/DailySpendingWidget.module.css';
import SettingsModal from '@/components/domain/home/SettingsModal.vue';
import { ref, computed, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useSpendingStore } from '@/stores/spending';

const isModalOpen = ref(false);

const spendingStore = useSpendingStore();
const { spendingData } = storeToRefs(spendingStore);

onMounted(() => {
    spendingStore.fetchDailySpending();
});

// 지출액이 한도를 초과했는지 여부를 계산하는 computed 속성
const isOverLimit = computed(() => {
  // 한도가 0일 경우 초과로 보지 않음
  if (spendingData.value.dailyLimit === 0) {
    return false;
  }
  return spendingData.value.spent > spendingData.value.dailyLimit;
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
            >{{ formatCurrency(spendingData.dailyLimit) }}원</span
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
        :max="spendingData.dailyLimit"
      ></progress>

      <p :class="styles.remainingAmount">
        {{ formatCurrency(spendingData.remaining) }} 남음
      </p>
    </div>
  </div>

  <SettingsModal v-if="isModalOpen" @close="isModalOpen = false" />
</template>
