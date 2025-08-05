<script setup>
import StudyContentItem from '@/components/domain/Study/StudyContentItem.vue';
import { ref, onMounted } from 'vue';

const studyContents = [
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['적금', '청년', '정부지원'],
    title: '2025 청년도약계좌: 목돈 마련 지름길',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['투자', '재테크'],
    title: '투자 성공 비결 궁금해요? 궁금하면 500원',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['투자', '재테크'],
    title: '근데 투자 성공 비결이 진짜로 궁금해요? 그러면 50000원',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['신용', '회복', '금융상식'],
    title: '야, 너도! 신용불량자 탈출할 수 있어',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['신용', '회복', '금융상식'],
    title: '야! 너도 진짜 신용불량자 탈출할 수 있어',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['신용', '회복', '금융상식'],
    title: '야! 장난하냐? 기죽지마! 너도 신용불량자 탈출할 수 있어',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['신용', '회복', '금융상식'],
    title: '야! 너도, 신용불량자 탈출할 수 있어',
  },
];

// 원본 콘텐츠
const originalSlides = [
  {
    imageUrl: '/images/study-thumbnail-example.png',
    title: '앱테크로 100만원 벌 수 있을까?',
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

// 트릭 슬라이드를 위한 복제 포함 슬라이드 배열
const carouselContents = ref([]);
const currentSlide = ref(1); // 실제 시작 위치
const transitionEnabled = ref(true);

onMounted(() => {
  carouselContents.value = [
    originalSlides[originalSlides.length - 1],
    ...originalSlides,
    originalSlides[0],
  ];
});

function nextSlide() {
  currentSlide.value++;
  transitionEnabled.value = true;
  if (currentSlide.value === carouselContents.value.length - 1) {
    setTimeout(() => {
      transitionEnabled.value = false;
      currentSlide.value = 1;
    }, 300);
  }
}

function prevSlide() {
  currentSlide.value--;
  transitionEnabled.value = true;
  if (currentSlide.value === 0) {
    setTimeout(() => {
      transitionEnabled.value = false;
      currentSlide.value = carouselContents.value.length - 2;
    }, 300);
  }
}

function goToSlide(index) {
  currentSlide.value = index + 1;
}

const categories = ref([
  '전체',
  '신용',
  '카드',
  '보험',
  '대출',
  '예적금',
  '세금',
  '투자',
]);
const activeIndex = ref(0);
function selectCategory(index) {
  activeIndex.value = index;
}
</script>

<template>
  <div class="study-page">
    <div class="study-main">
      <h1 class="page-title text-bold color-main">금융 지식을 학습해보세요.</h1>

      <div class="carousel">
        <button class="arrow" @click="prevSlide">
          <img src="/images/arrow-left.png" alt="왼쪽 화살표" />
        </button>

        <div class="carousel-container">
          <div class="carousel-wrapper">
            <div
              class="carousel-slides"
              :style="{
                transform: `translateX(-${currentSlide * 100}%)`,
                transition: transitionEnabled
                  ? 'transform 0.3s ease-in-out'
                  : 'none',
              }"
            >
              <div
                v-for="(content, index) in carouselContents"
                :key="index"
                class="carousel-card bg-light2"
              >
                <img
                  v-if="content.imageUrl"
                  :src="content.imageUrl"
                  alt="학습 이미지"
                  class="carousel-image"
                />
              </div>
            </div>
          </div>
        </div>

        <button class="arrow" @click="nextSlide">
          <img src="/images/arrow-right.png" alt="오른쪽 화살표" />
        </button>
      </div>

      <p class="carousel-caption color-third">
        {{ carouselContents[currentSlide]?.title }}
      </p>

      <div class="carousel-dots">
        <button
          v-for="(_, index) in originalSlides"
          :key="index"
          :class="['dot', currentSlide === index + 1 ? 'bg-main' : 'bg-gray2']"
          @click="goToSlide(index)"
        ></button>
      </div>
    </div>

    <div class="category-divider color-gray0"></div>

    <div class="category-tabs">
      <button
        v-for="(category, index) in categories"
        :key="category"
        :class="[
          'category-tab',
          { active: activeIndex === index },
          activeIndex === index ? 'color-main' : 'color-gray0',
        ]"
        @click="selectCategory(index)"
      >
        {{ category }}
      </button>
    </div>

    <div class="contentsList">
      <StudyContentItem :contents="studyContents" />
    </div>
  </div>
</template>

<style scoped>
.study-page {
  margin-bottom: 80px;
}
.study-main {
  text-align: center;
}
.page-title {
  margin: -10px 0 0 0;
}
.carousel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 150px;
  padding: 0 1rem;
}
.carousel-container {
  flex: 1;
  height: 100%;
  margin: 0 1rem;
  overflow: hidden;
  border-radius: 15%;
}
.carousel-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}
.carousel-slides {
  display: flex;
  height: 100%;
}
.carousel-card {
  flex: 0 0 100%;
  height: 100%;
  border-radius: 15%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin: 0;
}
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 15%;
}
.arrow {
  background: none;
  border: none;
  cursor: pointer;
}
.arrow img {
  width: 12px;
  height: 24px;
}
.carousel-caption {
  text-align: center;
  font-size: 15px;
  margin-top: -10px;
  margin-bottom: -5px;
}
.carousel-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 2rem;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
}
.category-divider {
  height: 1px;
  margin: 0 1rem;
  background-color: var(--color-gray1);
}
.category-tabs {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  padding: 0 1rem;
  margin-top: 1rem;
}
.category-tab {
  flex: 1 0 calc(100% / 8);
  text-align: center;
  font-size: 13px;
  background: none;
  border: none;
  padding: 0.5rem 0;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.category-tab.active {
  font-weight: 900;
  font-size: 14px;
}
.carousel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 150px;
  padding: 0 1rem;
}
.carousel-container {
  flex: 1;
  height: 100%;
  margin: 0 1rem;
  overflow: hidden;
  border-radius: 15%;
}
.carousel-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}
.carousel-slides {
  display: flex;
  height: 100%;
}
.carousel-card {
  flex: 0 0 100%;
  height: 100%;
  border-radius: 15%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin: 0;
}
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 15%;
}
.arrow {
  background: none;
  border: none;
  cursor: pointer;
}
.arrow img {
  width: 12px;
  height: 24px;
}
.carousel-caption {
  text-align: center;
  font-size: 15px;
  margin-top: -10px;
  margin-bottom: -5px;
}
.carousel-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 2rem;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: none;
  padding: 0;
  cursor: pointer;
}
.category-divider {
  height: 1px;
  background-color: var(--color-gray1);
}
.category-tabs {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  padding: 0 1rem;
  margin-top: 1rem;
}
.category-tab {
  flex: 1 0 calc(100% / 8);
  text-align: center;
  font-size: 13px;
  background: none;
  border: none;
  padding: 0.5rem 0;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.category-tab.active {
  font-weight: 900;
}
.contentsList {
  padding: 1rem 2rem;
}
</style>
