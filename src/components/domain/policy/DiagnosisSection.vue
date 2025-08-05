<script setup>
import styles from '@/assets/styles/components/policy/Policy.module.css';
</script>

<template>
  <div :class="styles.diagnosisSectionContainer">
    <!-- 검색 아이콘 섹션 -->
    <div :class="styles.searchSection">
      <div :class="styles.searchIcon">
        <img
          src="/images/policy-icon.png"
          alt="검색"
          :class="styles.searchIconImg"
        />
      </div>
      <div :class="styles.searchText">
        <h2 :class="styles.searchTitle">내게 맞는<br />채무 조정 제도 찾기</h2>
        <p :class="styles.searchDescription">
          가나다님의 상황에 맞는<br />채무 조정 제도를 알려드려요.
        </p>
      </div>
    </div>
    <button :class="styles.searchButton">제도 찾으러 가기</button>
  </div>
</template>
