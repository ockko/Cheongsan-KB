<script setup>
import { computed, watch, onMounted, onUnmounted } from 'vue';
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
  },
  { immediate: true }
);

// 표시할 정책 데이터
const displayPolicyData = computed(() => {
  console.log('PolicyDetailModal - 받은 정책 데이터:', props.policyData);

  if (!props.policyData || Object.keys(props.policyData).length === 0) {
    console.log('정책 데이터가 없습니다.');
    return {};
  }

  // API 응답 데이터 구조에 맞게 필드 매핑
  return {
    ministryName:
      props.policyData.ministryName ||
      props.policyData.ministry ||
      props.policyData.operatingEntity ||
      '기관명 없음',
    departmentName:
      props.policyData.departmentName || props.policyData.department || '',
    tags:
      props.policyData.policyTags || // API에서 받는 실제 필드명
      props.policyData.tags ||
      props.policyData.tagList ||
      props.policyData.categories ||
      [],
    policyName:
      props.policyData.policyName ||
      props.policyData.name ||
      props.policyData.programName ||
      '정책명 없음',
    policySummary:
      props.policyData.policySummary ||
      props.policyData.description ||
      props.policyData.simpleDescription ||
      props.policyData.content ||
      '설명 없음',
    supportAge:
      props.policyData.supportAge ||
      props.policyData.age ||
      props.policyData.ageRange ||
      '제한 없음',
    supportTarget:
      props.policyData.supportTarget || // API에서 받는 실제 필드명
      props.policyData.supportHousehold ||
      props.policyData.household ||
      props.policyData.targetHousehold ||
      '제한 없음',
    supportType:
      props.policyData.supportType || // API에서 받는 실제 필드명
      props.policyData.paymentMethod ||
      props.policyData.method ||
      props.policyData.supportMethod ||
      '해당 없음',
    supportCycle:
      props.policyData.supportCycle || // API에서 받는 실제 필드명
      props.policyData.paymentCycle ||
      props.policyData.cycle ||
      '해당 없음',
    isOnlineApplyAvailable:
      props.policyData.isOnlineApplyAvailable || // API에서 받는 실제 필드명
      'N',
    applicationMethod:
      props.policyData.applicationMethod ||
      props.policyData.application ||
      props.policyData.applyMethod ||
      '해당 없음',
    contactNumber:
      props.policyData.contactNumber ||
      props.policyData.contact ||
      props.policyData.phone ||
      props.policyData.telephone ||
      '문의 번호 없음',
    detailPageUrl:
      props.policyData.detailPageUrl ||
      props.policyData.url ||
      props.policyData.link ||
      props.policyData.homepage ||
      null,
    // 추가 필드들
    policyId:
      props.policyData.policyId || // API에서 받는 실제 필드명
      '',
    policyNumber:
      props.policyData.policyNumber || // API에서 받는 실제 필드명
      '',
  };
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
    try {
      if (newValue) {
        // 모달이 열릴 때 history에 상태 추가
        const currentUrl = window.location.href;
        if (currentUrl && currentUrl !== 'undefined') {
          window.history.pushState({ modal: 'policyDetail' }, '', currentUrl);
        }
      } else {
        // 모달이 닫힐 때 history 상태 정리
        if (
          window.history.state &&
          window.history.state.modal === 'policyDetail'
        ) {
          window.history.back();
        }
      }
    } catch (error) {
      console.error('History state management error:', error);
    }
  }
);
</script>

<template>
  <div
    v-if="isVisible"
    :class="styles.policyDetailModalOverlay"
    @click="closeModal"
  >
    <div :class="styles.policyDetailModalContent" @click.stop>
      <!-- 모달 내용 -->
      <div :class="styles.policyDetailModalBody">
        <!-- 닫기 버튼 -->
        <button @click="closeModal" :class="styles.policyDetailBackButton">
          <i class="fa fa-arrow-left"></i>
        </button>

        <!-- 부처 정보 -->
        <div :class="styles.policyDetailMinistryInfo">
          <span :class="styles.policyDetailMinistryText">
            {{ displayPolicyData.ministryName }} ·
            {{ displayPolicyData.departmentName }}
          </span>
        </div>

        <!-- 태그들 -->
        <div :class="styles.policyDetailTagContainer">
          <span
            v-for="tag in displayPolicyData.tags"
            :key="tag"
            :class="styles.policyDetailTag"
          >
            {{ tag }}
          </span>
        </div>

        <!-- 정책 제목 -->
        <h1 :class="styles.policyDetailPolicyTitle">
          {{ displayPolicyData.policyName }}
        </h1>

        <!-- 정책 요약 -->
        <p :class="styles.policyDetailPolicySummary">
          {{ displayPolicyData.policySummary }}
        </p>

        <!-- 지원 연령 -->
        <div :class="styles.policyDetailInfoSection">
          <h3 :class="styles.policyDetailSectionTitle">지원 연령</h3>
          <p :class="styles.policyDetailSectionContent">
            {{ displayPolicyData.supportAge || '제한 없음' }}
          </p>
        </div>

        <!-- 지원 가구 -->
        <div :class="styles.policyDetailInfoSection">
          <h3 :class="styles.policyDetailSectionTitle">지원 대상</h3>
          <p :class="styles.policyDetailSectionContent">
            {{
              displayPolicyData.supportTarget &&
              displayPolicyData.supportTarget.length > 0
                ? displayPolicyData.supportTarget.join(', ')
                : '제한 없음'
            }}
          </p>
        </div>

        <!-- 지원 유형 -->
        <div :class="styles.policyDetailInfoSection">
          <h3 :class="styles.policyDetailSectionTitle">지원 유형</h3>
          <p :class="styles.policyDetailSectionContent">
            {{ displayPolicyData.supportType || '해당 없음' }}
          </p>
        </div>

        <!-- 온라인 신청 가능 여부 -->
        <div :class="styles.policyDetailInfoSection">
          <h3 :class="styles.policyDetailSectionTitle">신청 방법</h3>
          <p :class="styles.policyDetailSectionContent">
            {{
              displayPolicyData.isOnlineApplyAvailable === 'Y'
                ? '온라인 신청 가능'
                : '오프라인 신청만 가능'
            }}
          </p>
        </div>

        <!-- 지급 주기 -->
        <div :class="styles.policyDetailInfoSection">
          <h3 :class="styles.policyDetailSectionTitle">지급 주기</h3>
          <p :class="styles.policyDetailSectionContent">
            {{ displayPolicyData.supportCycle || '해당 없음' }}
          </p>
        </div>

        <!-- 문의 번호 -->
        <div :class="styles.policyDetailInfoSection">
          <h3 :class="styles.policyDetailSectionTitle">문의 번호</h3>
          <div :class="styles.policyDetailContactInfo">
            <div :class="styles.policyDetailPhoneIcon">
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"
                  fill="currentColor"
                />
              </svg>
            </div>
            <span :class="styles.policyDetailContactText">
              {{ displayPolicyData.contactNumber || '문의 번호 없음' }}
            </span>
          </div>
        </div>

        <!-- 신청하러 가기 버튼 -->
        <button
          v-if="displayPolicyData.detailPageUrl"
          :class="styles.policyDetailApplyButton"
          @click="openDetailPage"
        >
          <span :class="styles.policyDetailApplyText">신청하러 가기</span>
          <div :class="styles.policyDetailExternalLinkIcon">
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

        <!-- 상세 페이지 URL이 없을 때 표시할 메시지 -->
        <div
          v-else
          :class="styles.policyDetailModalInfoSection"
          style="text-align: center; color: #666"
        >
          상세 정보는 해당 기관에 직접 문의해주세요.
        </div>
      </div>
    </div>
  </div>
</template>
