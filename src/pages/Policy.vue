<script setup>
// 정책 페이지
import styles from '@/assets/styles/pages/Policy.module.css';
</script>

<template>
  <div class="policy-page">
    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <!-- 검색 아이콘 섹션 -->
      <div class="search-section">
        <div class="search-icon">
          <img
            src="/images/policy-icon.png"
            alt="검색"
            class="search-icon-img"
          />
        </div>
        <div class="search-text">
          <h2 class="search-title">내게 맞는<br />채무 조정 제도 찾기</h2>
          <p class="search-description">
            가나다님의 상황에 맞는<br />채무 조정 제도를 알려드려요.
          </p>
        </div>
        <button class="search-button">제도 찾으러 가기</button>
      </div>

      <!-- 카테고리 필터 -->
      <div class="category-filter">
        <button class="category-btn active">전체</button>
        <button class="category-btn">주거</button>
        <button class="category-btn">보호·돌봄</button>
        <button class="category-btn">생활지원</button>
      </div>

      <div class="title-line"></div>

      <!-- 제목 섹션 -->
      <div class="title-section">
        <h2 class="section-title">맞춤 지원 정책</h2>
      </div>

      <!-- 검색창 -->
      <div class="search-box">
        <input
          type="text"
          placeholder="원하시는 키워드로 정책을 찾아보세요."
          class="search-input"
        />
        <div class="search-button-icon">
          <img
            src="/images/refresh-icon-blue.png"
            alt="검색"
            class="search-icon-small"
          />
        </div>
      </div>

      <!-- 정책 카드들 -->
      <div class="policy-cards">
        <div class="policy-card">
          <div class="card-image">
            <div class="image-placeholder"></div>
          </div>
          <div class="card-content">
            <h3 class="card-title">[오프라인/모집중] 청년도약계좌</h3>
            <div class="card-tags">
              <span class="tag offline">오프라인</span>
              <span class="date">2025년 7월 17일(금)</span>
            </div>
          </div>
        </div>

        <div class="policy-card">
          <div class="card-image">
            <div class="image-placeholder"></div>
          </div>
          <div class="card-content">
            <h3 class="card-title">[오프라인/모집중] 청년도약계좌</h3>
            <div class="card-tags">
              <span class="tag offline">오프라인</span>
              <span class="date">2025년 7월 17일(금)</span>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* 전체 페이지 스타일 */
.policy-page {
  width: 100%;
  background-color: #ffffff;
  font-family: 'Pretendard', sans-serif;
}

/* 메인 콘텐츠 */
.main-content {
  padding: 10px 10px;
}

/* 검색 섹션 */
.search-section {
  padding: 20px 0;
  text-align: center;
}

.search-icon {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.search-icon-img {
  width: 60px;
  height: 60px;
  object-fit: contain;
}

.search-title {
  font-size: 32px;
  font-weight: 400;
  color: #003e65;
  line-height: 1.2;
  margin: 0 0 16px 0;
}

.search-description {
  font-size: 16px;
  color: #003e65;
  line-height: 1.4;
  margin: 0 0 32px 0;
}

.search-button {
  background: none;
  border: none;
  border-bottom: 1px solid #003e65;
  color: #003e65;
  font-size: 16px;
  padding: 8px 12px;
  cursor: pointer;
  font-family: inherit;
}

/* 카테고리 필터 */
.category-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 32px;
  overflow-x: auto;
  padding: 0 4px;
}

.category-btn {
  background-color: #f9f9fd;
  border: 1px solid #72787f;
  border-radius: 30px;
  padding: 8px 16px;
  font-size: 10px;
  font-weight: 900;
  color: #72787f;
  white-space: nowrap;
  cursor: pointer;
  font-family: inherit;
}

.category-btn.active {
  background-color: #72787f;
  color: #f9f9fd;
}

/* 제목 섹션 */
.title-section {
  text-align: center;
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #000000;
  margin: 0 0 16px 0;
  letter-spacing: 2px;
}

.title-line {
  width: 100%;
  height: 1px;
  background-color: #e1e2e6;
  margin: 0;
}

/* 검색창 */
.search-box {
  position: relative;
  margin-bottom: 32px;
}

.search-input {
  width: 100%;
  height: 64px;
  background-color: #edeef2;
  border: none;
  border-radius: 8px;
  padding: 0 60px 0 24px;
  font-size: 13px;
  color: #72787f;
  font-family: inherit;
}

.search-input::placeholder {
  color: #72787f;
  letter-spacing: 1.3px;
}

.search-button-icon {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-icon-small {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

/* 정책 카드들 */
.policy-cards {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.policy-card {
  border: 2px solid #72787f;
  border-radius: 20px;
  overflow: hidden;
}

.card-image {
  height: 182px;
  background-color: #f5f5f5;
  position: relative;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e1e2e6 0%, #f9f9fd 100%);
}

.card-content {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 800;
  color: #000000;
  margin: 0 0 12px 0;
  letter-spacing: 1.6px;
  line-height: 1.3;
}

.card-tags {
  display: flex;
  align-items: center;
  gap: 12px;
}

.tag {
  background-color: #f9f9fd;
  border: 2px solid #003e65;
  border-radius: 30px;
  padding: 4px 12px;
  font-size: 10px;
  font-weight: 900;
  color: #003e65;
}

.tag.offline {
  background-color: #f9f9fd;
}

.date {
  font-size: 10px;
  font-weight: 900;
  color: #72787f;
  letter-spacing: 1px;
}

/* 반응형 디자인 */
@media (max-width: 375px) {
  .policy-page {
    width: 100%;
  }

  .title-line {
    width: 100%;
  }

  .category-filter {
    padding: 0;
  }
}
</style>
