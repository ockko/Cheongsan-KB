<script setup>
// 정책 페이지
import styles from '@/assets/styles/pages/Policy.module.css';
import DiagnosisSection from '@/components/domain/policy/DiagnosisSection.vue';
import DiagnosisResultSection from '@/components/domain/policy/DiagnosisResultSection.vue';
import PolicySection from '@/components/domain/policy/PolicySection.vue';
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getDiagnosisResult } from '@/api/diagnosis.js';

const route = useRoute();

// 사용자 진단 데이터
const userDiagnosis = ref(null);

// 진단 여부에 따른 컴포넌트 결정
const hasDiagnosis = computed(() => userDiagnosis.value !== null);

// 백엔드에서 진단 결과 가져오기
const loadDiagnosisFromBackend = async (diagnosisData) => {
  try {
    userDiagnosis.value = diagnosisData;
  } catch (error) {
    console.error('백엔드 진단 결과 로딩 실패:', error);
  }
};

// 페이지 마운트 시 자가진단 결과 확인
onMounted(async () => {
  // URL 쿼리 파라미터에서 진단 결과 확인
  const diagnosisId = route.query.diagnosisId;
  const recommendationId = route.query.recommendationId;

  if (diagnosisId && recommendationId) {
    // URL 파라미터가 있는 경우
    const fallbackData = {
      id: diagnosisId,
      operatingEntity: '관련 기관',
      programName: '추천 제도',
      simpleDescription: '채무 조정을 위한 제도입니다.',
    };
    await loadDiagnosisFromBackend(fallbackData);
  } else {
    // 기존 사용자의 진단 결과가 있는지 확인
    try {
      const existingDiagnosis = await getDiagnosisResult();
      if (existingDiagnosis && existingDiagnosis.id !== undefined) {
        await loadDiagnosisFromBackend(existingDiagnosis);
      }
    } catch (error) {
      // 진단 결과 없음 또는 로그인되지 않음
    }
  }
});
</script>

<template>
  <div :class="styles.policyPage">
    <!-- 메인 콘텐츠 -->
    <main :class="styles.policyMainContent">
      <!-- 진단 여부에 따라 다른 섹션 렌더링 -->
      <DiagnosisResultSection
        v-if="hasDiagnosis"
        :diagnosis-data="userDiagnosis"
      />
      <DiagnosisSection v-else />

      <div :class="styles.policyTitleLine"></div>
      <PolicySection />
    </main>
  </div>
</template>
