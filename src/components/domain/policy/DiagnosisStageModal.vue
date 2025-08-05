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
                src="/images/court2.png"
                alt="법원"
                @error="handleImageError"
              />
            </div>
            <span :class="styles.institutionName">{{
              currentStage.institution
            }}</span>
          </div>
          <h1 :class="styles.stageTitle">{{ currentStage.name }}</h1>
          <p :class="styles.stageDescription">{{ currentStage.description }}</p>
        </div>

        <!-- 대상자 섹션 -->
        <div :class="styles.section">
          <!-- 구분선 -->
          <div :class="styles.divider"></div>
          <h2 :class="styles.sectionTitle">대상자</h2>
          <ul :class="styles.sectionList">
            <li
              v-for="(item, index) in currentStage.targets"
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
              v-for="(item, index) in currentStage.advantages"
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
              v-for="(item, index) in currentStage.warnings"
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

<script setup>
import { computed } from 'vue';
import styles from '@/assets/styles/components/policy/DiagnosisStageModal.module.css';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false,
  },
  diagnosisStage: {
    type: String,
    default: '개인파산',
  },
});

const emit = defineEmits(['close']);

const closeModal = () => {
  emit('close');
};

// 이미지 에러 핸들링
const handleImageError = (e) => {
  console.log('이미지 로드 실패:', e.target.src);
  // 대체 이미지나 텍스트로 표시
  e.target.style.display = 'none';
};

// 진단 단계별 데이터
const stageData = {
  개인파산: {
    name: '개인파산',
    institution: '법원',
    icon: '/images/court.png',
    description:
      '재산을 모두 처분하여 채권자에게 배당하고 남은 부채를 면책받는 제도입니다.',
    targets: [
      '지급불능 상태',
      '면책불허가 사유에 해당하지 않는 자',
      '성실한 파산절차 참여',
    ],
    advantages: [
      '부채의 최대 90%까지 탕감 가능',
      '강제집행 중단',
      '이자 부담 없음',
      '주택 등 재산 보유 가능',
    ],
    warnings: [
      '재산 처분 필요',
      '신용등급 최하위 (7년간)',
      '일부 직업 제한',
      '면책 불허가 사유 존재',
    ],
  },
  신속채무조정: {
    name: '신속채무조정',
    institution: '법원',
    icon: '/images/court.png',
    description:
      '법원의 개입 하에 채권자와 채무자가 합의하여 채무를 조정하는 제도입니다.',
    targets: [
      '지급불능 상태',
      '채무조정 신청 자격이 있는 자',
      '성실한 채무조정 참여',
    ],
    advantages: [
      '부채의 최대 80%까지 탕감 가능',
      '강제집행 중단',
      '이자 부담 감소',
      '재산 처분 없이 가능',
    ],
    warnings: [
      '채무조정 계획 준수 필요',
      '신용등급 하락 (5년간)',
      '일부 금융거래 제한',
      '채무조정 실패 시 파산 가능',
    ],
  },
  개인워크아웃: {
    name: '개인워크아웃',
    institution: '금융감독원',
    icon: '/images/court.png',
    description: '금융기관과의 자율적 합의를 통해 채무를 조정하는 제도입니다.',
    targets: ['금융채무 보유자', '지급불능 상태', '성실한 채무조정 의지'],
    advantages: [
      '부채의 최대 70%까지 탕감 가능',
      '강제집행 중단',
      '이자 부담 감소',
      '재산 처분 없이 가능',
    ],
    warnings: [
      '금융채무만 대상',
      '신용등급 하락 (3년간)',
      '일부 금융거래 제한',
      '워크아웃 실패 시 법적 절차',
    ],
  },
  프리워크아웃: {
    name: '프리워크아웃',
    institution: '금융감독원',
    icon: '/images/court.png',
    description: '금융기관과의 자율적 합의를 통해 채무를 조정하는 제도입니다.',
    targets: ['금융채무 보유자', '지급불능 상태', '성실한 채무조정 의지'],
    advantages: [
      '부채의 최대 60%까지 탕감 가능',
      '강제집행 중단',
      '이자 부담 감소',
      '재산 처분 없이 가능',
    ],
    warnings: [
      '금융채무만 대상',
      '신용등급 하락 (2년간)',
      '일부 금융거래 제한',
      '워크아웃 실패 시 법적 절차',
    ],
  },
};

const currentStage = computed(() => {
  return stageData[props.diagnosisStage] || stageData['개인파산'];
});
</script>
