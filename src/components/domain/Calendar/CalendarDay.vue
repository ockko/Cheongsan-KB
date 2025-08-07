<script setup>
import { computed } from 'vue';
import styles from '@/assets/styles/components/Calendar/CalendarDay.module.css';

const props = defineProps({
  day: {
    type: Object,
    required: true,
  },
  hasSelectedDate: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['click']);

// 금액 포맷팅 함수
const formatAmount = (amount) => {
  return amount.toLocaleString();
};

// 대출 이름 4글자로 제한
const truncateLoanName = (name) => {
  if (!name) return '';
  return name.length > 4 ? name.substring(0, 4) : name;
};

// 표시할 대출 목록 계산 (최대 3개 + 더보기)
const displayLoans = computed(() => {
  if (!props.day.loan || props.day.loan.length === 0) return [];

  const loans = props.day.loan;
  const result = [];

  // 처음 3개까지만 표시
  const maxDisplay = 3;
  const displayCount = Math.min(loans.length, maxDisplay);

  for (let i = 0; i < displayCount; i++) {
    result.push({
      type: 'loan',
      data: loans[i],
    });
  }

  // 3개 초과시 더보기 표시
  if (loans.length > maxDisplay) {
    result.push({
      type: 'more',
      count: loans.length - maxDisplay,
    });
  }

  return result;
});

// 클래스 계산
const dayClasses = computed(() => {
  const classes = [styles.dayCell];

  if (!props.day.isCurrentMonth) {
    classes.push(styles.otherMonth);
  }

  if (props.day.isToday) {
    classes.push(styles.today);
  }

  if (props.day.isSelected) {
    classes.push(styles.selected);
  }

  // 클릭 가능한 날짜인지 확인 (데이터가 있는 날짜)
  if ((props.day.transactions || props.day.loan) && props.day.isCurrentMonth) {
    classes.push(styles.clickable);
  }

  // 선택된 날짜가 있을 때 컴팩트 모드 적용
  if (props.hasSelectedDate) {
    classes.push(styles.compact);
  }

  return classes;
});

// 클릭 핸들러
const handleClick = () => {
  emit('click');
};
</script>

<template>
  <div :class="dayClasses" @click="handleClick">
    <div :class="styles.dateNumber">{{ day.date }}</div>

    <!-- 선택된 날짜가 없을 때: 기본 표시 -->
    <template v-if="!hasSelectedDate">
      <!-- 대출 정보 표시 (최대 3개 + 더보기) -->
      <div v-if="displayLoans.length > 0" :class="styles.loanContainer">
        <template v-for="(item, index) in displayLoans" :key="index">
          <!-- 일반 대출 -->
          <div
            v-if="item.type === 'loan'"
            :class="styles.loanTag"
            :title="item.data.debtName"
          >
            {{ truncateLoanName(item.data.debtName) }}
          </div>

          <!-- 더보기 표시 -->
          <div
            v-else-if="item.type === 'more'"
            :class="[styles.loanTag, styles.moreTag]"
            :title="`${item.count}개 더 있음`"
          >
            +{{ item.count }}
          </div>
        </template>
      </div>

      <!-- 거래 내역 표시 -->
      <div v-if="day.transactions" :class="styles.transactions">
        <!-- 지출 표시 -->
        <div
          v-if="day.transactions.expense && day.transactions.expense > 0"
          :class="[styles.amount, styles.expense]"
        >
          - {{ formatAmount(day.transactions.expense) }}
        </div>

        <!-- 수익 표시 -->
        <div
          v-if="day.transactions.income && day.transactions.income > 0"
          :class="[styles.amount, styles.income]"
        >
          + {{ formatAmount(day.transactions.income) }}
        </div>
      </div>
    </template>

    <!-- 선택된 날짜가 있을 때: 동그라미 표시 -->
    <template v-else>
      <div :class="styles.compactIndicators">
        <!-- 대출 동그라미 -->
        <div
          v-if="displayLoans.length > 0"
          :class="[styles.indicator, styles.loanIndicator]"
          :title="`대출 ${displayLoans.length}개`"
        ></div>

        <!-- 지출 동그라미 -->
        <div
          v-if="
            day.transactions &&
            day.transactions.expense &&
            day.transactions.expense > 0
          "
          :class="[styles.indicator, styles.expenseIndicator]"
          :title="`지출 ${formatAmount(day.transactions.expense)}`"
        ></div>

        <!-- 수입 동그라미 -->
        <div
          v-if="
            day.transactions &&
            day.transactions.income &&
            day.transactions.income > 0
          "
          :class="[styles.indicator, styles.incomeIndicator]"
          :title="`수입 ${formatAmount(day.transactions.income)}`"
        ></div>
      </div>
    </template>
  </div>
</template>
