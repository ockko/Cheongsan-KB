<template>
  <div v-if="isVisible" :class="styles.modalOverlay" @click="closeModal">
    <div :class="styles.modalContent" @click.stop>
      <!-- 닫기 버튼 -->
      <button :class="styles.closeButton" @click="closeModal">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="12" fill="#E1E2E6" />
          <path
            d="M15 9L9 15M9 9L15 15"
            stroke="#72787F"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>
      </button>

      <!-- 모달 내용 -->
      <div :class="styles.modalBody">
        <!-- 제목 섹션 -->
        <div :class="styles.titleSection">
          <div :class="styles.institutionInfo">
            <div :class="styles.institutionIcon">
              <img
                :src="getMinisterLogo(displayPolicyData.ministryName)"
                :alt="displayPolicyData.ministryName"
                @error="handleImageError"
                loading="eager"
                decoding="async"
              />
            </div>
            <div :class="styles.institutionDetails">
              <span :class="styles.institutionName">{{
                displayPolicyData.ministryName
              }}</span>
              <span :class="styles.departmentName">{{
                displayPolicyData.departmentName
              }}</span>
            </div>
          </div>
          <div :class="styles.policyHeader">
            <h1 :class="styles.policyTitle">
              {{ displayPolicyData.policyName }}
            </h1>
            <div :class="styles.policyMeta">
              <span :class="styles.policyNumber"
                >정책번호: {{ displayPolicyData.policyNumber }}</span
              >
              <div :class="styles.policyTags">
                <span
                  v-for="tag in displayPolicyData.policyTags"
                  :key="tag"
                  :class="styles.tag"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>
          <p :class="styles.policySummary">
            {{ displayPolicyData.policySummary }}
          </p>
        </div>

        <!-- 지원 대상 섹션 -->
        <div :class="styles.section">
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">지원 대상</h2>
          <ul :class="styles.sectionList">
            <li
              v-for="(target, index) in displayPolicyData.supportTarget"
              :key="index"
              :class="styles.listItem"
            >
              <div :class="styles.listContent">
                <span :class="styles.targetIcon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="8" r="8" fill="#007BFF" />
                    <path
                      d="M6 8L7.5 9.5L10 7"
                      stroke="white"
                      stroke-width="1.5"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.listText">{{ target }}</span>
              </div>
            </li>
            <li v-if="displayPolicyData.supportAge" :class="styles.listItem">
              <div :class="styles.listContent">
                <span :class="styles.targetIcon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="8" r="8" fill="#007BFF" />
                    <path
                      d="M6 8L7.5 9.5L10 7"
                      stroke="white"
                      stroke-width="1.5"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </span>
                <span :class="styles.listText">{{
                  displayPolicyData.supportAge
                }}</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 지원 내용 섹션 -->
        <div :class="styles.section">
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">지원 내용</h2>
          <div :class="styles.supportInfo">
            <div :class="styles.supportItem">
              <span :class="styles.supportLabel">지원 형태</span>
              <span :class="styles.supportValue">{{
                displayPolicyData.supportType
              }}</span>
            </div>
            <div :class="styles.supportItem">
              <span :class="styles.supportLabel">지원 주기</span>
              <span :class="styles.supportValue">{{
                displayPolicyData.supportCycle
              }}</span>
            </div>
          </div>
        </div>

        <!-- 신청 방법 섹션 -->
        <div :class="styles.section">
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">신청 방법</h2>
          <div :class="styles.applicationInfo">
            <div :class="styles.applicationItem">
              <span :class="styles.applicationIcon">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <circle
                    cx="8"
                    cy="8"
                    r="8"
                    :fill="
                      displayPolicyData.isOnlineApplyAvailable === 'Y'
                        ? '#28A745'
                        : '#DC3545'
                    "
                  />
                  <path
                    v-if="displayPolicyData.isOnlineApplyAvailable === 'Y'"
                    d="M6 8L7.5 9.5L10 7"
                    stroke="white"
                    stroke-width="1.5"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <path
                    v-else
                    d="M6 6L10 10M10 6L6 10"
                    stroke="white"
                    stroke-width="1.5"
                    stroke-linecap="round"
                  />
                </svg>
              </span>
              <span :class="styles.applicationText">
                온라인 신청:
                {{
                  displayPolicyData.isOnlineApplyAvailable === 'Y'
                    ? '가능'
                    : '불가능'
                }}
              </span>
            </div>
          </div>
        </div>

        <!-- 문의처 섹션 -->
        <div :class="styles.section">
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">문의처</h2>
          <div :class="styles.contactInfo">
            <div :class="styles.contactItem">
              <span :class="styles.contactIcon">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <circle cx="8" cy="8" r="8" fill="#007BFF" />
                  <path
                    d="M5.5 6.5C5.5 7.5 6 8.5 8 10.5C10 8.5 10.5 7.5 10.5 6.5C10.5 5.67 9.83 5 9 5H7C6.17 5 5.5 5.67 5.5 6.5Z"
                    fill="white"
                  />
                </svg>
              </span>
              <span :class="styles.contactText">{{
                displayPolicyData.contactNumber
              }}</span>
            </div>
          </div>
        </div>

        <!-- 액션 버튼 섹션 -->
        <div :class="styles.actionSection">
          <div :class="styles.divider"></div>
          <div :class="styles.actionButtons">
            <button :class="styles.detailButton" @click="openDetailPage">
              상세 정보 보기
            </button>
            <button
              v-if="displayPolicyData.isOnlineApplyAvailable === 'Y'"
              :class="styles.applyButton"
              @click="openDetailPage"
            >
              온라인 신청하기
            </button>
          </div>
        </div>
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

// Mock 데이터 (Policy.js에서 가져온 데이터)
const mockPolicyData = {
  WLF00000917: {
    policyNumber: '39488',
    ministryName: '보건복지부',
    departmentName: '기초생활보장과',
    policyName: '긴급복지 주거지원',
    policyTags: ['주거'],
    policySummary:
      '생계곤란 등의 위기상황에 처하여 도움이 필요한 경우 일시적으로 신속하게 지원함으로써 위기상황에서 벗어날 수 있도록 지원합니다.',
    supportAge: null,
    supportTarget: ['저소득', '위기상황가구', '주거지원필요자'],
    supportType: '현금지급,현물지급',
    supportCycle: '월단위로 지원',
    isOnlineApplyAvailable: 'N',
    contactNumber: '129',
    detailPageUrl:
      'https://www.bokjiro.go.kr/ssis-tbu/twataa/wlfareInfo/moveTWAT52011M.do?wlfareInfoId=WLF00000917&wlfareInfoReldBztpCd=01',
    policyId: 'WLF00000917',
  },
  'policy-2': {
    policyNumber: '40123',
    ministryName: '고용노동부',
    departmentName: '청년고용정책과',
    policyName: '청년 일자리 지원',
    policyTags: ['일자리', '청년'],
    policySummary:
      '청년들의 취업을 돕기 위한 다양한 지원 프로그램을 제공하여 청년 고용률 증대에 기여합니다.',
    supportAge: '만 18세 ~ 만 34세',
    supportTarget: ['청년', '구직자', '미취업자'],
    supportType: '교육지원,취업알선',
    supportCycle: '연단위로 지원',
    isOnlineApplyAvailable: 'Y',
    contactNumber: '1350',
    detailPageUrl: 'https://www.work.go.kr/youth',
    policyId: 'policy-2',
  },
  'policy-3': {
    policyNumber: '40456',
    ministryName: '보건복지부',
    departmentName: '건강보험정책과',
    policyName: '건강보험 지원',
    policyTags: ['건강', '보험'],
    policySummary:
      '저소득층의 의료비 부담을 줄이기 위한 건강보험료 지원 및 의료급여 제도입니다.',
    supportAge: '전 연령',
    supportTarget: ['저소득층', '의료급여수급자', '차상위계층'],
    supportType: '의료비지원,보험료지원',
    supportCycle: '월단위로 지원',
    isOnlineApplyAvailable: 'N',
    contactNumber: '1577-1000',
    detailPageUrl: 'https://www.nhis.or.kr',
    policyId: 'policy-3',
  },
  'policy-4': {
    policyNumber: '40789',
    ministryName: '국토교통부',
    departmentName: '대중교통정책과',
    policyName: '대중교통 이용 지원',
    policyTags: ['교통', '이동지원'],
    policySummary:
      '대중교통 이용률 증대를 위한 교통비 지원 및 교통카드 할인 혜택을 제공합니다.',
    supportAge: '만 65세 이상',
    supportTarget: ['고령자', '저소득층', '교통약자'],
    supportType: '교통비지원,할인혜택',
    supportCycle: '월단위로 지원',
    isOnlineApplyAvailable: 'Y',
    contactNumber: '1599-7623',
    detailPageUrl: 'https://www.molit.go.kr',
    policyId: 'policy-4',
  },
  'policy-5': {
    policyNumber: '41012',
    ministryName: '교육부',
    departmentName: '평생교육정책과',
    policyName: '청년 교육 지원',
    policyTags: ['교육', '청년'],
    policySummary:
      '청년들의 평생교육 기회 확대를 위한 교육비 지원 및 온라인 교육 플랫폼을 제공합니다.',
    supportAge: '만 19세 ~ 만 39세',
    supportTarget: ['청년', '교육희망자', '재직자'],
    supportType: '교육비지원,온라인교육',
    supportCycle: '분기별로 지원',
    isOnlineApplyAvailable: 'Y',
    contactNumber: '1588-0570',
    detailPageUrl: 'https://www.moe.go.kr',
    policyId: 'policy-5',
  },
  'policy-6': {
    policyNumber: '41345',
    ministryName: '중소벤처기업부',
    departmentName: '창업진흥과',
    policyName: '창업 지원 프로그램',
    policyTags: ['창업', '청년'],
    policySummary:
      '청년 창업가들을 위한 자금 지원, 멘토링, 사업공간 제공 등 종합적인 창업 지원을 합니다.',
    supportAge: '만 20세 ~ 만 39세',
    supportTarget: ['예비창업자', '초기창업자', '청년'],
    supportType: '자금지원,멘토링,공간제공',
    supportCycle: '연단위로 지원',
    isOnlineApplyAvailable: 'Y',
    contactNumber: '1357',
    detailPageUrl: 'https://www.mss.go.kr',
    policyId: 'policy-6',
  },
  'policy-7': {
    policyNumber: '41678',
    ministryName: '환경부',
    departmentName: '녹색생활과',
    policyName: '친환경 생활 지원',
    policyTags: ['환경', '생활지원'],
    policySummary:
      '친환경 제품 구매 지원 및 에너지 절약 생활 실천을 위한 다양한 혜택을 제공합니다.',
    supportAge: '전 연령',
    supportTarget: ['일반시민', '친환경실천가구', '에너지절약가구'],
    supportType: '구매지원,할인혜택',
    supportCycle: '연단위로 지원',
    isOnlineApplyAvailable: 'N',
    contactNumber: '1588-9999',
    detailPageUrl: 'https://www.me.go.kr',
    policyId: 'policy-7',
  },
};

// 실제 데이터와 mock 데이터를 합성
const displayPolicyData = computed(() => {
  const policyId = props.policyData?.policyId;
  if (policyId && mockPolicyData[policyId]) {
    return {
      ...mockPolicyData[policyId],
      ...props.policyData, // props로 받은 데이터가 우선
    };
  }
  return props.policyData || {};
});

const closeModal = () => {
  emit('close');
};

// 이미지 에러 핸들링
const handleImageError = (e) => {
  console.log('이미지 로드 실패:', e.target.src);
  e.target.style.display = 'none';
};

// 이미지 프리로딩 함수
const preloadImages = () => {
  const imageUrls = [
    '/minister-logos/moel.jpg',
    '/minister-logos/mohw.jpg',
    '/minister-logos/motie.jpg',
    '/minister-logos/mogef.jpg',
    '/minister-logos/moe.jpg',
    '/minister-logos/mou.jpg',
    '/minister-logos/mocst.jpg',
    '/minister-logos/moafra.jpg',
    '/minister-logos/fsc.jpg',
    '/minister-logos/mopva.jpg',
    '/minister-logos/mois.jpg',
    '/minister-logos/mosi.jpg',
    '/minister-logos/moof.jpg',
    '/minister-logos/moef.jpg',
    '/minister-logos/kfs.jpg',
    '/minister-logos/moss.jpg',
    '/minister-logos/kdcpa.jpg',
    '/minister-logos/moen.jpg',
    '/minister-logos/molit.jpg',
  ];

  imageUrls.forEach((url) => {
    const img = new Image();
    img.src = url;
  });
};

// 컴포넌트 마운트 시 이미지 프리로드
if (typeof window !== 'undefined') {
  preloadImages();
}

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
  };

  return logoMapping[logoText] || '/images/default-logo.png';
};

// 상세 페이지 열기
const openDetailPage = () => {
  if (displayPolicyData.value.detailPageUrl) {
    window.open(displayPolicyData.value.detailPageUrl, '_blank');
  }
};
</script>
