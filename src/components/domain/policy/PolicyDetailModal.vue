<template>
  <div v-if="isVisible" :class="styles.modalOverlay" @click="closeModal">
    <div :class="styles.modalContent" @click.stop>
      <!-- 모달 내용 -->
      <div :class="styles.modalBody">
        <!-- 닫기 버튼 -->
        <button @click="closeModal" :class="styles.backButton">
          <i class="fa fa-arrow-left"></i>
        </button>
        <!-- 부처 정보 -->
        <div :class="styles.ministryInfo">
          <span :class="styles.ministryText">
            {{ displayPolicyData.ministryName }} ·
            {{ displayPolicyData.departmentName }}
          </span>
        </div>
        <!-- 태그 -->
        <div :class="styles.tagContainer">
          <span
            v-for="tag in displayPolicyData.policyTags"
            :key="tag"
            :class="styles.tag"
          >
            {{ tag }}
          </span>
        </div>
        <!-- 제목 -->
        <h1 :class="styles.policyTitle">
          {{ displayPolicyData.policyName }}
        </h1>

        <!-- 정책 요약 -->
        <p :class="styles.policySummary">
          {{ displayPolicyData.policySummary }}
        </p>

        <!-- 지원 연령 -->
        <div :class="styles.infoSection">
          <h3 :class="styles.sectionTitle">지원 연령</h3>
          <p :class="styles.sectionContent">
            {{ displayPolicyData.supportAge || '제한없음' }}
          </p>
        </div>

        <!-- 지원 가구 -->
        <div :class="styles.infoSection">
          <h3 :class="styles.sectionTitle">지원 가구</h3>
          <p :class="styles.sectionContent">
            {{ displayPolicyData.supportTarget?.join(', ') || '해당없음' }}
          </p>
        </div>

        <!-- 지급 방식 -->
        <div :class="styles.infoSection">
          <h3 :class="styles.sectionTitle">지급 방식</h3>
          <p :class="styles.sectionContent">
            {{ displayPolicyData.supportType || '해당없음' }}
          </p>
        </div>

        <!-- 지급 주기 -->
        <div :class="styles.infoSection">
          <h3 :class="styles.sectionTitle">지급 주기</h3>
          <p :class="styles.sectionContent">
            {{ displayPolicyData.supportCycle || '해당없음' }}
          </p>
        </div>

        <!-- 신청 방법 -->
        <div :class="styles.infoSection">
          <h3 :class="styles.sectionTitle">신청 방법</h3>
          <p :class="styles.sectionContent">
            {{
              displayPolicyData.isOnlineApplyAvailable === 'Y'
                ? '온라인, 오프라인'
                : '오프라인'
            }}
          </p>
        </div>

        <!-- 문의 번호 -->
        <div :class="styles.infoSection">
          <h3 :class="styles.sectionTitle">문의 번호</h3>
          <div :class="styles.contactInfo">
            <div :class="styles.phoneIcon">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path
                  d="M5.5 6.5C5.5 7.5 6 8.5 8 10.5C10 8.5 10.5 7.5 10.5 6.5C10.5 5.67 9.83 5 9 5H7C6.17 5 5.5 5.67 5.5 6.5Z"
                  fill="#003E65"
                />
              </svg>
            </div>
            <span :class="styles.contactText">
              {{ displayPolicyData.contactNumber || '해당없음' }}
            </span>
          </div>
        </div>

        <!-- 신청하러 가기 버튼 -->
        <button :class="styles.applyButton" @click="openDetailPage">
          <span :class="styles.applyText">신청하러 가기</span>
          <div :class="styles.externalLinkIcon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path
                d="M18 13V19A2 2 0 0 1 16 21H5A2 2 0 0 1 3 19V8A2 2 0 0 1 5 6H11M15 3H21V9M10 14L21 3"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue';
import { useModalStore } from '@/stores/modal';
import styles from '@/assets/styles/components/policy/PolicyDetailModal.module.css';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false,
  },
  policyData: {
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
      modalStore.openPolicyDetailModal();
    } else {
      modalStore.closePolicyDetailModal();
    }
  }
);

// 표시할 정책 데이터
const displayPolicyData = computed(() => {
  return props.policyData || {};
});

const closeModal = () => {
  emit('close');
};

// 상세 페이지 열기
const openDetailPage = () => {
  if (displayPolicyData.value.detailPageUrl) {
    window.open(displayPolicyData.value.detailPageUrl, '_blank');
  }
};
</script>
