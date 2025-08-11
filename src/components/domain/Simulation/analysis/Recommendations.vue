<script setup>
import styles from "@/assets/styles/components/Analysis.module.css";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
</script>

<template>
  <div :class="styles.recommendContainer">
    <div :class="styles.recommendationsHeader">
      <FontAwesomeIcon :icon="['far', 'face-smile']" />
      <h3 :class="styles.recommendTitle">추천 행동</h3>
    </div>

    <ul :class="styles.recommendList">
      <li>
        정부 지원 대출 먼저 확인<br />
        <small>위의 저금리 대안 상품들을 먼저 검토해보세요</small>
      </li>
      <li>
        기존 부채 정리 우선 고려<br />
        <small>새 대출보다는 기존 고금리 부채 정리를 고려해보세요</small>
      </li>
      <li>
        상환 계획 재검토<br />
        <small>대출 후 변경된 상환 계획을 다시 확인해보세요</small>
      </li>
    </ul>
  </div>
</template>
