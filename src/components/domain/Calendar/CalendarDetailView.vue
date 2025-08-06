<script setup>
import styles from '@/assets/styles/components/Calendar/CalendarDetailView.module.css';
import {computed} from 'vue';

const props = defineProps({
    data: {
        type: Object,
        required: true,
    },
});

// API 응답에 맞는 거래 내역 처리
const transactionList = computed(() => {
    const list = [];

    if (props.data.transactions && props.data.transactions.length > 0) {
        props.data.transactions.forEach((transaction) => {
            list.push({
                type: transaction.income ? 'income' : 'expense',
                amount: Math.abs(transaction.amount),
                formatted:
                    transaction.formattedAmount ||
                    (transaction.income
                        ? `+${transaction.amount.toLocaleString()}`
                        : transaction.amount.toLocaleString()),
                accountNumber: transaction.accountNumber,
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
</script>

<template>
    <div :class="styles.detailContainer">
        <!-- 대출 상환 안내 -->
        <div v-if="loanList.length > 0" :class="styles.loanSection">
            <div :class="styles.sectionTitle">대출 상환 안내</div>
            <div v-for="loan in loanList" :key="loan.debtId" :class="styles.loanAlert">
                <img src="/images/alert-icon.png" alt="알림" style="width: 24px; height: 24px" />
                <div :class="styles.alertText">
                    오늘은 '{{ loan.organizationName }}/{{ loan.debtName }}' 상환일입니다.
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
                    <div :class="styles.transactionAmount">{{ transaction.formatted }}</div>
                    <div v-if="transaction.accountNumber" :class="styles.accountInfo">
                        {{ transaction.accountNumber }}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
