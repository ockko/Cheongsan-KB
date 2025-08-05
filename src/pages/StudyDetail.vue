<script setup>
import StudyRecommendedItem from '@/components/domain/Study/StudyRecommendItem.vue';

const studyContents = [
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['적금', '정부지원'],
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
    tags: ['신용', '회복'],
    title: '야, 너도! 신용불량자 탈출할 수 있어',
  },
];
</script>

<template>
  <div class="study-detail-page">
    <button class="arrow">
      <img src="/images/arrow-left.png" alt="왼쪽 화살표" />
    </button>

    <div class="container">
      <h1 class="detail-title">2025 청년도약계좌: 목돈 마련 지름길</h1>

      <div class="thumbnail">
        <img src="/images/study-thumbnail-example.png" alt="썸네일" />
      </div>

      <div class="contents">
        <section class="content-section" v-for="i in 3" :key="i">
          <h2 class="sub-title">청년도약계좌란?</h2>
          <p class="section-text">
            동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세
          </p>
        </section>
      </div>

      <div class="category-divider color-gray0"></div>

      <section class="recommend-section">
        <h2 class="sub-title">추천 콘텐츠</h2>
      </section>

      <div class="contentsList">
        <StudyRecommendedItem :contents="studyContents" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.study-detail-page {
  padding: 0 2rem;
  margin-bottom: 80px;
}
.arrow {
  margin-top: -10px;
  margin-left: -10px;
  background: none;
  border: none;
  cursor: pointer;
}
.arrow img {
  width: 6px;
  height: 12px;
}
.container {
  padding: 0 0 2rem 0;
  margin-top: -30px;
}
.detail-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--color-main);
}
.thumbnail img {
  margin-top: 5px;
  width: 100%;
  height: auto;
  border-radius: 12px;
}
.content-section {
  margin-bottom: 1rem;
}

.sub-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--color-main);
}

.section-text {
  padding: 0 6px;
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-third);
}
.category-divider {
  height: 1px;
  background-color: var(--color-gray1);
}

.recommend-section {
  margin-top: 1.5rem;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.recommend-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.recommend-thumbnail {
  width: 64px;
  height: 64px;
  background-color: #d9d9d9;
  border-radius: 8px;
}

.recommend-info {
  flex: 1;
}

.recommend-tag {
  font-size: 11px;
  color: var(--color-main);
  margin-bottom: 2px;
  font-weight: 500;
}

.recommend-title-text {
  font-size: 14px;
  font-weight: 600;
  color: #000;
  line-height: 1.3;
}
.contentsList {
  margin-bottom: 20px;
}
</style>
