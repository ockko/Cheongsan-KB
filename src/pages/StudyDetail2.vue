<script setup>
import styles from '@/assets/styles/pages/StudyDetail.module.css'
import StudyContentItem from '@/components/domain/Study/StudyContentItem.vue'

const studyContents = [
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['예적금', '청년'],
    title: '2025 청년도약계좌: 목돈 마련 지름길',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['투자', '재테크'],
    title: '투자 성공 비결 궁금해요? 궁금하면 500원',
  },
  {
    thumbnailUrl: '/images/study-thumbnail-example.png',
    tags: ['신용', '회복'],
    title: '야, 너도! 신용불량자 탈출할 수 있어',
  },
];

const contentSections = [
  {
    text: '신용카드를 발급 받을 때는 보통 대금 결제일을 함께 설정해요. 결제일은 급여일이나 카드 발급일, 또는 매월 1일에 맞춰 설정하는 경우가 많습니다. \n\n 하지만 보통 신용카드 결제일을 14일로 설정하면 돈 관리를 가장 효과적으로 할 수 있어요. ',
  },
  {
    title: '전월실적 관리에 유리해요',
    text: '카드사들은 전월 실적을 충족하면 다양한 혜택을 제공해요. 여기서 ‘전월’의 기준은 매달 1일부터 말일까지인데요. 즉, 지난달 1일부터 말일까지 사용한 카드 금액이 일정 기준을 넘으면 혜택을 받을 수 있어요.' +
    '\n\n하지만 전월 카드값을 결제할 때 적용되는 실적 산정 기간은 결제일에 따라 달라질 수 있어요. 예를 들어 결제일이 10일이라면, 보통 전전달 27일부터 전달 26일까지 사용한 금액이 그날 청구돼요. 이처럼 전월 실적 기간과 결제 금액 산정 기간이 다르면, 자신이 쓴 카드값이 실적을 충족했는지 파악하기 어려워져요. 그래서 결제일을 변경해 카드 결제 기간과 실적 산정 기간을 일치시키는 것이 좋아요.' +
    '\n\n일반적으로 결제일이 14일인 경우, 실적 기준 기간과 결제 금액 기간이 일치해요. 이로 인해 전월 실적 금액과 결제 금액도 같아져요. 다만 카드사에 따라 일자는 조금씩 다를 수 있으니, 본인의 카드사에 결제일과 적용 기간을 확인해보세요.',
    image: '/images/study_detail_1.png'
  },
  {
    title: '신용카드 결제일 바꾸는 법',
    text: '신용카드 결제일은 카드사 앱이나 홈페이지에서 바꿀 수 있어요. ‘결제 정보’ 또는 ‘결제일 변경’ 관련 메뉴에서 직접 변경할 수 있으니 확인해보세요.',
  }
]
</script>

<template>
  <div :class="styles.studyDetailPage">
    <button :class="styles.arrow">
      <img src="/images/arrow-left.png" alt="왼쪽 화살표" />
    </button>

    <div :class="styles.container">
      <h1 :class="styles.detailTitle">신용카드 결제일을 14일로 해야 하는 이유</h1>
      <p :class="styles.createdDate">⏲️ 2025-07-21</p>

      <div :class="styles.thumbnail">
        <img src="/images/study-thumbnail-1.png" alt="썸네일" />
      </div>

      <div :class="styles.contents">
        <section
          v-for="(section, idx) in contentSections"
          :key="idx"
          :class="styles.contentSection"
        >
          <h2 v-if="section.title" :class="styles.subTitle">{{ section.title }}</h2>

          <p :class="styles.sectionText">{{ section.text }}</p>
          <img v-if="section.image" :src="section.image" alt="관련 이미지" :class="styles.sectionImage" />
        </section>
      </div>

      <div :class="[styles.categoryDivider, 'color-gray0']"></div>

      <section :class="styles.recommendSection">
        <h2 :class="styles.subTitle">추천 콘텐츠</h2>
      </section>

      <div :class="styles.contentsList">
        <StudyContentItem :contents="studyContents" />
      </div>
    </div>
  </div>
</template>
