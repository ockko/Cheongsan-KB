<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useMyPageStore } from '@/stores/mypage';
import styles from '@/assets/styles/pages/mypage/MyPage.module.css';

const router = useRouter();
const store = useMyPageStore();

onMounted(() => {
  store.fetchMyProfile();
});

const goTo = (path) => {
  router.push(path);
};

const logout = async () => {
  await store.logout();
  router.push('/login'); // 로그아웃 후 로그인 페이지로 이동
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div :class="styles.container">
    <header :class="styles.header">
      <i
        class="fa-solid fa-arrow-left"
        @click="goBack"
        :class="styles.arrowBack"
      ></i>
      <h1 :class="styles.title" class="text-light">내 정보</h1>
    </header>

    <p :class="styles.sectionTitle" class="text-light">내 정보 관리</p>

    <!-- 아이디 -->
    <div :class="styles.cardList">
      <div :class="styles.cardRow">
        <div :class="styles.cardContent">
          <span :class="styles.cardTitle">아이디</span>
          <span :class="styles.cardValue">{{ store.userId }}</span>
        </div>
      </div>
    </div>

    <!-- 이름 -->
    <div
      :class="[styles.cardList, styles.clickable]"
      @click="goTo('/mypage/edit/name')"
    >
      <div :class="styles.cardRow">
        <div :class="styles.cardContent">
          <span :class="styles.cardTitle">이름</span>
          <span :class="styles.cardValue">{{ store.nickname }}</span>
        </div>
        <span :class="styles.cardArrow">
          <i class="fa-solid fa-greater-than"></i>
        </span>
      </div>
    </div>

    <!-- 이메일 -->
    <div
      :class="[styles.cardList, styles.clickable]"
      @click="goTo('/mypage/edit/email')"
    >
      <div :class="styles.cardRow">
        <div :class="styles.cardContent">
          <span :class="styles.cardTitle">이메일</span>
          <span :class="styles.cardValue">{{ store.email }}</span>
        </div>
        <span :class="styles.cardArrow">
          <i class="fa-solid fa-greater-than"></i>
        </span>
      </div>
    </div>

    <!-- 비밀번호 변경 -->
    <div
      :class="[styles.cardList, styles.clickable]"
      @click="goTo('/mypage/edit/password')"
    >
      <div :class="[styles.cardRow, styles.singleLine]">
        <span :class="styles.cardTitle">비밀번호 변경</span>
        <span :class="styles.cardArrow">
          <i class="fa-solid fa-greater-than"></i>
        </span>
      </div>
    </div>

    <!-- 회원탈퇴 -->

    <div
      :class="[styles.cardList, styles.clickable]"
      @click="goTo('/mypage/withdraw')"
    >
      <div :class="[styles.cardRow, styles.singleLine]">
        <span :class="styles.cardTitle">회원탈퇴</span>
        <span :class="styles.cardArrow">
          <i class="fa-solid fa-greater-than"></i>
        </span>
      </div>
    </div>

    <!-- 로그아웃 -->
    <button :class="styles.logoutButton" @click="logout">로그아웃</button>
  </div>
</template>
