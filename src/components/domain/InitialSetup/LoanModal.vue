<script setup>
import styles from '@/assets/styles/components/InitialSetup/LoanModal.module.css'
import { ref, nextTick, watchEffect, computed  } from 'vue'

const emit = defineEmits(['confirm', 'cancel'])

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

const repaymentDay = ref('')
const repaymentMethod = ref('원금 균등 상환')

// 모든 항목 입력 완료했는지 확인
const isFormValid = computed(() => {
  const day = Number(repaymentDay.value)
  return (
    !isNaN(day) &&
    day >= 1 &&
    day <= 31 &&
    repaymentMethod.value.trim() !== ''
  )
})

// 확인 버튼 클릭 시 유효하지 않으면 알림
const handleConfirm = () => {
  if (!isFormValid.value) {
    alert('상환일과 상환 방식을 모두 입력해 주세요.')
    return
  }
  emit('confirm')
}
</script>

<template>
    <div :class="styles.modalOverlay">
        <div :class="styles.modal">
            <div :class="styles.loanHeader">
            <img v-if="logo" :src="logo" alt="로고" :class="styles.loanLogo" />
            <!-- 대출명/대출기관 한 줄에 작성되는 경우 -->
            <div 
              ref="titleEl" 
              :class="styles.loanTitle" 
              v-if="!isMultiline"
            >
              {{ institution }} / {{ name }}
            </div>
            <!-- 대출명/대출기관이 두 줄 이상인 경우 -->
            <div 
              v-else 
              :class="styles.loanTitle"
            >
              {{ institution }}<br />/ {{ name }}
            </div>
          </div>

        <div :class="styles.formRow">
            <div :class="dot">
                <img src="/images/dot-icon.png" alt="dot">
            </div>
            <div :class="styles.formLabel">상환일</div>
            <div :class="styles.formControl">
            매월 
            <input
                v-model="repaymentDay"
                type="number"
                min="1"
                max="31"
                :class="styles.repaymentDayInput"
            />
            일
            </div>
        </div>

        <div :class="styles.formRow">
            <img src="/images/dot-icon.png" alt="dot">
            <div :class="styles.formLabel">상환 방식</div>
            <div :class="styles.formControl">
                <select v-model="repaymentMethod" :class="styles.repaymentSelect">
                    <option>원금 균등 상환</option>
                    <option>원리금 균등 상환</option>
                    <option>만기 일시 상환</option>
                </select>
            </div>
        </div>

        <div :class="styles.buttonGroup">
            <button @click="$emit('cancel')" :class="styles.cancelBtn">취소</button>
            <button 
              @click="handleConfirm" 
              :class="styles.confirmBtn"
              :disabled="!isFormValid"
            >
              확인
            </button>
        </div>
      </div>
    </div>
</template>
