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
      window.history.pushState({ modal: 'diagnosisStage' }, '', window.location.href);
    } else {
      // 모달이 닫힐 때 history 상태 정리
      if (window.history.state && window.history.state.modal === 'diagnosisStage') {
        window.history.back();
      }
    }
  }
);

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
  <div v-if="isVisible" :class="styles.modalOverlay" @click="closeModal">
    <div :class="styles.modalContent" @click.stop>
      <!-- 모달 내용 -->
      <div :class="styles.modalBody">
        <!-- 닫기 버튼 -->
        <button @click="closeModal" :class="styles.backButton">
          <i class="fa fa-arrow-left"></i>
        </button>
        <!-- 제목 섹션 -->
        <div :class="styles.titleSection">
          <div :class="styles.institutionInfo">
            <div :class="styles.institutionIcon">
              <img
                src="/images/court2.png"
                alt="법원"
                @error="handleImageError"
              />
            </div>
            <span :class="styles.institutionName">{{
              detailData?.operatingEntity || '관련 기관'
            }}</span>
          </div>
          <h1 :class="styles.stageTitle">
            {{ detailData?.programName || '제도명' }}
          </h1>
          <p :class="styles.stageDescription">
            {{ detailData?.simpleDescription || '제도 설명' }}
          </p>
        </div>

        <!-- 대상자 섹션 -->
        <div :class="styles.section">
          <!-- 구분선 -->
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">대상자</h2>
          <ul :class="styles.sectionList">
            <li
              v-for="(item, index) in targetList"
              :key="index"
              :class="styles.listItem"
            >
              <div :class="styles.forListContent">
                <span :class="styles.infoIcon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="8" r="8" fill="#007BFF" />
                    <path
                      d="M8 4V8M8 12H8.01"
                      stroke="white"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  </svg>
                </span>
                <span :class="styles.listText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 장점 섹션 -->
        <div :class="styles.section">
          <!-- 구분선 -->
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">장점</h2>
          <ul :class="styles.sectionList">
            <li
              v-for="(item, index) in advantagesList"
              :key="index"
              :class="styles.listItem"
            >
              <div :class="styles.advantagesListContent">
                <span :class="styles.checkIcon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="8" r="8" fill="#28A745" />
                    <path
                      d="M6 8L7.5 9.5L10 7"
                      stroke="white"
                      stroke-width="1.5"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.listText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 주의사항 섹션 -->
        <div :class="styles.section">
          <!-- 구분선 -->
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">주의사항</h2>
          <ul :class="styles.sectionList">
            <li
              v-for="(item, index) in cautionsList"
              :key="index"
              :class="styles.listItem"
            >
              <div :class="styles.warnListContent">
                <span :class="styles.warningIcon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 1L15 14H1L8 1Z" fill="#DC3545" />
                    <path
                      d="M8 6V9M8 12H8.01"
                      stroke="white"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  </svg>
                </span>
                <span :class="styles.listText">{{ item }}</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
