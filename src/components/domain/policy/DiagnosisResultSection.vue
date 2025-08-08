<script setup>
import styles from '@/assets/styles/components/policy/DiagnosisResultSection.module.css';
import { useRouter } from 'vue-router';
import { ref, computed, onMounted } from 'vue';
import DiagnosisStageModal from './DiagnosisStageModal.vue';
import { getRecommendationDetail } from '@/api/diagnosis.js';

// Props 정의
const props = defineProps({
  diagnosisData: {
    type: Object,
    required: true,
  },
});

const router = useRouter();

// 진단 결과 데이터 (props에서 받거나 기본값 사용)
const diagnosisResult = computed(() => {
  const baseData = props.diagnosisData || {
    userName: '사용자',
    diagnosisStage: '진단 결과 없음',
    institution: '관련 기관',
    policyName: '추천 제도 없음',
    description: '진단 결과를 불러올 수 없습니다.',
  };

  // 서버에서 받은 진단 결과의 programName을 diagnosisStage로 사용
  return {
    ...baseData,
    diagnosisStage: baseData.programName || baseData.diagnosisStage,
  };
});

// 정책 데이터 정의
const policies = [
  {
    id: '1',
    programName: '개인파산',
    operatingEntity: '개인파산',
    institution: '법원',
    simpleDescription:
      '소득도 재산도 없는 극단적 상환불능자에게, 보유 재산 분배 후 남은 모든 채무를 법적으로 면책해 주는 제도',
    icon: '/images/court.png',
  },
  {
    id: '2',
    programName: '개인회생',
    operatingEntity: '개인회생',
    institution: '법원',
    simpleDescription:
      '소득이 적거나, 변제계획을 3~5년 성실히 이행 가능한 상환불능자에게 재산 초과분 변제 시 나머지 채무를 탕감해주는 법원 주도 제도',
    icon: '/images/court.png',
  },
  {
    id: '5',
    programName: '신속채무조정',
    operatingEntity: '신속채무조정',
    institution: '신용회복위원회',
    simpleDescription:
      '연체 30일 이하의 단기 연체자나 연체 위기에 놓인 사람들이 채무를 정상적으로 상환하기 어려울 때, 일정 기간 상환을 유예하거나 상환 기간을 연장해주는 제도',
    icon: '/images/court.png',
  },
  {
    id: '3',
    programName: '개인워크아웃',
    operatingEntity: '개인워크아웃',
    institution: '신용회복위원회',
    simpleDescription:
      '신용회복위원회를 통해 연체가 심한 채무자 대상, 연체이자·원금 일부를 감면하고 최장 10년간 채무를 분할 상환하게 돕는 민간 절차',
    icon: '/images/court.png',
  },
  {
    id: '4',
    programName: '프리워크아웃',
    operatingEntity: '프리워크아웃',
    institution: '신용회복위원회',
    simpleDescription:
      '단기(31~89일) 연체자에 특화, 연체이자 감면·일부 원금 상각 등으로 신속하게 재기를 지원',
    icon: '/images/court.png',
  },
  {
    id: '6',
    programName: '채무조정 심사 탈락',
    operatingEntity: '채무조정 심사 탈락',
    institution: '해당없음',
    simpleDescription:
      '월 소득이 높아 상환 능력이 충분하다고 판단되어 채무조정 대상에서 제외된 경우',
    icon: '/images/court.png',
  },
];

// 서버에서 받은 진단 결과의 id와 일치하는 정책 찾기
const findMatchingPolicy = (diagnosisId) => {
  return policies.find((policy) => policy.id === diagnosisId.toString());
};

// 현재 선택된 정책 인덱스
const currentPolicyIndex = ref(0);

// 초기 인덱스 설정 (서버 데이터와 매칭되는 정책으로 시작)
const initializePolicyIndex = () => {
  if (props.diagnosisData && props.diagnosisData.id) {
    const matchingIndex = policies.findIndex(
      (policy) => policy.id === props.diagnosisData.id.toString()
    );
    if (matchingIndex !== -1) {
      currentPolicyIndex.value = matchingIndex;
    }
  }
};

// 현재 정책 데이터 (인덱스 기반)
const currentPolicy = computed(() => {
  return policies[currentPolicyIndex.value];
});

// 컴포넌트 마운트 시 초기 인덱스 설정
onMounted(() => {
  initializePolicyIndex();
});

// 이전 정책으로 이동
const goToPreviousPolicy = () => {
  currentPolicyIndex.value =
    (currentPolicyIndex.value - 1 + policies.length) % policies.length;
};

// 다음 정책으로 이동
const goToNextPolicy = () => {
  currentPolicyIndex.value = (currentPolicyIndex.value + 1) % policies.length;
};

// 모달 상태 관리
const isModalVisible = ref(false);
const modalData = ref(null);
const isLoading = ref(false);

// 정책 상세 정보 조회 및 모달 열기
const openModal = async () => {
  try {
    isLoading.value = true;
    const policyId = currentPolicy.value.id;

    // API 호출하여 상세 정보 가져오기
    const detailData = await getRecommendationDetail(policyId);
    console.log(detailData);
    modalData.value = detailData;

    // 모달 열기
    isModalVisible.value = true;
  } catch (error) {
    console.error('정책 상세 정보 조회 실패:', error);
    // 에러 발생 시에도 기본 데이터로 모달 열기
    modalData.value = {
      name: currentPolicy.value.programName,
      institution: currentPolicy.value.institution,
      description: currentPolicy.value.simpleDescription,
    };
    isModalVisible.value = true;
  } finally {
    isLoading.value = false;
  }
};

// 모달 닫기
const closeModal = () => {
  isModalVisible.value = false;
  modalData.value = null;
};

// 진단 페이지로 이동 (검사 다시 하기)
const goToDiagnosis = () => {
  router.push('/diagnosis');
};
</script>

<template>
  <div :class="styles.diagnosisResultContainer">
    <!-- 상단 상태 텍스트 -->
    <div :class="styles.statusSection">
      <p :class="styles.userStatusText">
        <span :class="styles.userName">{{ diagnosisResult.nickName }}</span>
        <span :class="styles.userSuffix">님은</span>
      </p>
      <p :class="styles.diagnosisStageText">
        <span :class="styles.diagnosisStage">{{
          diagnosisResult.diagnosisStage
        }}</span>
        <span :class="styles.stageSuffix">입니다</span>
      </p>
    </div>

    <!-- 추천 정책 카드 -->
    <div :class="styles.cardSlider">
      <!-- 이전 카드 (왼쪽) -->
      <div :class="styles.sideCard" @click="goToPreviousPolicy"></div>

      <!-- 메인 카드 (중앙) -->
      <div :class="styles.resultCard" @click="openModal">
        <!-- 카드 헤더 -->
        <div :class="styles.cardHeader">
          <span :class="styles.institutionIcon">
            <img :src="currentPolicy.icon" :alt="currentPolicy.institution" />
          </span>
          <span :class="styles.institutionName">{{
            currentPolicy.institution
          }}</span>
        </div>
        <h3 :class="styles.policyName">{{ currentPolicy.programName }}</h3>

        <!-- 카드 내용 -->
        <div :class="styles.cardBody">
          <p :class="styles.policyDescription">
            {{ currentPolicy.simpleDescription }}
          </p>
        </div>

        <!-- 자세히 보기 버튼 -->
        <div :class="styles.cardFooter">
          <button :class="styles.detailButton" @click.stop="openModal">
            자세히 보기
          </button>
        </div>
      </div>

      <!-- 다음 카드 (오른쪽) -->
      <div :class="styles.sideCard" @click="goToNextPolicy"></div>
    </div>

    <!-- 하단 재진단 버튼 -->
    <div :class="styles.bottomButton">
      <button :class="styles.rediagnosisButton" @click="goToDiagnosis">
        검사 다시 하기
      </button>
    </div>

    <!-- 로딩 오버레이 -->
    <div v-if="isLoading" :class="styles.loadingOverlay">
      <div :class="styles.loadingSpinner">로딩 중...</div>
    </div>

    <!-- 진단 단계 모달 -->
    <DiagnosisStageModal
      :is-visible="isModalVisible"
      :detail-data="modalData"
      @close="closeModal"
    />
  </div>
</template>
