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
                :src="getMinisterLogo(policyData.ministryName)"
                :alt="policyData.ministryName"
                @error="handleImageError"
              />
            </div>
            <div :class="styles.institutionDetails">
              <span :class="styles.institutionName">{{
                policyData.ministryName
              }}</span>
              <span :class="styles.departmentName">{{
                policyData.departmentName
              }}</span>
            </div>
          </div>
          <div :class="styles.policyHeader">
            <h1 :class="styles.policyTitle">{{ policyData.policyName }}</h1>
            <div :class="styles.policyMeta">
              <span :class="styles.policyNumber"
                >정책번호: {{ policyData.policyNumber }}</span
              >
              <div :class="styles.policyTags">
                <span
                  v-for="tag in policyData.policyTags"
                  :key="tag"
                  :class="styles.tag"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>
          <p :class="styles.policySummary">{{ policyData.policySummary }}</p>
        </div>

        <!-- 지원 대상 섹션 -->
        <div :class="styles.section">
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">지원 대상</h2>
          <ul :class="styles.sectionList">
            <li
              v-for="(target, index) in policyData.supportTarget"
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
            <li v-if="policyData.supportAge" :class="styles.listItem">
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
                  policyData.supportAge
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
                policyData.supportType
              }}</span>
            </div>
            <div :class="styles.supportItem">
              <span :class="styles.supportLabel">지원 주기</span>
              <span :class="styles.supportValue">{{
                policyData.supportCycle
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
                      policyData.isOnlineApplyAvailable === 'Y'
                        ? '#28A745'
                        : '#DC3545'
                    "
                  />
                  <path
                    v-if="policyData.isOnlineApplyAvailable === 'Y'"
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
                  policyData.isOnlineApplyAvailable === 'Y' ? '가능' : '불가능'
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
                policyData.contactNumber
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
              v-if="policyData.isOnlineApplyAvailable === 'Y'"
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
import { computed } from 'vue';
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

const closeModal = () => {
  emit('close');
};

// 이미지 에러 핸들링
const handleImageError = (e) => {
  console.log('이미지 로드 실패:', e.target.src);
  e.target.style.display = 'none';
};

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
  if (props.policyData.detailPageUrl) {
    window.open(props.policyData.detailPageUrl, '_blank');
  }
};
</script>
