<script setup>
import { computed, ref, watch } from "vue";
import styles from "@/assets/styles/components/Analysis.module.css";

const props = defineProps({
  products: { type: Array, default: () => [] },
  pageSize: { type: Number, default: 5 },
});

const pageSize = computed(() => Math.max(1, props.pageSize));
const currentPage = ref(1);

const totalPages = computed(() => {
  const len = Array.isArray(props.products) ? props.products.length : 0;
  return Math.max(1, Math.ceil(len / pageSize.value));
});

const pagedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return (props.products || []).slice(start, start + pageSize.value);
});

const prev = () => (currentPage.value = Math.max(1, currentPage.value - 1));
const next = () =>
  (currentPage.value = Math.min(totalPages.value, currentPage.value + 1));

watch([() => props.products, pageSize], () => (currentPage.value = 1));
watch(totalPages, (tp) => {
  if (currentPage.value > tp) currentPage.value = tp;
});

const formatRate = (v) => {
  if (v == null) return "-%";
  const parts = Array.isArray(v)
    ? v
    : String(v)
        .split(/[^0-9.]+/)
        .filter(Boolean);
  const nums = parts
    .map((n) => Number(n))
    .filter((n) => !Number.isNaN(n))
    .sort((a, b) => a - b);
  if (nums.length === 0) return "-%";
  return nums.length === 1
    ? `${nums[0].toFixed(1)}%`
    : `${nums[0].toFixed(1)}% ~ ${nums[nums.length - 1].toFixed(1)}%`;
};

const parseWon = (v) => {
  if (v == null) return 0;
  if (typeof v === "number") return v;
  const s = String(v);
  if (s.includes("만원")) {
    const n = Number(s.replace(/[^0-9.]/g, ""));
    return Number.isNaN(n) ? 0 : Math.round(n * 10000);
  }
  if (s.includes("억원")) {
    const n = Number(s.replace(/[^0-9.]/g, ""));
    return Number.isNaN(n) ? 0 : Math.round(n * 100000000);
  }
  const n = Number(s.replace(/[^0-9.]/g, ""));
  return Number.isNaN(n) ? 0 : Math.round(n);
};

const formatAmount = (v) => {
  const n = parseWon(v);
  const abs = Math.abs(n);
  if (abs >= 100000000) return `${(abs / 100000000).toFixed(1)}억원`;
  if (abs >= 10000) return `${Math.round(abs / 10000)}만원`;
  return `${abs.toLocaleString()}원`;
};

const getSafeUrl = (url) => {
  if (!url || typeof url !== "string") return "#";
  if (/^https?:\/\//i.test(url)) return url;
  return `http://${url}`;
};
</script>

<template>
  <div :class="styles.recommendationsContainer">
    <div :class="styles.recommendHeader">
      <i class="far fa-thumbs-up"></i>

      <h3 :class="styles.title">대출 대안 추천</h3>
    </div>
    <span :class="styles.info"
      >정부 지원 상품이나 저금리 대출 등 더 나은 대안을 알려드립니다.</span
    >

    <div v-if="pagedProducts.length > 0" :class="styles.cardList">
      <div
        v-for="(p, idx) in pagedProducts"
        :key="
          p.id ??
          p.productId ??
          `${p.productName}-${(currentPage - 1) * pageSize + idx}`
        "
        :class="styles.card"
      >
        <div :class="styles.product">{{ p.productName || "-" }}</div>
        <span :class="styles.institution">{{ p.institutionName || "-" }}</span>

        <div :class="styles.cardBody">
          <div :class="styles.cardInfo">
            <p>연 {{ formatRate(p.interestRate) }}</p>
            <p>최대 {{ formatAmount(p.loanLimit) }}</p>
          </div>
          <a
            :href="getSafeUrl(p.siteUrl)"
            target="_blank"
            rel="noopener noreferrer"
            :class="styles.siteButton"
          >
            사이트
          </a>
        </div>
      </div>
    </div>

    <div v-else :class="styles.noProduct">추천 가능한 상품이 없습니다.</div>

    <div v-if="totalPages > 1" :class="styles.pagination">
      <button
        :class="[styles.pageBtn, currentPage === 1 && styles.pageBtnDisabled]"
        @click="prev"
        :disabled="currentPage === 1"
        aria-label="이전 페이지"
      >
        이전
      </button>
      <span :class="styles.pageInfo">{{ currentPage }} / {{ totalPages }}</span>
      <button
        :class="[
          styles.pageBtn,
          currentPage === totalPages && styles.pageBtnDisabled,
        ]"
        @click="next"
        :disabled="currentPage === totalPages"
        aria-label="다음 페이지"
      >
        다음
      </button>
    </div>
  </div>
</template>
