<script setup>
import { ref, nextTick, watchEffect  } from 'vue'
// import axios from 'axios';

defineEmits(['confirm', 'cancel'])

const props = defineProps({
  institution: String,
  name: String,
  logo: String
})

const isMultiline = ref(false)
const titleEl = ref(null)

watchEffect(async () => {
  if (!props.institution || !props.name) return
  await nextTick()
  if (titleEl.value) {
    const lineHeight = parseFloat(getComputedStyle(titleEl.value).lineHeight)
    const lines = titleEl.value.scrollHeight / lineHeight
    isMultiline.value = lines > 1
  }
})

// 입력값 상태
const repaymentDay = ref('')
const repaymentMethod = ref('원금 균등 상환')
</script>

<template>
    <div class="modal-overlay">
        <div class="modal">
            <div class="loan-header">
            <img v-if="logo" :src="logo" alt="로고" class="loan-logo" />
            <!-- 대출명/대출기관 한 줄에 작성되는 경우 -->
            <div 
              ref="titleEl" 
              class="loan-title" 
              v-if="!isMultiline"
            >
              {{ institution }} / {{ name }}
            </div>
            <!-- 대출명/대출기관이 두 줄 이상인 경우 -->
            <div 
              v-else 
              class="loan-title multiline"
            >
              {{ institution }}<br />/ {{ name }}
            </div>
          </div>

        <div class="form-row">
            <div class="dot">
                <img src="/images/dot-icon.png" alt="dot">
            </div>
            <div class="form-label">상환일</div>
            <div class="form-control repayment-control">
            매월 
            <input
                v-model="repaymentDay"
                type="number"
                min="1"
                max="31"
                class="repayment-day-input"
            />
            일
            </div>
        </div>

        <div class="form-row">
            <img src="/public/images/dot-icon.png" alt="dot">
            <label class="form-label">상환 방식</label>
            <div class="form-control">
                <select v-model="repaymentMethod" class="repayment-select">
                    <option>원금 균등 상환</option>
                    <option>원리금 균등 상환</option>
                    <option>만기 일시 상환</option>
                </select>
            </div>
        </div>

        <div class="button-group">
            <button @click="$emit('cancel')" class="cancel-btn">취소</button>
            <button @click="$emit('confirm')" class="confirm-btn">확인</button>
        </div>
      </div>
    </div>
  </template>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
  }
  
  .modal {
    background: white;
    border-radius: 18px;
    padding: 2rem 2.5rem;

    width: 80%;
    max-width: 300px;
    margin: 0 auto; 

    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  
  .loan-header {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-bottom: 1rem;
  }
  .loan-logo {
    width: 32px;
    height: 32px;
    border-radius: 8px;
  }
  .loan-title {
  font-size: 16px;
  font-weight: bold;
  line-height: 1.4;
  max-width: 200px;
  word-break: keep-all;
  white-space: normal;
  text-align: center;
}

.loan-title.multiline {
  white-space: normal;
}

.form-row {
  display: flex;
  align-items: center;
}
.form-row img {
    width: 10px;
    height: 10px;
}
.form-label {
  width: 60px;
  text-align: left;
  margin-left: 1rem;
  font-weight: bold;
  white-space: nowrap;
  margin-right: 1rem;
  font-size: 15px;
  flex-shrink: 0;
}
.form-control {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  flex: 1;
}
.repayment-row {
  justify-content: flex-start;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
}
  .repayment-day-input {
    margin-left: 6px;
    width: 60px;
    padding: 4px 6px;
    border: none;
    border-bottom: 1px solid #333;
    text-align: center;
    font-size: 15px;
    background: transparent;
  }
  .repayment-day-input:focus {
    outline: none;
    border-bottom: 2px solid #00497a;
  }
  
  .repayment-select {
    text-align: center;
    margin-top: 4px;
    width: 100%;
    padding: 6px 8px;
    font-size: 12px;
    border: 1px solid #aaa;
    border-radius: 4px;
  }
  
  /* 버튼 그룹 */
  .button-group {
    display: flex;
    justify-content: center;
    gap: 1.5rem;
    margin-top: 2rem;
  }
  .confirm-btn,
  .cancel-btn {
    width: fit-content;
    padding: 0 2rem;
    height: 40px;
    border: none;
    border-radius: 8px;
    font-weight: bold;
    font-size: 15px;
    cursor: pointer;
  }
  .confirm-btn {
    background-color: #00497a;
    color: #fff;
  }
  .cancel-btn {
    background-color: #e0e0e0;
    color: #333;
  }
  </style>