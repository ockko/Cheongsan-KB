<template>
  <div :class="styles['container']">
    <div :class="styles['description']">
      <p :class="styles['text-medium tight-line']">
        <span>추가 상환 가능액을 입력하고</span><br />
        <span>AI 추천 상환 플랜을 비교해보세요</span>
      </p>
    </div>

    <img src="/images/logo-blue.png" alt="저금통" :class="styles['icon']" />

    <div :class="styles['container']">
      <p class="text-light" :class="styles['pre-label']">월 추가 상환 가능액</p>
      <input
        type="text"
        :value="formattedValue"
        placeholder="없음"
        :class="styles['underline-input']"
        inputmode="numeric"
        @input="onInput"
        @keydown="onKeyDown"
        ref="inputRef"
      />
    </div>
    <button :class="styles['analyze-button']" @click="analyze">분석하기</button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import styles from '@/assets/styles/pages/RepaymentSimulation.module.css';

const rawValue = ref('');
const router = useRouter();

const inputRef = ref(null);

const formattedValue = computed(() => {
  if (!rawValue.value) return '';
  return Number(rawValue.value.replace(/,/g, '')).toLocaleString() + '원';
});

function onInput(e) {
  const el = e.target;
  const selectionStart = el.selectionStart;
  const oldValue = el.value;

  // 콤마 제거
  let numericValue = oldValue.replace(/,/g, '');

  // 숫자가 아니면 빈 문자열로 변경
  if (!/^\d*$/.test(numericValue)) {
    numericValue = numericValue.replace(/\D/g, '');
  }

  rawValue.value = numericValue;

  // 포맷된 값
  const newFormatted = formattedValue.value;

  // 커서 위치 계산
  // (콤마 전과 후의 차이를 계산해서 커서 보정)
  const oldLeft = oldValue.slice(0, selectionStart);
  const oldLeftCount = (oldLeft.match(/,/g) || []).length;

  const newLeftCount = (newFormatted.slice(0, selectionStart).match(/,/g) || [])
    .length;
  const diff = newLeftCount - oldLeftCount;

  // 값 강제로 바꾸고 커서 위치 조정
  el.value = newFormatted;
  const newPos = selectionStart + diff;
  setTimeout(() => {
    el.setSelectionRange(newPos, newPos);
  }, 0);
}

function onKeyDown(e) {
  // 숫자, 백스페이스, 방향키 등만 허용
  const allowedKeys = [
    'Backspace',
    'Delete',
    'ArrowLeft',
    'ArrowRight',
    'Tab',
    'Home',
    'End',
  ];
  if (
    allowedKeys.includes(e.key) ||
    (/^\d$/.test(e.key) && !e.ctrlKey && !e.metaKey && !e.altKey)
  ) {
    return;
  }
  e.preventDefault();
}

// 분석하기 버튼 클릭 (임의 로직)
function analyze() {
  alert(
    rawValue.value === ''
      ? '추가 상환 가능액: 없음'
      : `추가 상환 가능액: ${Number(rawValue.value).toLocaleString()}원`
  );
  router.push({
    path: '/result',
    query: {
      monthlyAvailableAmount: rawValue.value || 0,
    },
  });
}
</script>
