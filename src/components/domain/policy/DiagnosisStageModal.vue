<script setup>
import { computed, watch, onMounted, onUnmounted } from 'vue';
import { useModalStore } from '@/stores/modal';
import styles from '@/assets/styles/components/policy/DiagnosisStageModal.module.css';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false,
  },
  detailData: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['close']);

// 모달 스토어 사용
const modalStore = useModalStore();

// props.isVisible 변화를 감지하여 스토어 상태 업데이트
watch(
  () => props.isVisible,
  (newValue) => {
    if (newValue) {
      modalStore.openDiagnosisStageModal();
    } else {
      modalStore.closeDiagnosisStageModal();
    }
  },
  { immediate: true }
);

const closeModal = () => {
  emit('close');
};

// 뒤로가기 이벤트 처리
const handlePopState = () => {
  if (props.isVisible) {
    closeModal();
  }
};

// 컴포넌트 마운트 시 뒤로가기 이벤트 리스너 추가
onMounted(() => {
  window.addEventListener('popstate', handlePopState);
});

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('popstate', handlePopState);
});

// 모달이 열릴 때와 닫힐 때 history 상태 관리
watch(
  () => props.isVisible,
  (newValue) => {
    if (newValue) {
      // 모달이 열릴 때 history에 상태 추가
      window.history.pushState(
        { modal: 'diagnosisStage' },
        '',
        window.location.href
      );
    } else {
      // 모달이 닫힐 때 history 상태 정리
      if (
        window.history.state &&
        window.history.state.modal === 'diagnosisStage'
      ) {
        window.history.back();
      }
    }
  }
);

// 부처별 로고 매핑
const getMinisterLogo = (logoText) => {
  const logoMapping = {
    // 고용노동부
    고용노동부: '/minister-logos/moel.jpg',
    MOEL: '/minister-logos/moel.jpg',

    // 보건복지부
    보건복지부: '/minister-logos/mohw.jpg',
    MOHW: '/minister-logos/mohw.jpg',

    // 산업통상자원부
    산업통상자원부: '/minister-logos/motie.jpg',
    MOTIE: '/minister-logos/motie.jpg',

    // 여성가족부
    여성가족부: '/minister-logos/mogef.jpg',
    MOGEF: '/minister-logos/mogef.jpg',

    // 교육부
    교육부: '/minister-logos/moe.jpg',
    MOE: '/minister-logos/moe.jpg',

    // 통일부
    통일부: '/minister-logos/mou.jpg',
    MOU: '/minister-logos/mou.jpg',

    // 문화체육관광부
    문화체육관광부: '/minister-logos/mocst.jpg',
    MOCST: '/minister-logos/mocst.jpg',

    // 농림축산식품부
    농림축산식품부: '/minister-logos/moafra.jpg',
    MOAFRA: '/minister-logos/moafra.jpg',

    // 금융위원회
    금융위원회: '/minister-logos/fsc.jpg',
    FSC: '/minister-logos/fsc.jpg',

    // 국가보훈부
    국가보훈부: '/minister-logos/mopva.jpg',
    MOPVA: '/minister-logos/mopva.jpg',

    // 행정안전부
    행정안전부: '/minister-logos/mois.jpg',
    MOIS: '/minister-logos/mois.jpg',

    // 과학기술정보통신부
    과학기술정보통신부: '/minister-logos/mosi.jpg',
    MOSI: '/minister-logos/mosi.jpg',

    // 해양수산부
    해양수산부: '/minister-logos/moof.jpg',
    MOOF: '/minister-logos/moof.jpg',

    // 기획재정부
    기획재정부: '/minister-logos/moef.jpg',
    MOEF: '/minister-logos/moef.jpg',

    // 산림청
    산림청: '/minister-logos/kfs.jpg',
    KFS: '/minister-logos/kfs.jpg',

    // 중소벤처기업부
    중소벤처기업부: '/minister-logos/moss.jpg',
    MOSS: '/minister-logos/moss.jpg',

    // 질병관리청
    질병관리청: '/minister-logos/kdcpa.jpg',
    KDCPA: '/minister-logos/kdcpa.jpg',

    // 환경부
    환경부: '/minister-logos/moen.jpg',
    MOEN: '/minister-logos/moen.jpg',

    // 국토교통부
    국토교통부: '/minister-logos/molit.jpg',
    MOLIT: '/minister-logos/molit.jpg',

    // 기타 기관들
    서민금융진흥원: '/images/smf-logo.png',
    한국주택금융공사: '/images/hf-logo.png',
    국민연금공단: '/images/nps-logo.png',
    국민건강보험공단: '/images/nhis-logo.png',
    중소기업진흥공단: '/images/sbc-logo.png',
    한국산업기술진흥원: '/images/kiat-logo.png',
    한국장애인고용공단: '/images/kead-logo.png',
    한국여성가족재단: '/images/kwf-logo.png',
  };

  return logoMapping[logoText] || '/images/court.png';
};

// 이미지 에러 핸들링
const handleImageError = (e) => {
  console.log('이미지 로드 실패:', e.target.src);
  // 대체 이미지나 텍스트로 표시
  e.target.style.display = 'none';
};

// 대상자 목록 생성 (eligibleDebtors와 eligibleDebts 결합)
const targetList = computed(() => {
  const targets = [];

  // eligibleDebtors 처리
  if (props.detailData?.eligibleDebtors) {
    if (Array.isArray(props.detailData.eligibleDebtors)) {
      targets.push(...props.detailData.eligibleDebtors);
    } else if (typeof props.detailData.eligibleDebtors === 'string') {
      // 문자열인 경우 쉼표로 분리
      const debtors = props.detailData.eligibleDebtors
        .split(',')
        .map((item) => item.trim());
      targets.push(...debtors);
    }
  }

  // eligibleDebts 처리
  if (props.detailData?.eligibleDebts) {
    if (Array.isArray(props.detailData.eligibleDebts)) {
      targets.push(...props.detailData.eligibleDebts);
    } else if (typeof props.detailData.eligibleDebts === 'string') {
      // 문자열인 경우 쉼표로 분리
      const debts = props.detailData.eligibleDebts
        .split(',')
        .map((item) => item.trim());
      targets.push(...debts);
    }
  }

  return targets.length > 0 ? targets : ['대상자 정보가 없습니다.'];
});

// 장점 목록 생성
const advantagesList = computed(() => {
  if (props.detailData?.advantages) {
    if (Array.isArray(props.detailData.advantages)) {
      return props.detailData.advantages;
    } else if (typeof props.detailData.advantages === 'string') {
      // 문자열인 경우 쉼표로 분리
      return props.detailData.advantages.split(',').map((item) => item.trim());
    }
  }
  return [];
});

// 주의사항 목록 생성
const cautionsList = computed(() => {
  if (props.detailData?.cautions) {
    if (Array.isArray(props.detailData.cautions)) {
      return props.detailData.cautions;
    } else if (typeof props.detailData.cautions === 'string') {
      // 문자열인 경우 쉼표로 분리
      return props.detailData.cautions.split(',').map((item) => item.trim());
    }
  }
  return [];
});
</script>

<template>
  <div
    v-if="isVisible"
    :class="styles.diagnosisStageModalOverlay"
    @click="closeModal"
  >
    <div :class="styles.diagnosisStageModalContent" @click.stop>
      <!-- 모달 내용 -->
      <div :class="styles.diagnosisStageModalBody">
        <!-- 뒤로가기 버튼 -->
        <button @click="closeModal" :class="styles.diagnosisStageBackButton">
          <i class="fa fa-arrow-left"></i>
        </button>

        <!-- 제목 섹션 -->
        <div :class="styles.diagnosisStageTitleSection">
          <div :class="styles.diagnosisStageInstitutionInfo">
            <div :class="styles.diagnosisStageInstitutionIcon">
              <img src="/images/court.png" />
            </div>
            <span :class="styles.diagnosisStageInstitutionName">{{
              props.detailData?.institutionName
            }}</span>
          </div>
          <h1 :class="styles.diagnosisStageStageTitle">
            {{ props.detailData?.programName }}
          </h1>
          <p :class="styles.diagnosisStageStageDescription">
            {{ props.detailData?.description }}
          </p>
        </div>

        <!-- 대상자 섹션 -->
        <div :class="styles.diagnosisStageSection">
          <div :class="styles.diagnosisStageDivider"></div>
          <h2 :class="styles.diagnosisStageSectionTitle">대상자</h2>
          <ul :class="styles.diagnosisStageSectionList">
            <li
              v-for="(item, index) in targetList"
              :key="`target-${index}`"
              :class="styles.diagnosisStageListItem"
            >
              <div :class="styles.diagnosisStageForListContent">
                <span :class="styles.diagnosisStageInfoIcon">
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <circle
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="#003E65"
                      stroke-width="2"
                    />
                    <path
                      d="M12 16V12"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                    <path
                      d="M12 8H12.01"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.diagnosisStageListText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 장점 섹션 -->
        <div :class="styles.diagnosisStageSection">
          <div :class="styles.diagnosisStageDivider"></div>
          <h2 :class="styles.diagnosisStageSectionTitle">장점</h2>
          <ul :class="styles.diagnosisStageSectionList">
            <li
              v-for="(item, index) in advantagesList"
              :key="`advantage-${index}`"
              :class="styles.diagnosisStageListItem"
            >
              <div :class="styles.diagnosisStageAdvantagesListContent">
                <span :class="styles.diagnosisStageCheckIcon">
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M20 6L9 17L4 12"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.diagnosisStageListText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 주의사항 섹션 -->
        <div :class="styles.diagnosisStageSection">
          <div :class="styles.diagnosisStageDivider"></div>
          <h2 :class="styles.diagnosisStageSectionTitle">주의사항</h2>
          <ul :class="styles.diagnosisStageSectionList">
            <li
              v-for="(item, index) in cautionsList"
              :key="`warning-${index}`"
              :class="styles.diagnosisStageListItem"
            >
              <div :class="styles.diagnosisStageWarnListContent">
                <span :class="styles.diagnosisStageWarningIcon">
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M10.29 3.86L1.82 18A2 2 0 0 0 3.54 21H20.46A2 2 0 0 0 22.18 18L13.71 3.86A2 2 0 0 0 10.29 3.86Z"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                    <path
                      d="M12 9V13"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                    <path
                      d="M12 17H12.01"
                      stroke="#003E65"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.diagnosisStageListText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
