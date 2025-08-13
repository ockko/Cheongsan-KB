<script setup>
import styles from '@/assets/styles/components/Calendar/CalendarDetailView.module.css';
import { computed } from 'vue';

const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
});

// 금액 포맷팅 함수
const formatAmount = (amount, isIncome = false) => {
  const formattedNumber = Math.abs(amount).toLocaleString();
  return isIncome ? `+${formattedNumber}` : `-${formattedNumber}`;
};

// API 응답에 맞는 거래 내역 처리
const transactionList = computed(() => {
  const list = [];
  if (props.data.transactions && props.data.transactions.length > 0) {
    props.data.transactions.forEach((transaction) => {
      // 수입/지출 판단 로직 수정
      const isIncome =
        transaction.income === true ||
        (transaction.type && transaction.type === 'TRANSFER');

      list.push({
        type: isIncome ? 'income' : 'expense',
        amount: Math.abs(transaction.amount),
        formatted: formatAmount(transaction.amount, isIncome),
        accountNumber: transaction.accountNumber,
        resAccountDesc3: transaction.resAccountDesc3 || '', // resAccountDesc3 추가
      });
    });
  }
  return list;
});

// API 응답에 맞는 대출 정보 처리
const loanList = computed(() => {
  if (props.data.loans && props.data.loans.length > 0) {
    return props.data.loans.map((loan) => ({
      debtId: loan.debtId,
      debtName: loan.debtName,
      organizationName: loan.organizationName,
    }));
  }
  return [];
});

// 대출 이름들을 문자열로 조합하는 computed
const loanNamesText = computed(() => {
  if (loanList.value.length === 0) return '';

  return loanList.value
    .map((loan) => `'${loan.organizationName}/${loan.debtName}'`)
    .join(', ');
});
</script>

<template>
  <div :class="styles.detailContainer">
    <div v-if="loanList.length > 0" :class="styles.loanSection">
      <div :class="styles.sectionTitle">대출 상환 안내</div>
      <div :class="styles.loanAlert">
        <img
          src="/images/alert-icon.png"
          alt="알림"
          style="width: 24px; height: 24px"
        />
        <div :class="styles.alertText">
          오늘은 {{ loanNamesText }} 상환일입니다.
        </div>
      </div>
    </div>

    <div v-if="transactionList.length > 0" :class="styles.transactionSection">
      <div :class="styles.sectionTitle">거래 내역</div>
      <div :class="styles.transactionList">
        <div
          v-for="(transaction, index) in transactionList"
          :key="index"
          :class="[styles.transactionItem, styles[transaction.type]]"
        >
          <div :class="styles.transactionDot"></div>
          <div :class="styles.transactionAmount" class="text-medium">
            {{ transaction.formatted }}
          </div>
          <div v-if="transaction.resAccountDesc3" :class="styles.transactionDesc">
            {{ transaction.resAccountDesc3 }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
