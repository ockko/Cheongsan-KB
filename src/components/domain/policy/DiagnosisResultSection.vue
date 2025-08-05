<script setup>
import styles from '@/assets/styles/components/policy/Policy.module.css';
import { useRouter } from 'vue-router';
import { ref, computed } from 'vue';
import DiagnosisStageModal from './DiagnosisStageModal.vue';

// Props 정의
const props = defineProps({
  diagnosisData: {
    type: Object,
    required: true,
  },
});

const router = useRouter();

// 진단 결과 데이터 (props에서 받거나 기본값 사용)
const diagnosisResult = props.diagnosisData || {
  userName: '가나다',
  diagnosisStage: '개인회생단계',
  institution: '법원',
  policyName: '개인회생',
  description:
    '법원의 허가를 받아 부채의 일부를 탕감받고 3~5년간 분할 상환하는 제도입니다.긴 문장으로 테스트를 해보고 잇씁니다. 이거는 테스트',
};

// 정책 데이터 정의
const policies = [
  {
    id: 'personal-bankruptcy',
    name: '개인파산',
    institution: '법원',
    description:
      '법원의 허가를 받아 모든 부채를 탕감받는 제도입니다.긴 문장으로 테스트를 해보고 잇씁니다.',
    icon: '/images/court.png',
  },
  {
    id: 'fast-debt-adjustment',
    name: '신속채무조정',
    institution: '법원',
    description:
      '법원의 중재를 통해 부채를 조정하는 제도입니다.긴 문장으로 테스트를 해보고 잇씁니다. ',
    icon: '/images/court.png',
  },
  {
    id: 'personal-workout',
    name: '개인워크아웃',
    institution: '법원',
    description:
      '법원의 허가를 받아 부채를 조정하는 제도입니다.긴 문장으로 테스트를 해보고 잇씁니다. ',
    icon: '/images/court.png',
  },
  {
    id: 'pre-workout',
    name: '프리워크아웃',
    institution: '법원',
    description:
      '법원의 사전 허가를 받아 부채를 조정하는 제도입니다.긴 문장으로 테스트를 해보고 잇씁니다. ',
    icon: '/images/court.png',
  },
];

// 현재 선택된 정책 인덱스
const currentPolicyIndex = ref(0);

// 현재 정책 데이터
const currentPolicy = computed(() => policies[currentPolicyIndex.value]);

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

// 모달 열기
const openModal = () => {
  isModalVisible.value = true;
};

// 모달 닫기
const closeModal = () => {
  isModalVisible.value = false;
};

// 정책 상세 페이지로 이동
const goToPolicyDetail = () => {
  router.push(`/policy-detail/${currentPolicy.value.name}`);
};
</script>

<template>
  <div :class="styles.diagnosisResultContainer">
    <!-- 상단 상태 텍스트 -->
    <div :class="styles.statusSection">
      <p :class="styles.userStatusText">
        <span :class="styles.userName">{{ diagnosisResult.userName }}</span>
        <span :class="styles.userSuffix">님은</span>
      </p>
      <p :class="styles.diagnosisStageText">
        <span :class="styles.diagnosisStage">{{
          diagnosisResult.diagnosisStage
        }}</span>
        <span :class="styles.stageSuffix">입니다</span>
      </p>
    </div>

    <!-- 카드 슬라이더 -->
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
        <h3 :class="styles.policyName">{{ currentPolicy.name }}</h3>

        <!-- 카드 내용 -->
        <div :class="styles.cardBody">
          <p :class="styles.policyDescription">
            {{ currentPolicy.description }}
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
      <button :class="styles.rediagnosisButton">검사 다시 하기</button>
    </div>

    <!-- 진단 단계 모달 -->
    <DiagnosisStageModal
      :is-visible="isModalVisible"
      :diagnosis-stage="currentPolicy.name"
      @close="closeModal"
    />
  </div>
</template>
