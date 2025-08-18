<script setup>
import styles from '@/assets/styles/pages/Diagnosis.module.css';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { submitDiagnosis } from '@/api/diagnosis.js';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';
const router = useRouter();
const authStore = useAuthStore();
const uiStore = useUiStore();

// 현재 단계 관리 (-1: 시작 페이지, 0~2: 진단 단계)
const currentStep = ref(-1);

// 사용자 답변 저장
const answers = ref({});

// 진단 단계별 질문 정의
const diagnosisSteps = [
  {
    id: 'overdue-status',
    title: '현재 연체 상황을 알려주세요',
    questions: [
      {
        id: 'overdue-period',
        type: 'radio',
        options: [
          { value: 'no-overdue', label: '연체 없음' },
          { value: 'under-30', label: '30일 이하 연체' },
          { value: 'under-90', label: '90일 이하 연체' },
          { value: 'over-90', label: '90일 이상 연체' },
        ],
      },
    ],
  },
  {
    id: 'debt-scale',
    title: '총 채무 금액을 알려주세요',
    questions: [
      {
        id: 'debt-amount',
        type: 'radio',
        options: [
          { value: 'under-500m', label: '5억원 미만' },
          { value: '500m-1b', label: '5억원 이상 ~ 10억원 미만' },
          { value: 'over-1b', label: '10억원 이상' },
        ],
      },
    ],
  },
  {
    id: 'income-status',
    title: '현재 소득 상황을 알려주세요',
    questions: [
      {
        id: 'has-income',
        type: 'radio',
        options: [
          { value: 'has-income', label: '소득 있음' },
          { value: 'no-income', label: '소득 없음' },
        ],
      },
    ],
  },
];

// 현재 단계의 질문들
const currentQuestions = computed(() => {
  return diagnosisSteps[currentStep.value]?.questions || [];
});

// 모든 질문에 답변했는지 확인
const isCurrentStepComplete = computed(() => {
  return currentQuestions.value.every((question) => answers.value[question.id]);
});

// 전체 진행률
const progressPercentage = computed(() => {
  if (currentStep.value < 0) return 0;
  return ((currentStep.value + 1) / diagnosisSteps.length) * 100;
});

// 진행률 선의 너비 (점들 사이의 간격을 고려)
const progressLineWidth = computed(() => {
  if (currentStep.value < 0) return 0;
  // 3개 점이므로 2개 구간, 각 구간은 50%
  return (currentStep.value / (diagnosisSteps.length - 1)) * 100;
});

// 시작 페이지 여부
const isStartPage = computed(() => currentStep.value === -1);

// 진단 시작
const startDiagnosis = () => {
  currentStep.value = 0;
};

// 답변 선택 처리 - integer 값으로 저장
const selectAnswer = (questionId, value) => {
  // 각 질문별로 integer 값으로 매핑
  let integerValue;

  if (questionId === 'overdue-period') {
    // 연체 현황 (1~4)
    const mapping = {
      'no-overdue': 1, // 연체 없음
      'under-30': 2, // 30일 이하 연체
      'under-90': 3, // 90일 이하 연체
      'over-90': 4, // 90일 이상 연체
    };
    integerValue = mapping[value];
    answers.value[1] = integerValue; // q1
  } else if (questionId === 'debt-amount') {
    // 채무 규모 (1~3)
    const mapping = {
      'under-500m': 1, // 5억원 미만
      '500m-1b': 2, // 5억원 이상 ~ 10억원 미만
      'over-1b': 3, // 10억원 이상
    };
    integerValue = mapping[value];
    answers.value[2] = integerValue; // q2
  } else if (questionId === 'has-income') {
    // 소득 여부 (1~2)
    const mapping = {
      'has-income': 1, // 소득 있음
      'no-income': 2, // 소득 없음
    };
    integerValue = mapping[value];
    answers.value[3] = integerValue; // q3
  }

  // 원래 값도 UI 상태 관리를 위해 저장
  answers.value[questionId] = value;
};

// 다음 단계로 이동
const nextStep = () => {
  if (currentStep.value < diagnosisSteps.length - 1) {
    currentStep.value++;
  } else {
    // 진단 완료 - 결과 처리
    completeDiagnosis();
  }
};

// 이전 단계로 이동
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
};

// 진단 완료 처리
const completeDiagnosis = async () => {
  try {
    // 로딩 상태 표시 (선택적)
    const isSubmitting = ref(true);

    // 백엔드로 진단 데이터 전송
    const diagnosisData = {
      q1: answers.value[1], // 연체 현황
      q2: answers.value[2], // 채무 규모
      q3: answers.value[3], // 소득 여부
    };

    const result = await submitDiagnosis(diagnosisData);

    // 백엔드에서 받은 결과로 정책 페이지로 이동
    const diagnosisId = result?.diagnosisId || result?.id;
    const recommendationId = result?.recommendationId || result?.recommendation;

    const query = {};
    if (diagnosisId && diagnosisId !== 'undefined') {
      query.diagnosisId = diagnosisId;
    }
    if (recommendationId && recommendationId !== 'undefined') {
      query.recommendationId = recommendationId;
    }

    router.push({
      path: '/policy',
      query,
    });
  } catch (error) {
    console.error('진단 완료 처리 실패:', error);

    uiStore.openModal({
      title: '오류 발생',
      message: '진단 결과를 저장하는데 문제가 발생했습니다. 다시 시도해주세요.',
      isError: true,
    });
  }
};

// 정책 페이지로 돌아가기
const goHome = () => {
  router.push('/policy');
};
</script>

<template>
  <div :class="styles.diagnosisPage">
    <!-- 헤더 영역 -->
    <header :class="styles.diagnosisHeader">
      <button @click="goHome" :class="styles.diagnosisBackButton">
        <i class="fa fa-arrow-left"></i>
      </button>
      <h1 :class="styles.diagnosisTitle">진단하기</h1>
    </header>

    <!-- 진행률 표시 (진단 시작 후에만 표시) -->
    <div v-if="!isStartPage" :class="styles.diagnosisProgressContainer">
      <div :class="[styles.diagnosisProgressDots, `step-${currentStep}`]">
        <div
          v-for="(step, index) in diagnosisSteps"
          :key="index"
          :class="[
            styles.diagnosisProgressDot,
            { [styles.active]: index <= currentStep },
          ]"
        ></div>
      </div>
    </div>

    <!-- 메인 콘텐츠 -->
    <main :class="styles.diagnosisMainContent">
      <!-- 진단 시작 페이지 -->
      <div v-if="isStartPage" :class="styles.diagnosisStartContainer">
        <div :class="styles.diagnosisStartContent">
          <div :class="styles.diagnosisIconContainer">
            <img
              src="/images/diagnosis0.png"
              alt="진단 아이콘"
              :class="styles.diagnosisDiagnosticIcon"
            />
          </div>

          <div :class="styles.diagnosisStartTextContainer">
            <h1 :class="styles.diagnosisStartTitle">
              {{ authStore.state.user.nickName }}님의 이야기를 알려주세요
            </h1>
            <p :class="styles.diagnosisStartDescription">
              간단한 질문에 답하시면 현재 상황에 가장 적합한 공적 채무조정
              제도를 추천해드립니다.
            </p>
          </div>

          <button @click="startDiagnosis" :class="styles.diagnosisStartButton">
            <span :class="styles.diagnosisStartButtonText">시작하기</span>
          </button>
        </div>
      </div>

      <!-- 진단 단계 페이지 -->
      <div v-else :class="styles.diagnosisStepContainer">
        <!-- 단계 아이콘 -->
        <div :class="styles.diagnosisStepIcon">
          <img
            :src="`/images/diagnosis${currentStep + 1}.png`"
            :alt="`진단 단계 ${currentStep + 1}`"
            :class="styles.diagnosisDiagnosisIcon"
          />
        </div>

        <!-- 단계 제목 -->
        <div :class="styles.diagnosisStepHeader">
          <h2 :class="styles.diagnosisStepTitle">
            {{ diagnosisSteps[currentStep].title }}
          </h2>
        </div>

        <!-- 구분선 -->
        <div :class="styles.diagnosisDivider"></div>

        <!-- 질문 영역 -->
        <div :class="styles.diagnosisQuestionContainer">
          <div
            v-for="question in currentQuestions"
            :key="question.id"
            :class="styles.diagnosisQuestionItem"
          >
            <!-- 선택형 질문 -->
            <div
              v-if="question.type === 'radio'"
              :class="styles.diagnosisOptionsContainer"
            >
              <label
                v-for="option in question.options"
                :key="option.value"
                :class="[
                  styles.diagnosisOptionLabel,
                  { [styles.selected]: answers[question.id] === option.value },
                ]"
                @click="selectAnswer(question.id, option.value)"
              >
                <input
                  type="radio"
                  :name="question.id"
                  :value="option.value"
                  :checked="answers[question.id] === option.value"
                  :class="styles.diagnosisOptionInput"
                  @change.stop
                />
                <span :class="styles.diagnosisOptionText">{{
                  option.label
                }}</span>
              </label>
            </div>
          </div>
        </div>

        <!-- 하단 버튼 영역 -->
        <div :class="styles.diagnosisButtonContainer">
          <button
            v-if="currentStep > 0"
            @click="prevStep"
            :class="[styles.diagnosisButton, styles.diagnosisPrevButton]"
          >
            이전
          </button>

          <button
            @click="nextStep"
            :disabled="!isCurrentStepComplete"
            :class="[
              styles.diagnosisButton,
              styles.diagnosisNextButton,
              { [styles.disabled]: !isCurrentStepComplete },
              { [styles.fullWidth]: currentStep === 0 },
            ]"
          >
            {{
              currentStep === diagnosisSteps.length - 1 ? '진단 완료' : '다음'
            }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>
