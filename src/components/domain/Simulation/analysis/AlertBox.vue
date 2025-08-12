<script setup>
import styles from "@/assets/styles/components/Analysis.module.css";
</script>

<template>
  <div :class="styles.alertBox">
    <div :class="styles.icon"></div>
    <p :class="styles.text">
      무분별한 대출은 재정에 큰 부담이 될 수 있습니다.<br />
      시뮬레이션 결과는 참고용이며, 실제 대출 실행 전에는 반드시 신중하게
      검토하시기 바랍니다.
    </p>
  </div>
</template>
