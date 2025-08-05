<script setup>
// 정책 페이지
import styles from '@/assets/styles/components/policy/Policy.module.css';
import DiagnosisSection from '@/components/domain/policy/DiagnosisSection.vue';
import DiagnosisResultSection from '@/components/domain/policy/DiagnosisResultSection.vue';
import PolicySection from '@/components/domain/policy/PolicySection.vue';
import { ref, computed } from 'vue';

// 사용자 진단 데이터 (실제로는 API나 store에서 가져올 예정)
const userDiagnosis = ref(null); // null이면 진단 해야함, 있으면 진단 결과 있음

// 진단 여부에 따른 컴포넌트 결정
const hasDiagnosis = computed(() => userDiagnosis.value !== null);

// 진단 데이터 설정 함수 (테스트용)
const setDiagnosisData = (stage = '개인파산') => {
  const diagnosisData = {
    개인파산: {
      userName: '홍길동',
      diagnosisStage: '개인파산',
      institution: '법원',
      policyName: '개인파산',
      description:
        '재산을 모두 처분하여 채권자에게 배당하고 남은 부채를 면책받는 제도입니다.',
    },
    신속채무조정: {
      userName: '김철수',
      diagnosisStage: '신속채무조정',
      institution: '법원',
      policyName: '신속채무조정',
      description:
        '법원의 개입 하에 채권자와 채무자가 합의하여 채무를 조정하는 제도입니다.',
    },
    개인워크아웃: {
      userName: '이영희',
      diagnosisStage: '개인워크아웃',
      institution: '금융감독원',
      policyName: '개인워크아웃',
      description:
        '금융기관과의 자율적 합의를 통해 채무를 조정하는 제도입니다.',
    },
    프리워크아웃: {
      userName: '박민수',
      diagnosisStage: '프리워크아웃',
      institution: '금융감독원',
      policyName: '프리워크아웃',
      description:
        '금융기관과의 자율적 합의를 통해 채무를 조정하는 제도입니다.',
    },
  };

  userDiagnosis.value = diagnosisData[stage] || diagnosisData['개인파산'];
};

// 진단 데이터 초기화 함수 (테스트용)
const clearDiagnosisData = () => {
  userDiagnosis.value = null;
};
</script>

<template>
  <div :class="styles.policyPage">
    <!-- 메인 콘텐츠 -->
    <main :class="styles.mainContent">
      <!-- 테스트용 버튼들 (개발 완료 후 제거) -->
      <div
        style="
          position: fixed;
          top: 10px;
          right: 10px;
          z-index: 1000;
          display: flex;
          flex-direction: column;
          gap: 5px;
        "
      >
        <button
          @click="setDiagnosisData('개인파산')"
          style="
            background: #003e65;
            color: white;
            padding: 6px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
          "
        >
          개인파산
        </button>
        <button
          @click="setDiagnosisData('신속채무조정')"
          style="
            background: #28a745;
            color: white;
            padding: 6px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
          "
        >
          신속채무조정
        </button>
        <button
          @click="setDiagnosisData('개인워크아웃')"
          style="
            background: #ffc107;
            color: white;
            padding: 6px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
          "
        >
          개인워크아웃
        </button>
        <button
          @click="setDiagnosisData('프리워크아웃')"
          style="
            background: #dc3545;
            color: white;
            padding: 6px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
          "
        >
          프리워크아웃
        </button>
        <button
          @click="clearDiagnosisData"
          style="
            background: #666;
            color: white;
            padding: 6px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
          "
        >
          진단 섹션
        </button>
      </div>

      <!-- 진단 여부에 따라 다른 섹션 렌더링 -->
      <DiagnosisResultSection
        v-if="hasDiagnosis"
        :diagnosis-data="userDiagnosis"
      />
      <DiagnosisSection v-else />

      <div :class="styles.titleLine"></div>
      <PolicySection />
    </main>
  </div>
</template>
