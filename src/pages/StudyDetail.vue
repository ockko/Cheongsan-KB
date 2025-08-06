<script setup>
import styles from '@/assets/styles/pages/StudyDetail.module.css'
import StudyContentItem from '@/components/domain/Study/StudyContentItem.vue'

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
  <div :class="styles.studyDetailPage">
    <button :class="styles.arrow">
      <img src="/images/arrow-left.png" alt="왼쪽 화살표" />
    </button>

    <div :class="styles.container">
      <h1 :class="styles.detailTitle">2025 청년도약계좌: 목돈 마련 지름길</h1>

      <div :class="styles.thumbnail">
        <img src="/images/study-thumbnail-example.png" alt="썸네일" />
      </div>

      <div :class="styles.Contents">
        <section :class="styles.contentSection" v-for="i in 3" :key="i">
          <h2 :class="styles.subTitle">청년도약계좌란?</h2>
          <p :class="styles.sectionText">
            동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세
          </p>
        </section>
      </div>

      <div :class="[styles.categoryDivider, 'color-gray0']"></div>

      <section :class="styles.recommendSection">
        <h2 :class="styles.subTitle">추천 콘텐츠</h2>
      </section>

      <div :class="contentsList">
        <StudyContentItem :contents="studyContents" />
      </div>
    </div>
  </div>
</template>
