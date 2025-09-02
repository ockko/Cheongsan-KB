<script setup>
import styles from '@/assets/styles/components/policy/PolicySection.module.css';
import { useRouter } from 'vue-router';
import { ref, onMounted, computed } from 'vue';
import PolicyDetailModal from './PolicyDetailModal.vue';
import {
  getCustomPolicies,
  searchCustomPolicies,
  getPolicyDetail,
} from '@/api/policy.js';
import { useAuthStore } from '@/stores/auth';
import { useModalStore } from '@/stores/modal';

const router = useRouter();
const authStore = useAuthStore();

// 모달 상태 관리
const isModalVisible = ref(false);
const selectedPolicyData = ref({});
const isLoading = ref(false);

// 정책 데이터 상태 관리
const policies = ref([]);
const isPoliciesLoading = ref(false);
const searchKeyword = ref('');
const isSearching = ref(false);
const hasSearched = ref(false); // 검색 실행 여부 추적

// 페이지네이션 상태 관리
const currentPage = ref(1);
const itemsPerPage = 5;

// 맞춤 지원 정책 데이터 로드
const loadCustomPolicies = async () => {
  try {
    isPoliciesLoading.value = true;

    const data = await getCustomPolicies();
    policies.value = data;
  } catch (error) {
    console.error('맞춤 지원 정책 데이터 로드 실패:', error);
    // 에러 발생 시 빈 배열로 설정
    policies.value = [];
  } finally {
    isPoliciesLoading.value = false;
  }
};

// 검색 기능
const handleSearch = async () => {
  try {
    isSearching.value = true;
    hasSearched.value = true; // 검색 실행 표시
    activeCategory.value = '전체'; // 검색 시 카테고리 초기화
    currentPage.value = 1; // 검색 시 첫 페이지로 초기화

    // 검색 API 호출 (빈 검색어도 허용)
    const searchResults = await searchCustomPolicies(
      searchKeyword.value.trim() || ''
    );

    if (searchResults && searchResults.length > 0) {
      policies.value = searchResults;
    } else {
      policies.value = [];
      console.log('검색 결과가 없습니다.');
    }
  } catch (error) {
    console.error('검색 실패:', error);
    // 검색 실패 시 기존 정책 목록 유지
  } finally {
    isSearching.value = false;
  }
};

// 검색어 초기화 및 전체 정책 목록 로드
const clearSearch = async () => {
  searchKeyword.value = '';
  hasSearched.value = false; // 검색 상태 초기화
  activeCategory.value = '전체'; // 카테고리도 초기화
  currentPage.value = 1; // 첫 페이지로 초기화
  await loadCustomPolicies();
};

// 정책 상세 정보 조회 및 모달 열기
const openPolicyDetail = async (policy) => {
  try {
    isLoading.value = true;

    // 정책 이름으로 상세 정보 조회
    const policyDetail = await getPolicyDetail(policy.policyName);
    selectedPolicyData.value = policyDetail;
    isModalVisible.value = true;
  } catch (error) {
    console.error('정책 상세 정보 조회 실패:', error);
  } finally {
    isLoading.value = false;
  }
};

// 모달 닫기
const closeModal = () => {
  isModalVisible.value = false;
  selectedPolicyData.value = {};
  // 모달 스토어 상태도 명시적으로 업데이트
  const modalStore = useModalStore();
  modalStore.closePolicyDetailModal();
};

const goToPolicyDetail = (policyId) => {
  // policyId 유효성 검사
  if (policyId && typeof policyId === 'string' && policyId !== 'undefined') {
    try {
      router.push(`/policy-detail/${policyId}`);
    } catch (error) {
      console.error('Policy detail navigation error:', error);
      // 에러 발생 시 정책 목록 페이지로 이동
      router.push('/policy');
    }
  } else {
    console.error('Invalid policyId:', policyId);
    // 잘못된 policyId일 경우 정책 목록 페이지로 이동
    router.push('/policy');
  }
};

// 카테고리 필터 상태 관리
const activeCategory = ref('전체');

const categories = [
  { id: '전체', name: '전체' },
  { id: '주거', name: '주거' },
  { id: '보호·돌봄', name: '보호·돌봄' },
  { id: '생활지원', name: '생활지원' },
];

// 부처별 로고 매핑
const getMinisterLogo = (logoText) => {
  const logoMapping = {
    // 고용노동부
    고용노동부: '/images/Korea-logo.png',
    MOEL: '/images/Korea-logo.png',

    // 보건복지부
    보건복지부: '/images/Korea-logo.png',
    MOHW: '/images/Korea-logo.png',

    // 산업통상자원부
    산업통상자원부: '/images/Korea-logo.png',
    MOTIE: '/images/Korea-logo.png',

    // 여성가족부
    여성가족부: '/images/Korea-logo.png',
    MOGEF: '/images/Korea-logo.png',

    // 교육부
    교육부: '/images/Korea-logo.png',
    MOE: '/images/Korea-logo.png',

    // 통일부
    통일부: '/images/Korea-logo.png',
    MOU: '/images/Korea-logo.png',

    // 문화체육관광부
    문화체육관광부: '/images/Korea-logo.png',
    MOCST: '/images/Korea-logo.png',

    // 농림축산식품부
    농림축산식품부: '/images/Korea-logo.png',
    MOAFRA: '/images/Korea-logo.png',

    // 금융위원회
    금융위원회: '/images/Korea-logo.png',
    FSC: '/images/Korea-logo.png',

    // 국가보훈부
    국가보훈부: '/images/Korea-logo.png',
    MOPVA: '/images/Korea-logo.png',

    // 행정안전부
    행정안전부: '/images/Korea-logo.png',
    MOIS: '/images/Korea-logo.png',

    // 과학기술정보통신부
    과학기술정보통신부: '/images/Korea-logo.png',
    MOSI: '/images/Korea-logo.png',

    // 해양수산부
    해양수산부: '/images/Korea-logo.png',
    MOOF: '/images/Korea-logo.png',

    // 기획재정부
    기획재정부: '/images/Korea-logo.png',
    MOEF: '/images/Korea-logo.png',

    // 산림청
    산림청: '/images/Korea-logo.png',
    KFS: '/images/Korea-logo.png',

    // 중소벤처기업부
    중소벤처기업부: '/images/Korea-logo.png',
    MOSS: '/images/Korea-logo.png',

    // 질병관리청
    질병관리청: '/images/Korea-logo.png',
    KDCPA: '/images/Korea-logo.png',

    // 환경부
    환경부: '/images/Korea-logo.png',
    MOEN: '/images/Korea-logo.png',

    // 국토교통부
    국토교통부: '/images/Korea-logo.png',
    MOLIT: '/images/Korea-logo.png',

    // 기타 기관들 (기존)
    서민금융진흥원: '/images/Korea-logo.png',
    한국주택금융공사: '/images/Korea-logo.png',
    국민연금공단: '/images/Korea-logo.png',
    국민건강보험공단: '/images/Korea-logo.png',
    중소기업진흥공단: '/images/Korea-logo.png',
    한국산업기술진흥원: '/images/Korea-logo.png',
    한국장애인고용공단: '/images/Korea-logo.png',
    한국여성가족재단: '/images/Korea-logo.png',
  };

  return logoMapping[logoText] || '/images/Korea-logo.png';
};

const selectCategory = (categoryId) => {
  activeCategory.value = categoryId;
  currentPage.value = 1; // 카테고리 변경 시 첫 페이지로 이동
  // 카테고리 변경 시에는 검색 상태를 유지하고 클라이언트 사이드 필터링만 적용
};

// 필터링된 정책 목록 계산
const filteredPolicies = computed(() => {
  let filtered = policies.value;

  // 카테고리 필터링만 적용 (검색은 API에서 처리)
  if (activeCategory.value !== '전체') {
    filtered = filtered.filter(
      (policy) =>
        policy.tagList && policy.tagList.includes(activeCategory.value)
    );
  }

  return filtered;
});

// 페이지네이션된 정책 목록 계산
const paginatedPolicies = computed(() => {
  const startIndex = (currentPage.value - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  return filteredPolicies.value.slice(startIndex, endIndex);
});

// 전체 페이지 수 계산
const totalPages = computed(() => {
  return Math.ceil(filteredPolicies.value.length / itemsPerPage);
});

// 페이지 변경 함수
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

// 이전 페이지로 이동
const goToPreviousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

// 다음 페이지로 이동
const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

// 페이지 번호 배열 생성 (최대 5개 표시)
const pageNumbers = computed(() => {
  const pages = [];
  const maxVisiblePages = 5;

  if (totalPages.value <= maxVisiblePages) {
    // 전체 페이지가 5개 이하면 모든 페이지 표시
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    // 현재 페이지를 중심으로 최대 5개 페이지 표시
    let start = Math.max(
      1,
      currentPage.value - Math.floor(maxVisiblePages / 2)
    );
    let end = Math.min(totalPages.value, start + maxVisiblePages - 1);

    // 끝에 도달했을 때 시작점 조정
    if (end === totalPages.value) {
      start = Math.max(1, end - maxVisiblePages + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
  }

  return pages;
});

// 태그별 CSS 클래스 결정
const getTagClass = (tag) => {
  const tagClassMap = {
    주거: styles.housing,
    다자녀: styles.multiChild,
    장애인: styles.disabled,
    저소득: styles.lowIncome,
    '한부모·조손': styles.singleParent,
    '보호·돌봄': styles.care,
    생활지원: styles.lifeSupport,
  };
  return tagClassMap[tag] || styles.default;
};

// 온라인/오프라인 변환 함수
const formatPolicyOnline = (online) => {
  if (!online) return '오프라인';
  return online === 'Y' ? '온라인' : '오프라인';
};

// 날짜 형식 변환 함수 (20250807 -> 2025년 08월 07일)
const formatPolicyDate = (dateString) => {
  if (!dateString || dateString.length !== 8) return '';

  const year = dateString.substring(0, 4);
  const month = dateString.substring(4, 6);
  const day = dateString.substring(6, 8);

  return `${year}년 ${month}월 ${day}일`;
};

// 컴포넌트 마운트 시 정책 데이터 로드
onMounted(() => {
  loadCustomPolicies();
});
</script>

<template>
  <div :class="styles.policySectionContainer">
    <!-- 제목 섹션 -->
    <div :class="styles.policySectionTitleSection">
      <h2 :class="styles.policySectionSectionTitle">맞춤 지원 정책</h2>
    </div>

    <!-- 검색창 -->
    <div :class="styles.policySectionSearchBox">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="정책명, 키워드로 검색해보세요"
        :class="styles.policySectionSearchInput"
        @keyup.enter="handleSearch"
      />
      <div :class="styles.policySectionSearchButtonIcon" @click="handleSearch">
        <div :class="styles.policySectionSearchIconInner">
          <i class="fa-solid fa-magnifying-glass"></i>
        </div>
      </div>
    </div>

    <!-- 카테고리 필터 -->
    <div :class="styles.policySectionCategoryFilter">
      <button
        v-for="category in categories"
        :key="category.id"
        :class="[
          styles.policySectionCategoryBtn,
          { [styles.policySectionActive]: activeCategory === category.id },
        ]"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </button>
    </div>

    <!-- 정책 카드들 -->
    <div :class="styles.policySectionPolicyCards">
      <!-- 로딩 상태 -->
      <div v-if="isPoliciesLoading" :class="styles.policySectionLoadingMessage">
        정책을 불러오는 중...
      </div>

      <!-- 검색 결과 없음 -->
      <div
        v-else-if="hasSearched && policies.length === 0"
        :class="styles.policySectionNoPoliciesMessage"
      >
        검색 결과가 없습니다.
      </div>

      <!-- 정책 카드들 -->
      <div
        v-else
        v-for="policy in paginatedPolicies"
        :key="policy.id"
        :class="styles.policySectionPolicyCard"
        @click="openPolicyDetail(policy)"
      >
        <!-- 카드 헤더 -->
        <div :class="styles.policySectionCardHeader">
          <div :class="styles.policySectionCardLogo">
            <div :class="styles.policySectionCardDetailHeader">
              <span :class="styles.policySectionSupportCycle">{{
                policy.supportCycle || '연중'
              }}</span>
            </div>
            <h3 :class="styles.policySectionCardTitle">
              {{ policy.policyName }}
            </h3>
          </div>
        </div>

        <!-- 카드 내용 -->
        <div :class="styles.policySectionCardContent">
          <div :class="styles.policySectionLogoImage">
            <img
              :src="getMinisterLogo(policy.ministryName)"
              :alt="policy.ministryName"
            />
          </div>
          <span :class="styles.policySectionLogoText">{{
            policy.ministryName
          }}</span>
        </div>

        <!-- 정책 정보 -->
        <div :class="styles.policySectionPolicyInfo">
          <span :class="styles.policySectionPolicyOnline">{{
            policy.isOnlineApplyAvailable === 'Y' ? '온라인' : '오프라인'
          }}</span>
          <span :class="styles.policySectionPolicyDate">{{
            formatPolicyDate(policy.registrationDate)
          }}</span>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div v-if="totalPages > 1" :class="styles.policySectionPaginationContainer">
      <button
        :disabled="currentPage === 1"
        @click="goToPreviousPage"
        :class="[
          styles.policySectionPaginationBtn,
          styles.policySectionPrevBtn,
        ]"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path
            d="M15 18L9 12L15 6"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>

      <div :class="styles.policySectionPageNumbers">
        <button
          v-for="page in pageNumbers"
          :key="page"
          :class="[
            styles.policySectionPageNumber,
            { [styles.policySectionActivePage]: currentPage === page },
          ]"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
      </div>

      <button
        :disabled="currentPage === totalPages"
        @click="goToNextPage"
        :class="[
          styles.policySectionPaginationBtn,
          styles.policySectionNextBtn,
        ]"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path
            d="M9 18L15 12L9 6"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </div>

    <!-- 정책 상세 모달 -->
    <PolicyDetailModal
      :isVisible="isModalVisible"
      :policyData="selectedPolicyData"
      @close="closeModal"
    />
  </div>
</template>
