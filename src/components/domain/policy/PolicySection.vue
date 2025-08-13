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
    console.log('검색 시작:', searchKeyword.value || '(빈 검색어)');

    // 검색 API 호출 (빈 검색어도 허용)
    const searchResults = await searchCustomPolicies(
      searchKeyword.value.trim() || ''
    );

    if (searchResults && searchResults.length > 0) {
      policies.value = searchResults;
      console.log('검색 결과:', searchResults);
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
};

const goToPolicyDetail = (policyId) => {
  router.push(`/policy-detail/${policyId}`);
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

    // 기타 기관들 (기존)
    서민금융진흥원: '/images/smf-logo.png',
    한국주택금융공사: '/images/hf-logo.png',
    국민연금공단: '/images/nps-logo.png',
    국민건강보험공단: '/images/nhis-logo.png',
    중소기업진흥공단: '/images/sbc-logo.png',
    한국산업기술진흥원: '/images/kiat-logo.png',
    한국장애인고용공단: '/images/kead-logo.png',
    한국여성가족재단: '/images/kwf-logo.png',
  };

  return logoMapping[logoText] || '/images/default-logo.png';
};

const selectCategory = (categoryId) => {
  activeCategory.value = categoryId;
  // 카테고리 변경 시에는 검색 상태를 유지하고 클라이언트 사이드 필터링만 적용
  console.log('선택된 카테고리:', categoryId);
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
    <div :class="styles.titleSection">
      <h2 :class="styles.sectionTitle">맞춤 지원 정책</h2>
    </div>

    <!-- 검색창 -->
    <div :class="styles.searchBox">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="원하시는 키워드로 정책을 찾아보세요."
        :class="styles.searchInput"
        @keyup.enter="handleSearch"
      />
      <div :class="styles.searchButtonIcon" @click="handleSearch">
        <div :class="styles.searchIconContainer">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="45"
            height="45"
            viewBox="0 0 45 45"
            fill="none"
          >
            <circle cx="22.5" cy="22.5" r="22.5" fill="#2E3134" />
          </svg>
          <div :class="styles.searchIconInner">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
            >
              <circle cx="11" cy="11" r="7" stroke="#FFF1F1" stroke-width="2" />
              <path
                d="M20 20L17 17"
                stroke="#FFF1F1"
                stroke-width="2"
                stroke-linecap="round"
              />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 카테고리 필터 -->
    <div :class="styles.categoryFilter">
      <button
        v-for="category in categories"
        :key="category.id"
        :class="[
          styles.categoryBtn,
          { [styles.active]: activeCategory === category.id },
        ]"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </button>
    </div>

    <!-- 정책 카드들 -->
    <div :class="styles.policyCards">
      <!-- 로딩 상태 -->
      <div
        v-if="isPoliciesLoading || isSearching"
        :class="styles.loadingMessage"
      >
        {{
          isSearching ? '검색 중입니다...' : '정책 정보를 불러오는 중입니다...'
        }}
      </div>

      <!-- 정책이 없을 때 -->
      <div
        v-else-if="filteredPolicies.length === 0"
        :class="styles.noPoliciesMessage"
      >
        {{
          searchKeyword.trim()
            ? '검색 결과가 없습니다.'
            : '조건에 맞는 정책이 없습니다.'
        }}
      </div>

      <!-- 정책 카드들 -->
      <div
        v-for="policy in filteredPolicies"
        :key="policy.policyId"
        :class="styles.policyCard"
        @click="openPolicyDetail(policy)"
      >
        <div :class="styles.cardHeader">
          <div :class="styles.cardLogo">
            <div :class="styles.cardDetailHeader">
              <span :class="styles.supportCycle">{{
                policy.supportCycle
              }}</span>
              <h3 :class="styles.cardTitle">{{ policy.policyName }}</h3>
            </div>
          </div>
        </div>

        <div :class="styles.cardContent">
          <div :class="styles.logoImage">
            <img
              :src="getMinisterLogo(policy.ministryName)"
              :alt="policy.ministryName"
              @error="(e) => (e.target.style.display = 'none')"
            />
          </div>
          <span :class="styles.logoText">{{ policy.ministryName }}</span>
        </div>
        <div :class="styles.policyInfo">
          <span :class="styles.policyOnline">{{
            formatPolicyOnline(policy.policyOnline)
          }}</span>
          <span :class="styles.policyDate">{{
            formatPolicyDate(policy.policyDate)
          }}</span>
        </div>
      </div>
    </div>

    <!-- 정책 상세 모달 -->
    <PolicyDetailModal
      :isVisible="isModalVisible"
      :policyData="selectedPolicyData"
      @close="closeModal"
    />
  </div>
</template>
