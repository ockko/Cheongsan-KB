<script setup>
import styles from '@/assets/styles/components/Calendar/CalendarDetailView.module.css';
import { computed } from 'vue';

const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
});

// 거래 내역을 배열로 변환
const transactionList = computed(() => {
  const list = [];
  if (props.data.transactions) {
    if (props.data.transactions.income) {
      list.push({
        type: 'income',
        amount: props.data.transactions.income,
        formatted: `+${props.data.transactions.income.toLocaleString()}`,
      });
    }
    if (props.data.transactions.expense) {
      list.push({
        type: 'expense',
        amount: props.data.transactions.expense,
        formatted: props.data.transactions.expense.toLocaleString(),
      });
    }
  }
  return list;
});
</script>

<template>
  <div :class="styles.detailContainer">
    <!-- 대출 상환 안내 -->
    <div v-if="data.loan" :class="styles.loanSection">
      <div :class="styles.sectionTitle">대출 상환 안내</div>
      <div :class="styles.loanAlert">
        <!-- <div :class="styles.alertIcon">⚠️</div> -->
        <img
          src="/images/alert-icon.png"
          alt="알림"
          style="width: 24px; height: 24px"
        />
        <div :class="styles.alertText">
          오늘은 '{{ data.loan.bankName }}/{{ data.loan.loanName }}'
          상환일입니다.
        </div>
      </div>
    </div>

    <!-- 거래 내역 -->
    <div v-if="transactionList.length > 0" :class="styles.transactionSection">
      <div :class="styles.sectionTitle">거래 내역</div>
      <div :class="styles.transactionList">
        <div
          v-for="(transaction, index) in transactionList"
          :key="index"
          :class="[styles.transactionItem, styles[transaction.type]]"
        >
          <div :class="styles.transactionDot"></div>
          <div :class="styles.transactionAmount">
            {{ transaction.formatted }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
