<script setup>
import styles from '@/assets/styles/components/simulation/PlanModal.module.css';
import { applyPlan } from '@/api/repayment-simulation';
import { useUiStore } from '@/stores/ui';
const emit = defineEmits(['close']);
const props = defineProps({
  isOpen: Boolean,
  strategy: Object,
});

const mapStrategyLabel = (type) => {
  switch (type) {
    case 'TCS_RECOMMEND':
      return '티모청 추천 전략';
    case 'HIGH_INTEREST_FIRST':
      return '고금리 우선 전략';
    case 'SMALL_AMOUNT_FIRST':
      return '소액 우선 전략';
    case 'OLDEST_FIRST':
      return '오래된 순 전략';
    default:
      return type;
  }
};

const onApplyPlan = async () => {
  const uiStore = useUiStore();

  try {
    await applyPlan(props.strategy.strategyType);
    close();
  } catch (error) {
    uiStore.openModal({
      title: '오류 발생',
      message: '전략 적용 중 오류가 발생했습니다.',
      isError: true,
    });
  }
};

const close = () => emit('close');

const formatCurrency = (value) =>
  new Intl.NumberFormat('ko-KR').format(value) + '원';

const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}월 ${date.getDate()}일`;
};

const calculateDDay = (dateStr) => {
  const today = new Date();
  const target = new Date(dateStr);
  const diffTime = target - today;
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

const hasPrepayment = (payment) => {
  return (payment.prepayment || 0) > 0 || (payment.prepaymentFee || 0) > 0;
};

const getExtraAmount = (payment) => {
  return (payment.prepayment || 0) + (payment.prepaymentFee || 0);
};
</script>

<template>
  <div v-if="isOpen" :class="styles.modalOverlay" @click.self="close">
    <div :class="styles.modalContent">
      <div :class="styles.result">
        <h2 :class="styles.title">
          <i class="far fa-lightbulb"></i> 상환 전략
        </h2>
        <p :class="styles.subtitle">
          현재 적용된 전략은<br />
          <span :class="styles.highlight">
            {{ mapStrategyLabel(strategy.strategyType) }}
          </span>
          입니다
        </p>
      </div>

      <div :class="styles.info" v-if="strategy">
        <div :class="styles.infoItem">
          <span :class="styles.label">총 상환 기간</span>
          <div :class="styles.content">{{ strategy.totalMonths }}개월</div>
        </div>

        <div :class="styles.infoItem">
          <span :class="styles.label">총 절약 이자</span>
          <p :class="styles.content">
            {{ formatCurrency(strategy.interestSaved) }}
          </p>
        </div>

        <div :class="styles.infoItem">
          <span :class="styles.label">총 납입액</span>
          <p :class="styles.content">
            {{ formatCurrency(strategy.totalPayment) }}
          </p>
        </div>

        <div :class="styles.infoItem">
          <span :class="styles.label">기존 납입 예상액</span>
          <p :class="styles.content">
            {{ formatCurrency(strategy.originalPayment) }}
          </p>
        </div>

        <div :class="styles.infoItem">
          <span :class="styles.label">중도상환 수수료</span>
          <p :class="styles.content">
            {{ formatCurrency(strategy.totalPrepaymentFee) }}
          </p>
        </div>
      </div>

      <hr :class="styles.divider" />

      <div
        :class="styles.repaymentOrder"
        v-if="strategy.sortedLoanNames?.length"
      >
        <p :class="styles.sectionTitle">
          <span :class="styles.titleLabel">상환 대출 순서</span>
        </p>

        <div
          :class="styles.loanCardList"
          v-for="(loanName, index) in strategy.sortedLoanNames"
          :key="index"
        >
          <div :class="styles.repaymentCard1">
            <div :class="styles.loanNameTop">{{ loanName }}</div>

            <div :class="styles.repaymentMain">
              <div :class="styles.cardIcon">
                <span :class="styles.dDay">
                  D-{{
                    calculateDDay(
                      strategy.repaymentHistory[loanName][0].paymentDate
                    )
                  }}
                </span>
              </div>

              <div :class="styles.cardInfo">
                <div :class="styles.repaymentDate">
                  {{
                    formatDate(
                      strategy.repaymentHistory[loanName][0].paymentDate
                    )
                  }}
                  납부할 상환액
                </div>

                <div :class="styles.repaymentAmountWrapper">
                  <span :class="styles.repaymentAmount">
                    {{
                      formatCurrency(
                        strategy.repaymentHistory[loanName][0].principal +
                          strategy.repaymentHistory[loanName][0].interest
                      )
                    }}
                  </span>

                  <span
                    v-if="hasPrepayment(strategy.repaymentHistory[loanName][0])"
                    :class="styles.extraPayment"
                  >
                    +
                    {{
                      formatCurrency(
                        getExtraAmount(strategy.repaymentHistory[loanName][0])
                      )
                    }}<br />
                    <span
                      :class="styles.feeLabel"
                      v-if="
                        strategy.repaymentHistory[loanName][0].prepaymentFee > 0
                      "
                    >
                      (중도상환수수료 포함)
                    </span>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div
            v-if="index !== strategy.sortedLoanNames.length - 1"
            :class="styles.divider2"
          ></div>
        </div>
      </div>

      <div :class="styles.actions">
        <button :class="styles.cancel" @click="close">취소</button>
        <button :class="styles.apply" @click="onApplyPlan">플랜 적용</button>
      </div>
    </div>
  </div>
</template>
