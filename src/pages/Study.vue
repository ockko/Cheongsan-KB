<script setup>
import styles from '@/assets/styles/pages/Study.module.css';
import StudyContentItem from '@/components/domain/Study/StudyContentItem.vue';
import { ref, onMounted, computed } from 'vue';

const categories = ref([
  '전체',
  '신용',
  '카드',
  '보험',
  '대출',
  '저축',
  '세금',
  '투자',
]);

const originalSlides = [
  {
    imageUrl: '/images/study-thumbnail-1.png',
    title: '신용카드 결제일을 14일로 해야 하는 이유',
  },
  {
    imageUrl: '/images/study-thumbnail-example.png',
    title: '투자 초보자를 위한 가이드',
  },
  {
    imageUrl: '/images/study-thumbnail-example.png',
    title: '부동산 투자의 기본 원칙',
  },
  {
    imageUrl: '/images/study-thumbnail-example.png',
    title: '암호화폐 투자 전략',
  },
  {
    imageUrl: '/images/study-thumbnail-example.png',
    title: '경제 뉴스 읽는 법',
  },
];

const studyContents = [
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['저축', '카드', '신용'],
    title: '2025 청년도약계좌: 목돈 마련 지름길',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['투자', '신용'],
    title: '투자 성공 비결 궁금해요? 궁금하면 500원',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['저축', '세금'],
    title: '근데 투자 성공 비결이 진짜로 궁금해요? 그러면 50000원',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['신용', '카드', '보험'],
    title: '야, 너도! 신용불량자 탈출할 수 있어',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['보험', '대출'],
    title: '야! 너도 진짜 신용불량자 탈출할 수 있어',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['신용', '대출', '저축'],
    title: '야! 장난하냐? 기죽지마! 너도 신용불량자 탈출할 수 있어',
  },
];

// 트릭 슬라이드를 위한 복제 포함 슬라이드 배열
const carouselContents = ref([]);
const currentSlide = ref(1); // 실제 시작 위치
const transitionEnabled = ref(true);

// 스와이프 관련 변수들
const touchStartX = ref(0);
const touchEndX = ref(0);
const minSwipeDistance = 50; // 최소 스와이프 거리

onMounted(() => {
  carouselContents.value = [
    originalSlides[originalSlides.length - 1],
    ...originalSlides,
    originalSlides[0],
  ];
});

const nextSlide = () => {
  currentSlide.value++;
  transitionEnabled.value = true;
  if (currentSlide.value === carouselContents.value.length - 1) {
    setTimeout(() => {
      transitionEnabled.value = false;
      currentSlide.value = 1;
    }, 300);
  }
};

const prevSlide = () => {
  currentSlide.value--;
  transitionEnabled.value = true;
  if (currentSlide.value === 0) {
    setTimeout(() => {
      transitionEnabled.value = false;
      currentSlide.value = carouselContents.value.length - 2;
    }, 300);
  }
};

const goToSlide = (index) => {
  currentSlide.value = index + 1;
};

// 터치 이벤트 핸들러들
const handleTouchStart = (event) => {
  touchStartX.value = event.touches[0].clientX;
};

const handleTouchMove = (event) => {
  touchEndX.value = event.touches[0].clientX;
};

const handleTouchEnd = () => {
  const swipeDistance = touchStartX.value - touchEndX.value;

  if (Math.abs(swipeDistance) > minSwipeDistance) {
    if (swipeDistance > 0) {
      // 왼쪽으로 스와이프 (다음 슬라이드)
      nextSlide();
    } else {
      // 오른쪽으로 스와이프 (이전 슬라이드)
      prevSlide();
    }
  }
};

const activeIndex = ref(0);
const selectCategory = (index) => {
  activeIndex.value = index;
  window.scrollTo({ top: 0, behavior: 'smooth' }); // 카테고리 클릭 시 스크롤 최상단
};

const filteredContents = computed(() => {
  const activeCategory = categories.value[activeIndex.value];
  if (activeCategory === '전체') return studyContents;
  return studyContents.filter(content => content.tags.includes(activeCategory));
});
</script>

<template>
  <div :class="styles.studyPage">
    <div :class="styles.studyMain">
      <h1 :class="[styles.pageTitle, 'text-bold', 'color-main']">
        금융 지식을 학습해보세요.
      </h1>

      <div :class="styles.carousel">
        <button :class="styles.arrow" @click="prevSlide">
          <img src="/images/arrow-left.png" alt="왼쪽 화살표" />
        </button>

        <div :class="styles.carouselContainer">
          <div :class="styles.carouselWrapper">
            <div
              :class="styles.carouselSlides"
              :style="{
                transform: `translateX(-${currentSlide * 100}%)`,
                transition: transitionEnabled
                  ? 'transform 0.3s ease-in-out'
                  : 'none',
              }"
              @touchstart="handleTouchStart"
              @touchmove="handleTouchMove"
              @touchend="handleTouchEnd"
            >
              <div
                v-for="(content, index) in carouselContents"
                :key="index"
                :class="[styles.carouselCard, 'bg-light2']"
              >
                <img
                  v-if="content.imageUrl"
                  :src="content.imageUrl"
                  alt="학습 이미지"
                  :class="styles.carouselImage"
                />
              </div>
            </div>
          </div>
        </div>

        <button :class="styles.arrow" @click="nextSlide">
          <img src="/images/arrow-right.png" alt="오른쪽 화살표" />
        </button>
      </div>

      <p :class="[styles.carouselCaption, 'color-third']">
        {{ carouselContents[currentSlide]?.title }}
      </p>

      <div :class="styles.carouselDots">
        <button
          v-for="(_, index) in originalSlides"
          :key="index"
          :class="[
            styles.dot,
            currentSlide === index + 1 ? 'bg-main' : 'bg-gray2',
          ]"
          @click="goToSlide(index)"
        ></button>
      </div>
    </div>

    <div :class="[styles.categoryDivider, 'color-gray0']"></div>

    <!-- 카테고리 탭 -->
      <div :class="styles.categoryTabs">
        <button
          v-for="(category, index) in categories"
          :key="category"
          :class="[
            styles.categoryTab,
            { active: activeIndex === index },
            activeIndex === index ? 'color-main' : 'color-gray0',
          ]"
          @click="selectCategory(index)"
        >
          {{ category }}
        </button>
      </div>

    <!-- 컨텐츠 리스트 -->
      <div :class="styles.contentsList">
        <StudyContentItem :contents="filteredContents" />
      </div>
    </div>
</template>
