<script setup>
import styles from '@/assets/styles/pages/Diagnosis.module.css';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { submitDiagnosis } from '@/api/diagnosis.js';

const router = useRouter();

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

    console.log('진단 데이터 전송:', diagnosisData);

    const result = await submitDiagnosis(diagnosisData);

    console.log('백엔드 응답:', result);

    // 백엔드에서 받은 결과로 정책 페이지로 이동
    router.push({
      path: '/policy',
      query: {
        diagnosisId: result.diagnosisId || result.id,
        recommendationId: result.recommendationId || result.recommendation,
      },
    });
  } catch (error) {
    console.error('진단 완료 처리 실패:', error);

    // 에러 발생 시 기본 로직으로 fallback
    const fallbackRecommendation = calculateRecommendation();
    router.push({
      path: '/policy',
      query: { diagnosis: fallbackRecommendation },
    });

    // 사용자에게 에러 메시지 표시 (선택적)
    alert(
      '진단 결과를 저장하는데 문제가 발생했습니다. 임시 결과를 표시합니다.'
    );
  }
};

// 추천 제도 계산 (새로운 질문 기반 로직)
const calculateRecommendation = () => {
  const overdueStatus = answers.value['overdue-period'];
  const debtAmount = answers.value['debt-amount'];
  const hasIncome = answers.value['has-income'];

  // 새로운 규칙 기반 추천 로직
  // 1. 소득 없음 + 고액 채무 (5억 이상) = 개인파산
  if (
    hasIncome === 'no-income' &&
    (debtAmount === '500m-1b' || debtAmount === 'over-1b')
  ) {
    return '개인파산';
  }

  // 2. 장기 연체 (90일 이상) + 고액 채무 = 개인파산
  if (overdueStatus === 'over-90' && debtAmount === 'over-1b') {
    return '개인파산';
  }

  // 3. 소득 있음 + 연체 없음 또는 단기 연체 = 개인워크아웃
  if (
    hasIncome === 'has-income' &&
    (overdueStatus === 'no-overdue' || overdueStatus === 'under-30')
  ) {
    return '개인워크아웃';
  }

  // 4. 소득 있음 + 중기 연체 (30-90일) = 신속채무조정
  if (hasIncome === 'has-income' && overdueStatus === 'under-90') {
    return '신속채무조정';
  }

  // 5. 기타 경우 = 프리워크아웃
  return '프리워크아웃';
};

// 홈으로 돌아가기
const goHome = () => {
  router.push('/home');
};
</script>

<template>
  <div :class="styles.diagnosisPage">
    <!-- 헤더 영역 -->
    <header :class="styles.header">
      <button @click="goHome" :class="styles.backButton">←</button>
      <h1 :class="styles.title">자가진단</h1>
    </header>

    <!-- 진행률 표시 (진단 시작 후에만 표시) -->
    <div v-if="!isStartPage" :class="styles.progressContainer">
      <div :class="styles.progressBar">
        <div
          :class="styles.progressFill"
          :style="{ width: progressPercentage + '%' }"
        ></div>
      </div>
      <span :class="styles.progressText">
        {{ currentStep + 1 }} / {{ diagnosisSteps.length }}
      </span>
    </div>

    <!-- 메인 콘텐츠 -->
    <main :class="styles.mainContent">
      <!-- 진단 시작 페이지 -->
      <div v-if="isStartPage" :class="styles.startContainer">
        <div :class="styles.startContent">
          <div :class="styles.iconContainer">
            <div :class="styles.diagnosticIcon"></div>
          </div>

          <div :class="styles.startTextContainer">
            <h1 :class="styles.startTitle">가나다님의 이야기를 알려주세요</h1>
            <p :class="styles.startDescription">
              간단한 질문에 답하시면 현재 상황에 가장 적합한 공적 채무조정
              제도를 추천해드립니다.
            </p>
          </div>

          <button @click="startDiagnosis" :class="styles.startButton">
            <span :class="styles.startButtonText">시작하기</span>
          </button>
        </div>
      </div>

      <!-- 진단 단계 페이지 -->
      <div v-else :class="styles.stepContainer">
        <!-- 단계 제목 -->
        <div :class="styles.stepHeader">
          <h2 :class="styles.stepTitle">
            {{ diagnosisSteps[currentStep].title }}
          </h2>
        </div>

        <!-- 질문 영역 -->
        <div :class="styles.questionContainer">
          <div
            v-for="question in currentQuestions"
            :key="question.id"
            :class="styles.questionItem"
          >
            <!-- 선택형 질문 -->
            <div
              v-if="question.type === 'radio'"
              :class="styles.optionsContainer"
            >
              <label
                v-for="option in question.options"
                :key="option.value"
                :class="[
                  styles.optionLabel,
                  { [styles.selected]: answers[question.id] === option.value },
                ]"
                @click="selectAnswer(question.id, option.value)"
              >
                <input
                  type="radio"
                  :name="question.id"
                  :value="option.value"
                  :checked="answers[question.id] === option.value"
                  :class="styles.optionInput"
                  @change.stop
                />
                <span :class="styles.optionText">{{ option.label }}</span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 하단 버튼 영역 (진단 단계에서만 표시) -->
    <footer v-if="!isStartPage" :class="styles.footer">
      <div :class="styles.buttonContainer">
        <button
          v-if="currentStep > 0"
          @click="prevStep"
          :class="[styles.button, styles.prevButton]"
        >
          이전
        </button>

        <button
          @click="nextStep"
          :disabled="!isCurrentStepComplete"
          :class="[
            styles.button,
            styles.nextButton,
            { [styles.disabled]: !isCurrentStepComplete },
          ]"
        >
          {{ currentStep === diagnosisSteps.length - 1 ? '진단 완료' : '다음' }}
        </button>
      </div>
    </footer>
  </div>
</template>
