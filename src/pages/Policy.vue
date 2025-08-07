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

// 백엔드에서 진단 결과 가져오기
const loadDiagnosisFromBackend = async (diagnosisData) => {
  try {
    console.log('백엔드에서 진단 결과 로딩:', diagnosisData);

    // 서버 데이터를 그대로 사용 (DiagnosisResultSection에서 처리)
    userDiagnosis.value = diagnosisData;
    console.log('userDiagnosis 설정 완료:', userDiagnosis.value);
    console.log('hasDiagnosis 값:', hasDiagnosis.value);
  } catch (error) {
    console.error('백엔드 진단 결과 로딩 실패:', error);
  }
};

// 추천 제도별 담당 기관 반환 (URL 파라미터용 fallback)
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

// 추천 제도별 설명 반환 (URL 파라미터용 fallback)
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
  console.log('Policy.vue onMounted 시작');

  // URL 쿼리 파라미터에서 진단 결과 확인
  const diagnosisId = route.query.diagnosisId;
  const recommendationId = route.query.recommendationId;
  const legacyDiagnosis = route.query.diagnosis;

  console.log('URL 쿼리 파라미터:', {
    diagnosisId,
    recommendationId,
    legacyDiagnosis,
  });

  if (diagnosisId && recommendationId) {
    // URL 파라미터가 있는 경우 - 기존 방식 유지
    console.log('URL 파라미터로 진단 결과 로딩');
    const fallbackData = {
      id: diagnosisId,
      operatingEntity: getInstitutionByRecommendation(Number(recommendationId)),
      programName: getRecommendationName(Number(recommendationId)),
      simpleDescription: getDescriptionByRecommendation(
        Number(recommendationId)
      ),
    };
    await loadDiagnosisFromBackend(fallbackData);
  } else if (legacyDiagnosis) {
    // 기존 fallback 버전 - 더 이상 지원하지 않음
    console.log('Legacy diagnosis parameter is no longer supported');
  } else {
    // 기존 사용자의 진단 결과가 있는지 확인
    console.log('getDiagnosisResult API 호출 시작');
    try {
      const existingDiagnosis = await getDiagnosisResult();
      console.log('기존 진단 결과:', existingDiagnosis);
      console.log('진단 결과 타입:', typeof existingDiagnosis);

      if (existingDiagnosis && existingDiagnosis.id !== undefined) {
        console.log('기존 진단 결과로 DiagnosisResultSection 표시');
        await loadDiagnosisFromBackend(existingDiagnosis);
      } else {
        console.log('진단 결과가 없거나 id가 없음');
        console.log('hasDiagnosis 값:', hasDiagnosis.value);
      }
    } catch (error) {
      console.log('기존 진단 결과 없음 또는 로그인되지 않음:', error);
      console.log('hasDiagnosis 값:', hasDiagnosis.value);
    }
  }

  console.log('onMounted 완료, 최종 hasDiagnosis 값:', hasDiagnosis.value);
});
</script>

<template>
  <div :class="styles.policyPage">
    <!-- 메인 콘텐츠 -->
    <main :class="styles.mainContent">
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
