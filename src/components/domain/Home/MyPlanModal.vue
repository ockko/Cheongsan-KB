<script setup>
import styles from '@/assets/styles/components/home/MyPlanModal.module.css';

const emit = defineEmits(['close']);
const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
  strategy: {
    type: Object,
    required: true,
  },
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

const handleClose = () => {
  console.log('MyPlanModal handleClose 함수 호출됨');
  console.log('현재 props.isOpen:', props.isOpen);
  emit('close');
  console.log('close 이벤트 emit 완료');
};

const formatCurrency = (value) => {
  if (!value && value !== 0) return '0원';
  return new Intl.NumberFormat('ko-KR').format(value) + '원';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '날짜 없음';
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}월 ${date.getDate()}일`;
};

const calculateDDay = (dateStr) => {
  if (!dateStr) return 0;
  const today = new Date();
  const target = new Date(dateStr);
  const diffTime = target - today;
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

const hasPrepayment = (payment) => {
  if (!payment) return false;
  return (payment.prepayment || 0) > 0 || (payment.prepaymentFee || 0) > 0;
};

const getExtraAmount = (payment) => {
  if (!payment) return 0;
  return (payment.prepayment || 0) + (payment.prepaymentFee || 0);
};
</script>

<template>
  <Teleport to="body">
    <div
      v-if="props.isOpen"
      :class="styles.modalOverlay"
      @click.self="handleClose"
    >
      <div :class="styles.modalContent">
        <!-- 풀모달 헤더 -->

        <div :class="styles.result">
          <h2 :class="styles.title">
            <i class="far fa-lightbulb"></i> 상환 전략
          </h2>
          <p :class="styles.subtitle">
            현재 적용된 전략은
            <span :class="styles.highlight">
              {{ mapStrategyLabel(strategy?.strategyType) }}
            </span>
            입니다
          </p>
        </div>

        <div :class="styles.info" v-if="strategy">
          <div :class="styles.infoItem">
            <span :class="styles.label">총 상환 기간</span>
            <div :class="styles.content">
              {{ strategy.totalMonths || 0 }}개월
            </div>
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
          v-if="strategy?.sortedLoanNames?.length"
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
                        strategy.repaymentHistory?.[loanName]?.[0]?.paymentDate
                      )
                    }}
                  </span>
                </div>

                <div :class="styles.cardInfo">
                  <div :class="styles.repaymentDate">
                    {{
                      formatDate(
                        strategy.repaymentHistory?.[loanName]?.[0]?.paymentDate
                      )
                    }}
                    납부할 상환액
                  </div>

                  <div :class="styles.repaymentAmountWrapper">
                    <span :class="styles.repaymentAmount">
                      {{
                        formatCurrency(
                          (strategy.repaymentHistory?.[loanName]?.[0]
                            ?.principal || 0) +
                            (strategy.repaymentHistory?.[loanName]?.[0]
                              ?.interest || 0)
                        )
                      }}
                    </span>

                    <span
                      v-if="
                        hasPrepayment(
                          strategy.repaymentHistory?.[loanName]?.[0]
                        )
                      "
                      :class="styles.extraPayment"
                    >
                      +
                      {{
                        formatCurrency(
                          getExtraAmount(
                            strategy.repaymentHistory?.[loanName]?.[0]
                          )
                        )
                      }}<br />
                      <span
                        :class="styles.feeLabel"
                        v-if="
                          strategy.repaymentHistory?.[loanName]?.[0]
                            ?.prepaymentFee > 0
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
      </div>
    </div>
  </Teleport>
</template>
