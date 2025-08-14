<script setup>
import styles from '@/assets/styles/pages/Study.module.css';
import StudyContentItem from '@/components/domain/Study/StudyContentItem.vue';
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const itemsPerPage = 5;
const currentPage = ref(1);

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
    imageUrl: '/images/study-thumbnail-2.jpg',
    title: '재테크의 정석, 금융 집짓기',
  },
  {
    imageUrl: '/images/study-thumbnail-3.jpg',
    title: '빚이 부담될 때 살펴보는 채무조정제도',
  },
  {
    imageUrl: '/images/study-thumbnail-4.png',
    title: '2025 청년 목돈 마련 위한 정책 총정리',
  },
  {
    imageUrl: '/images/study-thumbnail-5.jpg',
    title: '신용카드 한도, 어떻게 정해질까?',
  },
];

const handleContentClick = (content, index) => {
  // 신용 카테고리이면서 첫 번째 게시글만 상세 페이지 이동
  if (
    categories.value[activeIndex.value] === '신용' &&
    index === 0
  ) {
    router.push('/study/detail');
  }
};

const studyContents = [
  {
    thumbnailUrl: '/images/study-thumbnail-2.jpg',
    tags: ['투자', '저축', '보험'],
    title: '재테크의 정석, 금융 집짓기',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-4.png',
    tags: ['세금', '저축', '대출'],
    title: '2025 청년 목돈 마련 위한 정책 총정리',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-1.png',
    tags: ['신용', '카드'],
    title: '신용카드 결제일을 14일로 해야 하는 이유',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-3.jpg',
    tags: ['신용', '대출'],
    title: '빚이 부담될 때 살펴보는 채무조정제도',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-6.jpg',
    tags: ['신용', '대출'],
    title: '대출 필수 용어 3가지: LTV, DTI, DSR',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-5.jpg',
    tags: ['신용', '카드'],
    title: '신용카드 한도, 어떻게 정해질까?',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-7.png',
    tags: ['신용', '카드'],
    title: '헬스장 회원권은 신용카드 할부로 결제하면 좋은 이유',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-8.png',
    tags: ['대출', '신용', '카드'],
    title: '대출 이자 줄이는 3가지 방법',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-9.png',
    tags: ['신용', '카드'],
    title: '해외결제 취소 후 챙겨야 할 1가지',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-10.png',
    tags: ['세금' , '저축'],
    title: '중소기업 취업자라면 소득세 감면 혜택 꼭 챙기세요',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-11.png',
    tags: ['저축','투자'],
    title: '금테크의 모든 것: 왜 지금 금값이 오르고 있을까?',
  },
   {
    thumbnailUrl: '/images/study-thumbnail-12.png',
    tags: ['대출'],
    title: '대출 관리 시작하기, 내가 받은 대출 파악하는 법',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-13.png',
    tags: ['보험', '저축'],
    title: '암보험에서 반드시 확인해야 할 두 가지는?',
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
  currentPage.value = 1; // ← 추가
  window.scrollTo({ top: 0, behavior: 'smooth' }); // 카테고리 클릭 시 스크롤 최상단
};


const filteredContents = computed(() => {
  const activeCategory = categories.value[activeIndex.value];
  if (activeCategory === '전체') return studyContents;
  return studyContents.filter(content => content.tags.includes(activeCategory));
});

// 현재 페이지에 맞는 콘텐츠 슬라이스
const paginatedContents = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return filteredContents.value.slice(start, start + itemsPerPage);
});

// 전체 페이지 수
const totalPages = computed(() => Math.ceil(filteredContents.value.length / itemsPerPage));

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
};
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

      <div class="contentsListWrapper">
          <div :class="styles.contentsList">
            <StudyContentItem 
            :contents="paginatedContents" 
            @clickContent="handleContentClick"
            />
        
          <div :class="styles.pagination">
            <button 
              @click="goToPage(currentPage - 1)" 
              :disabled="currentPage === 1"
            >
              &lt;
            </button>

            <button
              v-for="page in totalPages"
              :key="page"
              :class="{ active: currentPage === page }"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>

            <button 
              @click="goToPage(currentPage + 1)" 
              :disabled="currentPage === totalPages"
            >
              &gt;
            </button>
          </div>
      </div>
    </div>
  </div>
</template>
