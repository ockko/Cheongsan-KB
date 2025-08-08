<script setup>
import styles from '@/assets/styles/components/Home/LoanAddModal.module.css';
import { ref, reactive } from 'vue';
import { defineEmits } from 'vue';

const emit = defineEmits(['close', 'add-loan']);

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
  remainingAmount: '',
  gracePeriod: '',
});

const fields = [
  { label: '원금', model: 'originalAmount', unit: '원' },
  { label: '이자율', model: 'interestRate', unit: '%' },
  { label: '대출 시작일', model: 'loanStartDate', unit: '' },
  { label: '총 상환 기간', model: 'totalRepaymentPeriod', unit: '개월' },
  { label: '남은 상환액', model: 'remainingAmount', unit: '원' },
  { label: '거치 기간', model: 'gracePeriod', unit: '개월' },
];

// 닫기 함수
function closeModal() {
  emit('close');
}

// 대출 추가
function addLoan() {
  if (!debtName.value || !institutionName.value) {
    alert('대출명과 기관명을 입력해주세요.');
    return;
  }

  const newLoan = {
    name: debtName.value,
    institution: institutionName.value,
    resAccount: resAccount.value,
    repaymentMethod: repaymentMethod.value,
    originalAmount: formData.originalAmount,
    interestRate: formData.interestRate,
    loanYear: formData.loanYear,
    loanMonth: formData.loanMonth,
    loanDay: formData.loanDay,
    totalRepaymentPeriod: formData.totalRepaymentPeriod,
    remainingAmount: formData.remainingAmount,
    gracePeriod: formData.gracePeriod,
    logo: '/images/logo-blue.png',
  };
  emit('add-loan', newLoan);
  emit('close');
}
</script>

<template>
  <div :class="styles.modalOverlay" @click.self="closeModal">
    <div :class="styles.modalContainer">
      <form :class="styles.form" @submit.prevent="addLoan">
        <!-- 대출명 -->
        <div :class="styles.formRow">
          <label :class="styles.titleLabel">대출명 :</label>
          <input v-model="debtName" type="text" :class="styles.textInput" />
        </div>
        <!-- 대출 기관명 -->
        <div :class="styles.formRow">
          <label :class="styles.subLabel">대출 기관명 :</label>
          <input
            v-model="institutionName"
            type="text"
            :class="styles.textInput"
          />
        </div>
      </form>

      <!-- 상세 항목 -->
      <form :class="styles.form">
        <div>
          <div
            :class="styles.formRow"
            v-for="(field, index) in fields"
            :key="index"
          >
            <div :class="styles.dot">
              <img src="/images/dot-icon.png" alt="dot" />
            </div>
            <div :class="styles.formLabel">{{ field.label }}</div>

            <div
              :class="styles.formControl"
              v-if="field.model !== 'loanStartDate'"
            >
              <input
                v-model="formData[field.model]"
                type="number"
                :class="styles.textInput"
              />
              {{ field.unit }}
            </div>

            <div :class="styles.formControl" v-else>
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
        </div>
        <!-- 상환 방식 -->
        <div :class="styles.formRow">
          <img src="/images/dot-icon.png" alt="dot" />
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
          <button
            :class="[styles.button, styles.cancelButton]"
            @click.self="closeModal"
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
