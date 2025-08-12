<script setup>
import styles from '@/assets/styles/components/home/LoanAddModal.module.css';
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { defineEmits, defineProps } from 'vue';

const emit = defineEmits(['close', 'add-loan']);
const props = defineProps({
  visible: Boolean,
});

const debtName = ref('');
const institutionName = ref('');
const resAccount = ref('');
const repaymentMethod = ref('');
const formData = reactive({
  originalAmount: '',
  interestRate: '',
  loanYear: '',
  loanMonth: '',
  loanDay: '',
  totalRepaymentPeriod: '',
  nextPaymentDay: '',
  remainingAmount: '',
  gracePeriod: '',
});

const fields = [
  { label: '원금', model: 'originalAmount', unit: '원' },
  { label: '이자율', model: 'interestRate', unit: '%' },
  { label: '대출 시작일', model: 'loanStartDate', unit: '' },
  { label: '총 상환 기간', model: 'totalRepaymentPeriod', unit: '개월' },
  { label: '상환일', model: 'nextPaymentDay', unit: '일' },
  { label: '남은 상환액', model: 'remainingAmount', unit: '원' },
  { label: '거치 기간', model: 'gracePeriod', unit: '개월' },
];

// 모든 입력값이 채워졌는지 여부
const isFormComplete = computed(() => {
  return (
    debtName.value.trim() &&
    institutionName.value.trim() &&
    repaymentMethod.value.trim() &&
    formData.originalAmount !== '' &&
    formData.interestRate !== '' &&
    formData.loanYear.trim() &&
    formData.loanMonth.trim() &&
    formData.loanDay.trim() &&
    formData.totalRepaymentPeriod !== '' &&
    formData.nextPaymentDay !== '' &&
    formData.remainingAmount !== '' &&
    formData.gracePeriod !== ''
  );
});

// 폼 초기화 함수
function resetForm() {
  debtName.value = '';
  institutionName.value = '';
  resAccount.value = '';
  repaymentMethod.value = '';
  formData.originalAmount = '';
  formData.interestRate = '';
  formData.loanYear = '';
  formData.loanMonth = '';
  formData.loanDay = '';
  formData.totalRepaymentPeriod = '';
  formData.nextPaymentDay = '';
  formData.remainingAmount = '';
  formData.gracePeriod = '';
}

// 모달이 열릴 때마다 폼 초기화
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) resetForm();
  }
);

// 닫기 함수
function closeModal() {
  resetForm();
  emit('close');
}

function addLoan() {
  if (!isFormComplete.value) {
    alert('모든 항목을 입력해주세요.');
    return;
  }
  const newLoan = {
    name: debtName.value.trim(),
    institution: institutionName.value.trim(),
    resAccount: resAccount.value.trim(),
    repaymentMethod: repaymentMethod.value.trim(),
    originalAmount: formData.originalAmount,
    interestRate: formData.interestRate,
    loanYear: formData.loanYear.trim(),
    loanMonth: formData.loanMonth.trim(),
    loanDay: formData.loanDay.trim(),
    totalRepaymentPeriod: formData.totalRepaymentPeriod,
    nextPaymentDay: formData.nextPaymentDay,
    remainingAmount: formData.remainingAmount,
    gracePeriod: formData.gracePeriod,
    logo: '/images/logo-blue.png',
    selected: true,
  };

  emit('add-loan', newLoan);
  closeModal();
}
</script>

<template>
  <div v-if="visible" :class="styles.modalOverlay" @click.self="closeModal">
    <div :class="styles.modalContainer">
      <form :class="styles.form" @submit.prevent="addLoan">
        <!-- 기본 정보 -->
        <div :class="styles.formRow">
          <label :class="styles.titleLabel">대출명 :</label>
          <div :class="styles.formControl">
            <input v-model="debtName" type="text" :class="styles.textInput" />
          </div>
        </div>
        <div :class="styles.formRow">
          <label :class="styles.subLabel">대출 기관명 :</label>
          <div :class="styles.formControl">
            <input
              v-model="institutionName"
              type="text"
              :class="styles.textInput"
            />
          </div>
        </div>

        <!-- 상세 항목 -->
        <div :class="styles.detailInfo">
          <div
            v-for="(field, index) in fields"
            :key="index"
            :class="styles.formRow"
          >
            <div :class="styles.formLabelContainer">
              <div :class="styles.dot">
                <img src="/images/dot-icon.png" alt="dot" />
              </div>
              <div :class="styles.formLabel">{{ field.label }}</div>
            </div>

            <div
              v-if="field.model !== 'loanStartDate'"
              :class="styles.formControl"
            >
              <input
                v-model="formData[field.model]"
                type="number"
                :class="styles.textInput"
              />
              {{ field.unit }}
            </div>

            <div v-else :class="styles.formControl">
              <input
                v-model="formData.loanYear"
                type="text"
                :class="styles.yearInput"
              />
              년
              <input
                v-model="formData.loanMonth"
                type="text"
                :class="styles.dateInput"
              />
              월
              <input
                v-model="formData.loanDay"
                type="text"
                :class="styles.dateInput"
              />
              일
            </div>
          </div>

          <div :class="styles.formRow">
            <div :class="styles.formLabelContainer">
              <img src="/images/dot-icon.png" alt="dot" />
              <div :class="styles.formLabel">상환 방식</div>
            </div>
            <div :class="styles.formControl">
              <select v-model="repaymentMethod" :class="styles.repaymentSelect">
                <option disabled value="">선택</option>
                <option>원금 균등 상환</option>
                <option>원리금 균등 상환</option>
                <option>만기 일시 상환</option>
              </select>
            </div>
          </div>
        </div>

        <!-- 제출 버튼 -->
        <div :class="styles.buttonGroup">
          <button
            type="button"
            :class="[styles.button, styles.cancelButton]"
            @click="closeModal"
          >
            취소
          </button>
          <button type="submit" :class="[styles.button, styles.submitBtn]">
            추가하기
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
