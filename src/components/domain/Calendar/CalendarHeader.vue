<script setup>
import { computed, ref, watch } from 'vue';
import styles from '@/assets/styles/components/Calendar/CalendarHeader.module.css';

const props = defineProps({
  year: {
    type: Number,
    required: true,
  },
  month: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(['change-month']);

// 애니메이션 상태 관리
const isAnimating = ref(false);
const animationDirection = ref(''); // 'next' 또는 'prev'

// 월 이름 배열
const monthNames = [
  '1월',
  '2월',
  '3월',
  '4월',
  '5월',
  '6월',
  '7월',
  '8월',
  '9월',
  '10월',
  '11월',
  '12월',
];

const monthName = computed(() => monthNames[props.month]);

// 애니메이션이 시작될 때의 키 값 (Vue transition을 위해)
const monthKey = computed(() => `${props.year}-${props.month}`);

const previousMonth = () => {
  if (isAnimating.value) return; // 애니메이션 중이면 무시

  animationDirection.value = 'prev';
  isAnimating.value = true;
  emit('change-month', -1);
};

const nextMonth = () => {
  if (isAnimating.value) return; // 애니메이션 중이면 무시

  animationDirection.value = 'next';
  isAnimating.value = true;
  emit('change-month', 1);
};

// 애니메이션 완료 핸들러
const onAnimationComplete = () => {
  isAnimating.value = false;
  animationDirection.value = '';
};

// month prop이 변경되면 애니메이션이 완료되었다고 간주
watch(
  () => props.month,
  () => {
    // 약간의 지연 후 애니메이션 상태 리셋
    setTimeout(() => {
      onAnimationComplete();
    }, 300); // CSS transition 시간과 맞춤
  }
);
</script>

<template>
  <div :class="styles.header">
    <div class="text-light" style="color: var(--color-main)">{{ year }}년</div>

    <div :class="styles.monthNavigation">
      <button
        :class="styles.navButton"
        @click="previousMonth"
        type="button"
        :disabled="isAnimating"
      >
        <img src="/images/left-arrow.png" alt="이전 달" :class="styles.arrow" />
      </button>

      <div :class="styles.monthDisplayContainer">
        <transition
          :name="animationDirection === 'next' ? 'slide-left' : 'slide-right'"
          mode="out-in"
        >
          <div :key="monthKey" :class="styles.monthDisplay">
            {{ monthName }}
          </div>
        </transition>
      </div>

      <button
        :class="styles.navButton"
        @click="nextMonth"
        type="button"
        :disabled="isAnimating"
      >
        <img
          src="/images/right-arrow.png"
          alt="다음 달"
          :class="styles.arrow"
        />
      </button>
    </div>
  </div>
</template>
