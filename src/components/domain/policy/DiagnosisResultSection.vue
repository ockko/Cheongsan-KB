<script setup>
import styles from '@/assets/styles/components/policy/DiagnosisResultSection.module.css';
import { useRouter } from 'vue-router';
import { ref, computed, onMounted, onUnmounted } from 'vue';
import DiagnosisStageModal from './DiagnosisStageModal.vue';
import { getRecommendationDetail } from '@/api/diagnosis.js';
import { useModalStore } from '@/stores/modal';

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
    id: '5',
    programName: '신속채무조정',
    operatingEntity: '신속채무조정',
    institution: '신용회복위원회',
    simpleDescription:
      '연체 30일 이하의 단기 연체자나 연체 위기에 놓인 사람들이 채무를 정상적으로 상환하기 어려울 때, 일정 기간 상환을 유예하거나 상환 기간을 연장해주는 제도',
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

// 현재 선택된 정책 인덱스 (1부터 시작)
const currentPolicyIndex = ref(1);

// 현재 정책 데이터 (인덱스 기반)
const currentPolicy = computed(() => {
  const actualIndex = currentPolicyIndex.value - 1; // 1-based를 0-based로 변환
  if (actualIndex >= 0 && actualIndex < policies.length) {
    return policies[actualIndex];
  }
  return policies[0]; // 기본값
});

// 초기 인덱스 설정 (서버 데이터와 매칭되는 정책으로 시작)
const initializePolicyIndex = () => {
  if (props.diagnosisData && props.diagnosisData.id) {
    const matchingIndex = policies.findIndex(
      (policy) => policy.id === props.diagnosisData.id.toString()
    );
    if (matchingIndex !== -1) {
      currentPolicyIndex.value = matchingIndex + 1; // 0-based를 1-based로 변환
    }
  }
};

// 스와이프 관련 상태
const touchStartX = ref(0);
const touchEndX = ref(0);
const isDragging = ref(false);
const isTransitioning = ref(false);
const slideWidth = ref(0);
const carouselRef = ref(null);

// 슬라이드 위치를 computed로 변경하여 반응성 향상
const slidePosition = computed(() => {
  const position = -(currentPolicyIndex.value - 3) * slideWidth.value;
  return position;
});

// translateX 변수 추가 (템플릿에서 사용)
const translateX = computed(() => {
  return slidePosition.value;
});

// 슬라이드 위치 업데이트 함수 제거 (computed로 대체됨)

// 터치 시작 이벤트
const handleTouchStart = (event) => {
  touchStartX.value = event.touches[0].clientX;
  touchEndX.value = event.touches[0].clientX; // 초기값 설정
  isDragging.value = false;
};

// 터치 이동 이벤트
const handleTouchMove = (event) => {
  if (!touchStartX.value) return;

  touchEndX.value = event.touches[0].clientX;
  const deltaX = touchEndX.value - touchStartX.value;

  // 수평 스와이프가 수직 스와이프보다 클 때만 드래그로 인식
  if (Math.abs(deltaX) > 10) {
    isDragging.value = true;
  }
};

// 터치 종료 이벤트
const handleTouchEnd = () => {
  if (!isDragging.value) return;

  const deltaX = touchEndX.value - touchStartX.value;
  const threshold = 50; // 스와이프 감지 임계값

  if (Math.abs(deltaX) > threshold) {
    if (deltaX > 0) {
      // 오른쪽으로 스와이프 - 이전 정책 (왼쪽으로 이동)
      goToPreviousPolicy();
    } else {
      // 왼쪽으로 스와이프 - 다음 정책 (오른쪽으로 이동)
      goToNextPolicy();
    }
  }

  // 상태 초기화
  touchStartX.value = 0;
  touchEndX.value = 0;
  isDragging.value = false;
};

// 마우스 이벤트 (데스크톱 지원)
const startDrag = (event) => {
  if (event.type === 'mousedown') {
    touchStartX.value = event.clientX;
    touchEndX.value = event.clientX;
  } else if (event.type === 'touchstart') {
    touchStartX.value = event.touches[0].clientX;
    touchEndX.value = event.touches[0].clientX;
  }
  isDragging.value = false;
};

const onDrag = (event) => {
  if (!touchStartX.value) return;

  if (event.type === 'mousemove') {
    touchEndX.value = event.clientX;
  } else if (event.type === 'touchmove') {
    touchEndX.value = event.touches[0].clientX;
  }

  const deltaX = touchEndX.value - touchStartX.value;

  if (Math.abs(deltaX) > 10) {
    isDragging.value = true;
  }
};

const endDrag = (event) => {
  if (!isDragging.value) return;

  const deltaX = touchEndX.value - touchStartX.value;
  const threshold = 50;

  if (Math.abs(deltaX) > threshold) {
    if (deltaX > 0) {
      // 오른쪽으로 드래그 - 이전 정책 (왼쪽으로 이동)
      goToPreviousPolicy();
    } else {
      // 왼쪽으로 드래그 - 다음 정책 (오른쪽으로 이동)
      goToNextPolicy();
    }
  }

  // 상태 초기화
  touchStartX.value = 0;
  touchEndX.value = 0;
  isDragging.value = false;
};

// 이전 정책으로 이동 (무한 루프)
const goToPreviousPolicy = () => {
  if (isTransitioning.value) return;
  isTransitioning.value = true;

  if (currentPolicyIndex.value === 1) {
    // 첫 번째 실제 카드
    currentPolicyIndex.value = policies.length; // 마지막 실제 카드로 이동
  } else {
    currentPolicyIndex.value--;
  }

  console.log(
    '이전 정책으로 이동:',
    currentPolicyIndex.value,
    '정책:',
    currentPolicy.value.programName
  );

  // 트랜지션 완료 후 상태 초기화 (CSS 트랜지션 시간과 일치)
  setTimeout(() => {
    isTransitioning.value = false;
  }, 300);
};

// 다음 정책으로 이동 (무한 루프)
const goToNextPolicy = () => {
  if (isTransitioning.value) return;
  isTransitioning.value = true;

  if (currentPolicyIndex.value === policies.length) {
    // 마지막 실제 카드
    currentPolicyIndex.value = 1; // 첫 번째 실제 카드로 이동
  } else {
    currentPolicyIndex.value++;
  }

  console.log(
    '다음 정책으로 이동:',
    currentPolicyIndex.value,
    '정책:',
    currentPolicy.value.programName
  );

  // 트랜지션 완료 후 상태 초기화 (CSS 트랜지션 시간과 일치)
  setTimeout(() => {
    isTransitioning.value = false;
  }, 300);
};

// 특정 정책으로 이동
const goToPolicy = (index) => {
  if (isTransitioning.value) return;
  isTransitioning.value = true;
  currentPolicyIndex.value = index + 1; // 0-based index를 1-based로 변환

  console.log(
    '특정 정책으로 이동:',
    currentPolicyIndex.value,
    '정책:',
    currentPolicy.value.programName
  );

  // 트랜지션 완료 후 상태 초기화 (CSS 트랜지션 시간과 일치)
  setTimeout(() => {
    isTransitioning.value = false;
  }, 300);
};

// 슬라이드 위치 업데이트 (제거 - computed로 대체)

// 리사이즈 이벤트 핸들러 함수
const handleResize = () => {
  slideWidth.value = 210 + 20;
  // updateSlidePosition() 호출 제거 - computed가 자동으로 처리
};

// 컴포넌트 마운트 시 초기 인덱스 설정 및 슬라이드 너비 계산
onMounted(() => {
  // 슬라이드 너비 계산 (카드 너비 + 간격)
  slideWidth.value = 210 + 20; // 카드 너비(210px) + 간격(20px)

  console.log('초기 슬라이드 너비 설정:', slideWidth.value);

  // 초기 인덱스 설정 (슬라이드 너비가 설정된 후에)
  initializePolicyIndex();

  console.log('초기 정책 인덱스 설정:', currentPolicyIndex.value);
  console.log('초기 슬라이드 위치:', slidePosition.value);

  // 윈도우 크기 변경 시 슬라이드 너비 재계산
  window.addEventListener('resize', handleResize);
});

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});

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
  // 모달 스토어 상태도 명시적으로 업데이트
  const modalStore = useModalStore();
  modalStore.closeDiagnosisStageModal();
};

// 진단 페이지로 이동 (검사 다시 하기)
const goToDiagnosis = () => {
  router.push('/diagnosis');
};

// 스와이프 진행률 표시
const swipeProgress = computed(() => {
  if (isDragging.value && touchStartX.value && touchEndX.value) {
    return `${Math.min(
      (Math.abs(touchEndX.value - touchStartX.value) / 100) * 100,
      100
    )}%`;
  }
  return '0%';
});

// 페이지 인디케이터
const pageIndicator = computed(() => {
  return policies.map((_, index) => ({
    index,
    isActive: index === currentPolicyIndex.value - 1, // 1-based를 0-based로 변환
  }));
});

// 템플릿에서 사용하는 변수들
const pageIndicatorItems = computed(() => {
  return policies.map((_, index) => ({
    page: index + 1,
    isActive: index === currentPolicyIndex.value - 1,
  }));
});

// 스와이프 힌트 표시
const showLeftHint = computed(() => {
  return currentPolicyIndex.value > 1;
});

const showRightHint = computed(() => {
  return currentPolicyIndex.value < policies.length;
});

// 정책 선택 함수
const selectPolicy = (index) => {
  goToPolicy(index);
};

// 페이지 이동 함수
const goToPage = (page) => {
  goToPolicy(page);
};

// 트랜지션 종료 시 처리 함수 제거 (setTimeout으로 대체됨)
</script>

<template>
  <div :class="styles.diagnosisResultContainer">
    <!-- 상단 상태 텍스트 -->
    <div :class="styles.diagnosisResultStatusSection">
      <p :class="styles.diagnosisResultUserStatusText">
        <span :class="styles.diagnosisResultUserName">{{
          diagnosisResult.nickName
        }}</span>
        <span :class="styles.diagnosisResultUserSuffix">님은</span>
      </p>
      <p :class="styles.diagnosisResultDiagnosisStageText">
        <span :class="styles.diagnosisResultDiagnosisStage">{{
          diagnosisResult.diagnosisStage
        }}</span>
        <span :class="styles.diagnosisResultStageSuffix">입니다</span>
      </p>
    </div>

    <!-- 카드 슬라이더 -->
    <div
      :class="[
        styles.diagnosisResultCardSlider,
        { [styles.diagnosisResultSwiping]: isDragging },
      ]"
      @mousedown="startDrag"
      @mousemove="onDrag"
      @mouseup="endDrag"
      @mouseleave="endDrag"
      @touchstart="startDrag"
      @touchmove="onDrag"
      @touchend="endDrag"
    >
      <!-- 슬라이드 컨테이너 -->
      <div
        :class="styles.diagnosisResultSlideContainer"
        :style="{ transform: `translateX(${translateX}px)` }"
      >
        <!-- 정책 카드들 -->
        <div
          v-for="(policy, index) in policies"
          :key="policy.id"
          :class="[
            styles.diagnosisResultResultCard,
            {
              [styles.diagnosisResultActiveCard]:
                index === currentPolicyIndex.value - 1,
            },
          ]"
          @click="selectPolicy(index + 1)"
        >
          <div :class="styles.diagnosisResultCardHeader">
            <span :class="styles.diagnosisResultInstitutionIcon">
              <img :src="policy.icon" :alt="policy.institution" />
            </span>
            <span :class="styles.diagnosisResultInstitutionName">{{
              policy.institution
            }}</span>
          </div>
          <h3 :class="styles.diagnosisResultPolicyName">
            {{ policy.programName }}
          </h3>
          <div :class="styles.diagnosisResultCardBody">
            <p :class="styles.diagnosisResultPolicyDescription">
              {{ policy.simpleDescription }}
            </p>
          </div>
          <div :class="styles.diagnosisResultCardFooter">
            <button
              :class="styles.diagnosisResultDetailButton"
              @click.stop="openModal"
            >
              자세히 보기
            </button>
          </div>
        </div>
      </div>

      <!-- 스와이프 진행률 표시 -->
      <div :class="styles.diagnosisResultSwipeProgress">
        <div
          :class="styles.diagnosisResultSwipeProgressBar"
          :style="{ width: `${swipeProgress}%` }"
        ></div>
      </div>

      <!-- 페이지 인디케이터 -->
      <div :class="styles.diagnosisResultPageIndicator">
        <div
          v-for="(item, index) in pageIndicatorItems"
          :key="index"
          :class="[
            styles.diagnosisResultPageDot,
            { [styles.diagnosisResultActiveDot]: item.isActive },
          ]"
          @click="goToPage(item.page)"
        ></div>
      </div>
    </div>

    <!-- 하단 버튼 -->
    <div :class="styles.diagnosisResultBottomButton">
      <button
        :class="styles.diagnosisResultRediagnosisButton"
        @click="goToDiagnosis"
      >
        다시 진단하기
      </button>
    </div>
  </div>

  <!-- 로딩 오버레이 -->
  <div v-if="isLoading" :class="styles.diagnosisResultLoadingOverlay">
    <div :class="styles.diagnosisResultLoadingSpinner">로딩 중...</div>
  </div>

  <!-- 진단 단계 모달 -->
  <DiagnosisStageModal
    :is-visible="isModalVisible"
    :detail-data="modalData"
    @close="closeModal"
  />
</template>
