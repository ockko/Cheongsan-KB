<script setup>
// 정책 페이지
import styles from '@/assets/styles/pages/Policy.module.css';
import DiagnosisSection from '@/components/domain/policy/DiagnosisSection.vue';
import DiagnosisResultSection from '@/components/domain/policy/DiagnosisResultSection.vue';
import PolicySection from '@/components/domain/policy/PolicySection.vue';
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getDiagnosisResult, getRecommendationName } from '@/api/diagnosis.js';

const route = useRoute();

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

// 자가진단 결과로부터 진단 데이터 설정
const setDiagnosisFromResult = (diagnosisResult) => {
  if (diagnosisResult) {
    setDiagnosisData(diagnosisResult);
  }
};

// 백엔드에서 진단 결과 가져오기
const loadDiagnosisFromBackend = async (diagnosisId, recommendationId) => {
  try {
    console.log('백엔드에서 진단 결과 로딩:', {
      diagnosisId,
      recommendationId,
    });

    // 백엔드에서 진단 결과 상세 정보 가져오기
    const diagnosisData = await getDiagnosisResult();

    console.log('백엔드 진단 결과:', diagnosisData);

    // 추천 제도명 변환
    const recommendationName = getRecommendationName(Number(recommendationId));

    // 백엔드 데이터를 기존 형식에 맞게 변환
    const transformedData = {
      userName: diagnosisData.userName || '사용자',
      diagnosisStage: recommendationName,
      institution: getInstitutionByRecommendation(Number(recommendationId)),
      policyName: recommendationName,
      description: getDescriptionByRecommendation(Number(recommendationId)),
      diagnosisId: diagnosisId,
      recommendationId: recommendationId,
    };

    userDiagnosis.value = transformedData;
  } catch (error) {
    console.error('백엔드 진단 결과 로딩 실패:', error);

    // 에러 발생 시 기본 추천으로 fallback
    const fallbackRecommendation = getRecommendationName(
      Number(recommendationId)
    );
    setDiagnosisData(fallbackRecommendation);
  }
};

// 추천 제도별 담당 기관 반환
const getInstitutionByRecommendation = (recommendationId) => {
  const institutionMap = {
    0: '금융감독원', // 예방적 상담
    1: '법원', // 개인파산
    2: '법원', // 개인회생
    3: '금융감독원', // 개인워크아웃
    4: '금융감독원', // 프리워크아웃
    5: '법원', // 신속채무조정
  };
  return institutionMap[recommendationId] || '관련 기관';
};

// 추천 제도별 설명 반환
const getDescriptionByRecommendation = (recommendationId) => {
  const descriptionMap = {
    0: '채무 문제 예방을 위한 상담 서비스를 제공합니다.',
    1: '재산을 모두 처분하여 채권자에게 배당하고 남은 부채를 면책받는 제도입니다.',
    2: '일정한 소득이 있는 개인이 법원의 인가를 받아 채무를 조정하는 제도입니다.',
    3: '금융기관과의 자율적 합의를 통해 채무를 조정하는 제도입니다.',
    4: '신용회복위원회를 통해 채무를 조정하는 제도입니다.',
    5: '법원의 개입 하에 채권자와 채무자가 합의하여 채무를 조정하는 제도입니다.',
  };
  return descriptionMap[recommendationId] || '채무 조정을 위한 제도입니다.';
};

// 페이지 마운트 시 자가진단 결과 확인
onMounted(async () => {
  // URL 쿼리 파라미터에서 진단 결과 확인
  const diagnosisId = route.query.diagnosisId;
  const recommendationId = route.query.recommendationId;
  const legacyDiagnosis = route.query.diagnosis;

  if (diagnosisId && recommendationId) {
    // 백엔드 연동 버전
    await loadDiagnosisFromBackend(diagnosisId, recommendationId);
  } else if (legacyDiagnosis) {
    // 기존 fallback 버전
    setDiagnosisFromResult(legacyDiagnosis);
  } else {
    // 기존 사용자의 진단 결과가 있는지 확인 (로그인 상태라면)
    try {
      const existingDiagnosis = await getDiagnosisResult();
      if (
        existingDiagnosis &&
        existingDiagnosis.recommendationId !== undefined
      ) {
        await loadDiagnosisFromBackend(
          existingDiagnosis.diagnosisId,
          existingDiagnosis.recommendationId
        );
      }
    } catch (error) {
      console.log('기존 진단 결과 없음 또는 로그인되지 않음');
    }
  }
});
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
