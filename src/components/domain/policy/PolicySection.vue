<script setup>
import styles from '@/assets/styles/components/policy/PolicySection.module.css';
import { useRouter } from 'vue-router';
import { ref, onMounted, computed } from 'vue';
import PolicyDetailModal from './PolicyDetailModal.vue';
import { getCustomPolicies } from '@/api/policy.js';
import { useAuthStore } from '@/stores/auth';
import { getAccessToken } from '@/config/tokens.js';

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

// 맞춤 지원 정책 데이터 로드
const loadCustomPolicies = async () => {
  try {
    isPoliciesLoading.value = true;

    // 설정 파일에서 토큰 가져오기
    const accessToken = getAccessToken();

    const data = await getCustomPolicies(accessToken);
    policies.value = data;
    console.log('맞춤 지원 정책 데이터 로드 완료:', data);
  } catch (error) {
    console.error('맞춤 지원 정책 데이터 로드 실패:', error);
    // 에러 발생 시 빈 배열로 설정
    policies.value = [];
  } finally {
    isPoliciesLoading.value = false;
  }
};

// 정책 상세 정보 조회 및 모달 열기
const openPolicyDetail = async (policyId) => {
  try {
    isLoading.value = true;
    // policyId만 전달하여 PolicyDetailModal에서 mockData를 사용하도록 함
    selectedPolicyData.value = { policyId };
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
  // 여기에 카테고리별 필터링 로직을 추가할 수 있습니다
  console.log('선택된 카테고리:', categoryId);
};

// 필터링된 정책 목록 계산
const filteredPolicies = computed(() => {
  let filtered = policies.value;

  // 카테고리 필터링
  if (activeCategory.value !== '전체') {
    filtered = filtered.filter(
      (policy) =>
        policy.tagList && policy.tagList.includes(activeCategory.value)
    );
  }

  // 검색어 필터링
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(
      (policy) =>
        policy.serviceName.toLowerCase().includes(keyword) ||
        policy.summary.toLowerCase().includes(keyword) ||
        policy.jurMnofNm.toLowerCase().includes(keyword)
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
      />
      <div :class="styles.searchButtonIcon">
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
      <div v-if="isPoliciesLoading" :class="styles.loadingMessage">
        정책 정보를 불러오는 중입니다...
      </div>

      <!-- 정책이 없을 때 -->
      <div
        v-else-if="filteredPolicies.length === 0"
        :class="styles.noPoliciesMessage"
      >
        조건에 맞는 정책이 없습니다.
      </div>

      <!-- 정책 카드들 -->
      <div
        v-for="policy in filteredPolicies"
        :key="policy.serviceId"
        :class="styles.policyCard"
        @click="openPolicyDetail(policy.serviceId)"
      >
        <div :class="styles.cardHeader">
          <div :class="styles.cardLogo">
            <div :class="styles.logoImage">
              <img
                :src="getMinisterLogo(policy.jurMnofNm)"
                :alt="policy.jurMnofNm"
                @error="(e) => (e.target.style.display = 'none')"
              />
            </div>
            <span :class="styles.logoText">{{ policy.jurMnofNm }}</span>
          </div>
        </div>
        <div :class="styles.cardContent">
          <div :class="styles.cardDetailHeader">
            <span :class="styles.supportCycle">{{ policy.supportCycle }}</span>
            <h3 :class="styles.cardTitle">{{ policy.serviceName }}</h3>
          </div>
          <p :class="styles.cardSummary">{{ policy.summary }}</p>
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
