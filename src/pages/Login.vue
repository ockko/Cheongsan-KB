<script setup>
import { useAuthStore } from "@/stores/auth";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import NaverAuthButton from "@/components/domain/Signup/NaverAuthButton.vue";
import styles from "@/assets/styles/pages/Login.module.css";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const formData = ref({ username: "", password: "" });
const errorMessage = ref("");
const isSubmitting = ref(false);

if (route.query.error === "session_expired") {
  errorMessage.value = "세션이 만료되었습니다. 다시 로그인해주세요.";
} else if (route.query.error === "login_required") {
  errorMessage.value = "로그인이 필요한 서비스입니다.";
}

const handleLogin = async () => {
  if (!formData.value.username.trim()) {
    errorMessage.value = "아이디를 입력해주세요.";
    return;
  }
  if (!formData.value.password.trim()) {
    errorMessage.value = "비밀번호를 입력해주세요.";
    return;
  }

  isSubmitting.value = true;
  errorMessage.value = "";

  try {
    // 로그인 (서버/스토어 구현에 따라 result에 사용자 정보 포함)
    const result = await authStore.login(formData.value);

    // 1) 로그인 응답에서 role 사용
    const role = result?.role ?? authStore.state.user?.role; // 둘 중 있는 값 사용

    if (role === "ADMIN") {
      router.push("/admin/users");
    } else {
      router.push("/home");
    }
  } catch (error) {
    errorMessage.value = error?.message ?? "로그인에 실패했습니다.";
  } finally {
    isSubmitting.value = false;
  }
};

// 네이버 로그인 핸들러
const handleNaverAuthStart = () => {
  errorMessage.value = "";
};
const handleNaverAuthSuccess = () => {};
const handleNaverAuthError = (error) => {
  errorMessage.value = error?.message ?? String(error);
};
</script>

<template>
  <div :class="styles.loginPage">
    <div :class="styles.loginContainer">
      <!-- 타이틀 -->
      <h1 :class="styles.loginTitle">로그인</h1>
      <p :class="styles.loginSubtitle">티끌모아 청산에 오신 걸 환영합니다.</p>

      <!-- 로그인 폼 -->
      <form @submit.prevent="handleLogin" :class="styles.loginForm">
        <!-- 에러 메시지 -->
        <div v-if="errorMessage" :class="styles.loginErrorMessage">
          {{ errorMessage }}
        </div>

        <!-- 아이디 입력 -->
        <div :class="styles.loginInputGroup">
          <input
            v-model="formData.username"
            type="text"
            placeholder="아이디"
            :class="styles.loginInput"
            :disabled="isSubmitting"
          />
        </div>

        <!-- 비밀번호 입력 -->
        <div :class="styles.loginInputGroup">
          <input
            v-model="formData.password"
            type="password"
            placeholder="비밀번호"
            :class="styles.loginInput"
            :disabled="isSubmitting"
          />
        </div>

        <!-- 로그인 버튼 -->
        <button
          type="submit"
          :class="[styles.loginButton, { [styles.loading]: isSubmitting }]"
          :disabled="isSubmitting"
        >
          {{ isSubmitting ? "로그인 중..." : "Login" }}
        </button>
      </form>

      <!-- SNS 로그인 구분선 -->
      <div :class="styles.loginDivider">
        <span>SNS 계정 로그인</span>
      </div>

      <!-- 네이버 로그인 버튼 (컴포넌트 사용) -->
      <NaverAuthButton
        type="login"
        :disabled="isSubmitting"
        @auth-start="handleNaverAuthStart"
        @auth-success="handleNaverAuthSuccess"
        @auth-error="handleNaverAuthError"
      />

      <!-- 하단 링크들 -->
      <div :class="styles.loginFooter">
        <button :class="styles.loginLinkButton">아이디 찾기</button>
        <span :class="styles.loginSeparator">|</span>
        <button :class="styles.loginLinkButton">비밀번호 찾기</button>
      </div>
    </div>
  </div>
</template>
