<template>
  <div :class="styles.backHeader">
    <div :class="styles.backInner">
      <button :class="styles.backBtn" @click="goBack" aria-label="뒤로가기">
        <i class="fa fa-arrow-left"></i>
      </button>
      <h1 :class="styles.backTitle">{{ title }}</h1>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import styles from "@/assets/styles/pages/LoanAnalysisResult.module.css";

const props = defineProps({
  title: { type: String, required: true },
});

const router = useRouter();
const goBack = () => {
  router.back();
};
</script>
