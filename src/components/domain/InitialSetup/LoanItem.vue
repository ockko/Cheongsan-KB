<script setup>
import styles from '@/assets/styles/components/InitialSetup/LoanItem.module.css';

defineProps({
  logoUrl: String,
  institution: String,
  loanName: String,
  selected: Boolean,
});
const emit = defineEmits(['click']);

const onClick = () => {
  emit('click');
};
</script>

<template>
  <div :class="[styles.loanItem, selected && styles.selected]" @click="onClick">
    <img :src="logoUrl" alt="기관 로고" :class="styles.logo" />
    <div :class="styles.loanInfo">
      <span :class="styles.institutionName">{{ institution }}</span>
      <br />
      <span :class="styles.loanName">{{ loanName }}</span>
    </div>
    <img
      :src="selected ? '/images/checkbox-on.png' : '/images/checkbox-off.png'"
      alt="완료 상태 아이콘"
      :class="styles.statusIcon"
    />
  </div>
</template>
