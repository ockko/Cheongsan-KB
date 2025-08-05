<template>
  <div class="container">
    <div class="description">
      <p class="text-medium tight-line">
        <span>추가 상환 가능액을 입력하고</span><br />
        <span>AI 추천 상환 플랜을 비교해보세요</span>
      </p>
    </div>

    <img src="/images/logo-blue.png" alt="저금통" class="icon" />

    <div class="container">
      <p class="text-light label">월 추가 상환 가능액</p>
      <input
        type="text"
        :value="formattedValue"
        placeholder="없음"
        class="underline-input"
        inputmode="numeric"
        @input="onInput"
        @keydown="onKeyDown"
        ref="inputRef"
      />
    </div>
    <button class="analyze-button" @click="analyze">분석하기</button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const rawValue = ref('');

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
}
</script>

<style scoped>
.underline-input {
  border: none;
  border-bottom: 1px solid var(--color-main);
  padding: 6px 0 4px;
  font-size: 16px;
  width: 280px;
  outline: none;
}

.underline-input::placeholder {
  color: var(--color-main);
  user-select: none;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 16px;
}

.description {
  text-align: center;
  font-size: 18px;
  line-height: 1.5;
  margin-bottom: 24px;
}

.icon {
  width: 128px;
  height: 128px;
  margin-bottom: 32px;
}

.analyze-button {
  background-color: var(--color-main);
  color: white;
  font-size: 16px;
  width: 280px;
  height: 40px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 16px;
}
.tight-line {
  line-height: 1.4;
}

.label {
  align-self: flex-start;
  font-size: 14px;
  color: #555;
  margin-bottom: 4px;
}
</style>
