<script setup>
import { ref } from 'vue';
import styles from '@/assets/styles/components/LoanSimulation.module.css';
import { useRouter } from 'vue-router';
import { loanApi } from '@/api/loanApi';
import { useUiStore } from '@/stores/ui';
import { use } from 'echarts';

const principal = ref('');
const interestRate = ref('');
const loanPeriod = ref('');
const income = ref('');
const repaymentMethod = ref('원리금균등분할방식');
const router = useRouter();
const uiStore = useUiStore();

const formatNumber = (value) =>
  !value ? '' : value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
const handleNumberInput = (value, setter) =>
  setter(value.replace(/[^0-9]/g, ''));
const validate = () => {
  if (
    !principal.value ||
    !interestRate.value ||
    !loanPeriod.value ||
    !income.value
  ) {
    uiStore.openModal({
      title: '입력 오류',
      message: '모든 항목을 입력해주세요.',
      isError: true,
    });
    return false;
  }
  return true;
};

const mapRepaymentType = (method) => {
  switch (method) {
    case '원리금균등분할방식':
      return 'EQUAL_PAYMENT';
    case '원금균등분할방식':
      return 'EQUAL_PRINCIPAL';
    case '만기일시상환방식':
      return 'LUMP_SUM';
    default:
      return 'EQUAL_PAYMENT';
  }
};

const simulateLoan = async () => {
  if (!validate()) return;

  try {
    const requestData = {
      loanAmount: Number(principal.value),
      interestRate: parseFloat(interestRate.value),
      loanPeriod: Number(loanPeriod.value),
      annualIncome: Number(income.value),
      repaymentType: mapRepaymentType(repaymentMethod.value),
    };

    const data = await loanApi.analyzeLoan(requestData);

    if (!data || Object.keys(data).length === 0) {
      uiStore.openModal({
        title: '오류',
        message: '응답이 비어 있습니다.',
        isError: true,
      });
      return;
    }

    sessionStorage.setItem('analysisData', JSON.stringify(data));
    await router.push({ path: '/simulation/loan', state: { result: data } });
  } catch (e) {
    console.error('시뮬레이션 요청 실패:', e);

    const data = e.response?.data;
    const code = data?.code ?? data?.errorCode ?? data?.error?.code;
    const message =
      data?.message ?? data?.errorMessage ?? data?.error?.message ?? '';

    if (
      code === 'DSR_EXCEEDED' ||
      message.includes('DSR') ||
      message.includes('40%')
    ) {
      uiStore.openModal({
        title: 'DSR 초과',
        message: '고객님의 DSR이 40%가 넘었습니다. 값을 재조정 해주세요.',
        isError: true,
      });
      return;
    }

    uiStore.openModal({
      title: '오류',
      message: '시뮬레이션 중 오류가 발생했습니다.',
      isError: true,
    });
  }
};
</script>

<template>
  <div :class="styles.loanFormContainer">
    <div :class="styles.formDescription">
      <h2>새로운 대출 상품을 입력하고<br />영향 분석 결과를 확인해보세요</h2>
    </div>

    <div :class="styles.loanForm">
      <div :class="styles.formGroup">
        <label for="principal">원금</label>
        <div :class="styles.inputWrapper">
          <input
            type="text"
            id="principal"
            :value="formatNumber(principal)"
            @input="
              handleNumberInput($event.target.value, (v) => (principal = v))
            "
            placeholder="0"
            required
          />
          <span :class="styles.currency">원</span>
        </div>
      </div>

      <div :class="styles.formRow">
        <div :class="[styles.formGroup, styles.half]">
          <label for="loanPeriod">총 대출 기간</label>
          <div :class="styles.inputWrapper">
            <input
              type="text"
              id="loanPeriod"
              :value="formatNumber(loanPeriod)"
              @input="
                handleNumberInput($event.target.value, (v) => (loanPeriod = v))
              "
              placeholder="0"
              required
            />
            <span :class="styles.currency">년</span>
          </div>
        </div>
        <div :class="[styles.formGroup, styles.half]">
          <label for="interestRate">금리</label>
          <div :class="styles.inputWrapper">
            <input
              type="number"
              step="0.1"
              id="interestRate"
              v-model="interestRate"
              placeholder="0.0"
              required
            />
            <span :class="styles.currency">%</span>
          </div>
        </div>
      </div>

      <div :class="styles.formGroup">
        <label for="income">연 소득</label>
        <div :class="styles.inputWrapper">
          <input
            type="text"
            id="income"
            :value="formatNumber(income)"
            @input="handleNumberInput($event.target.value, (v) => (income = v))"
            placeholder="0"
            required
          />
          <span :class="styles.currency">원</span>
        </div>
      </div>

      <div :class="styles.formGroup">
        <label for="repaymentMethod">상환 방식</label>
        <select id="repaymentMethod" v-model="repaymentMethod" required>
          <option value="원리금균등분할방식">원리금균등분할방식</option>
          <option value="원금균등분할방식">원금균등분할방식</option>
          <option value="만기일시상환방식">만기일시상환방식</option>
        </select>
      </div>

      <button type="button" @click="simulateLoan" :class="styles.submitButton">
        분석하기
      </button>
    </div>
  </div>
</template>
