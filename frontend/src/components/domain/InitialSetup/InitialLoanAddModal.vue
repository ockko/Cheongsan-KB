<script setup>
import styles from '@/assets/styles/components/InitialSetup/initialLoanModal.module.css';
import { ref, reactive, computed, watch } from 'vue';
import { defineEmits, defineProps } from 'vue';
import { registerDebt } from '@/api/dashboard-bottomApi.js';
import { useUiStore } from '@/stores/ui';

const emit = defineEmits(['close', 'add-loan', 'refresh-debt-list']);
const props = defineProps({
  visible: Boolean,
  debtName: String,
  institutionName: String,
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

// 이자율 유효값 검증
const isInterestRateValid = computed(() => {
  const rate = parseFloat(formData.interestRate);
  return !isNaN(rate) && rate >= 0 && rate <= 100;
});

// 모든 입력값이 채워졌는지 여부
const isFormComplete = computed(() => {
  return (
    debtName.value.trim() &&
    institutionName.value.trim() &&
    resAccount.value.trim() &&
    repaymentMethod.value.trim() &&
    formData.originalAmount !== '' &&
    formData.interestRate !== '' &&
    isInterestRateValid.value &&
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
const closeModal = () => {
  resetForm();
  emit('close');
};

const addLoan = async () => {
  const uiStore = useUiStore();

  if (!isFormComplete.value) {
    uiStore.openModal({
      title: '입력 오류',
      message: '모든 항목을 입력해주세요.',
      isError: true,
    });
    return;
  }

  // 이자율 유효값 추가 검증
  if (!isInterestRateValid.value) {
    uiStore.openModal({
      title: '입력 오류',
      message: '이자율은 0% ~ 100% 사이의 값이어야 합니다.',
      isError: true,
    });
    return;
  }

  // 숫자 필드 검증
  const originalAmount = parseFloat(formData.originalAmount);
  const totalRepaymentPeriod = parseInt(formData.totalRepaymentPeriod);
  const nextPaymentDay = parseInt(formData.nextPaymentDay);
  const remainingAmount = parseFloat(formData.remainingAmount);
  const gracePeriod = parseInt(formData.gracePeriod);

  if (
    isNaN(originalAmount) ||
    isNaN(totalRepaymentPeriod) ||
    isNaN(nextPaymentDay) ||
    isNaN(remainingAmount) ||
    isNaN(gracePeriod)
  ) {
    uiStore.openModal({
      title: '입력 오류',
      message: '숫자 필드에 유효하지 않은 값이 포함되어 있습니다.',
      isError: true,
    });
    return;
  }

  // 날짜 필드 검증
  if (
    !formData.loanYear.trim() ||
    !formData.loanMonth.trim() ||
    !formData.loanDay.trim()
  ) {
    uiStore.openModal({
      title: '입력 오류',
      message: '대출 시작일을 올바르게 입력해주세요.',
      isError: true,
    });
    return;
  }

  try {
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

    // 백엔드 서버에 대출 상품 등록 요청
    const result = await registerDebt(newLoan);

    // 성공 시 모달 표시 및 부모 컴포넌트에 알림
    uiStore.openModal({
      title: '등록 완료',
      message: result.message,
      isError: false,
    });

    emit('add-loan', newLoan);
    emit('refresh-debt-list'); // 대출 목록 새로고침 신호
    closeModal();
  } catch (error) {
    // 에러 발생 시 사용자에게 알림
    uiStore.openModal({
      title: '등록 실패',
      message: error.message || '대출 상품 등록 중 오류가 발생했습니다.',
      isError: true,
    });
    console.error('대출 상품 등록 실패:', error);
  }
};
</script>

<template>
  <div
    v-if="visible"
    :class="styles.initialLoanModalOverlay"
    @click.self="closeModal"
  >
    <div :class="styles.initialLoanModalContainer">
      <form :class="styles.initialLoanModalForm" @submit.prevent="addLoan">
        <!-- 기본 정보 -->
        <div :class="styles.initialLoanModalFormRow">
          <div :class="styles.initialLoanModalFormLabelContainer">
            <div :class="styles.initialLoanModalDot">
              <img
                src="/images/dot-icon.png"
                alt="dot"
                :class="styles.initialLoanModalDotIcon"
              />
            </div>
            <div :class="styles.initialLoanModalFormLabel">대출 상품명</div>
          </div>
          <div :class="styles.initialLoanModalFormControlUp">
            <input
              v-model="debtName"
              type="text"
              :class="styles.initialLoanModalTextInput"
            />
          </div>
        </div>
        <div :class="styles.initialLoanModalFormRow">
          <div :class="styles.initialLoanModalFormLabelContainer">
            <div :class="styles.initialLoanModalDot">
              <img
                src="/images/dot-icon.png"
                alt="dot"
                :class="styles.initialLoanModalDotIcon"
              />
            </div>
            <div :class="styles.initialLoanModalFormLabel">대출 기관명</div>
          </div>
          <div :class="styles.initialLoanModalFormControlUp">
            <input
              v-model="institutionName"
              type="text"
              :class="styles.initialLoanModalTextInput"
            />
          </div>
        </div>
        <div :class="styles.initialLoanModalFormRow">
          <div :class="styles.initialLoanModalFormLabelContainer">
            <div :class="styles.initialLoanModalDot">
              <img
                src="/images/dot-icon.png"
                alt="dot"
                :class="styles.initialLoanModalDotIcon"
              />
            </div>
            <div :class="styles.initialLoanModalFormLabel">계좌번호</div>
          </div>
          <div :class="styles.initialLoanModalFormControlUp">
            <input
              v-model="resAccount"
              type="text"
              :class="styles.initialLoanModalTextInput"
            />
          </div>
        </div>

        <!-- 상세 항목 -->
        <div :class="styles.initialLoanModalDetailInfo">
          <div
            v-for="(field, index) in fields"
            :key="index"
            :class="styles.initialLoanModalFormRow"
          >
            <div :class="[styles.initialLoanModalFormLabelContainer]">
              <div :class="styles.initialLoanModalDot">
                <img
                  src="/images/dot-icon.png"
                  alt="dot"
                  :class="styles.initialLoanModalDotIcon"
                />
              </div>
              <div :class="styles.initialLoanModalFormLabel">
                {{ field.label }}
              </div>
            </div>

            <div
              v-if="field.model !== 'loanStartDate'"
              :class="styles.initialLoanModalFormControl"
            >
              <input
                v-model="formData[field.model]"
                type="number"
                :class="styles.initialLoanModalTextInput"
                :min="field.model === 'interestRate' ? 0 : undefined"
                :max="field.model === 'interestRate' ? 100 : undefined"
                :step="field.model === 'interestRate' ? 0.01 : undefined"
              />
              <span :class="styles.initialLoanModalUnit">{{ field.unit }}</span>
            </div>

            <div v-else :class="styles.initialLoanModalFormControl">
              <input
                v-model="formData.loanYear"
                type="text"
                :class="styles.initialLoanModalYearInput"
              />
              <span :class="styles.initialLoanModalDateUnit">년</span>
              <input
                v-model="formData.loanMonth"
                type="text"
                :class="styles.initialLoanModalDateInput"
              />
              <span :class="styles.initialLoanModalDateUnit">월</span>
              <input
                v-model="formData.loanDay"
                type="text"
                :class="styles.initialLoanModalDateInput"
              />
              <span :class="styles.initialLoanModalDateUnit">일</span>
            </div>
          </div>

          <div :class="styles.initialLoanModalFormRow">
            <div :class="[styles.initialLoanModalFormLabelContainer]">
              <div :class="styles.initialLoanModalDot">
                <img
                  src="/images/dot-icon.png"
                  alt="dot"
                  :class="styles.initialLoanModalDotIcon"
                />
              </div>
              <div :class="styles.initialLoanModalFormLabel">상환 방식</div>
            </div>
            <div :class="styles.initialLoanModalFormControl">
              <select
                v-model="repaymentMethod"
                :class="[styles.initialLoanModalRepaymentSelect]"
              >
                <option disabled value="">선택</option>
                <option>원금균등상환</option>
                <option>원리금균등상환</option>
                <option>만기일시상환</option>
                <option>수시상환</option>
              </select>
            </div>
          </div>
        </div>

        <!-- 제출 버튼 -->
        <div :class="styles.initialLoanModalButtonGroup">
          <button
            type="button"
            :class="[
              styles.initialLoanModalButton,
              styles.initialLoanModalCancelButton,
            ]"
            @click="closeModal"
          >
            취소
          </button>
          <button
            type="submit"
            :class="[
              styles.initialLoanModalButton,
              styles.initialLoanModalSubmitButton,
            ]"
          >
            추가하기
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
