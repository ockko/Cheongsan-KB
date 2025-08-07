<script setup>
defineProps({
  logoUrl: String,
  institution: String,
  loanName: String,
  selected: Boolean
})
const emit = defineEmits(['click'])

function onClick() {
  emit('click')
}
</script>

<template>
    <div class="container">
        <div
            class="loan-item"
            :class="{ selected }"
            @click="onClick"
        >
            <img :src="logoUrl" alt="기관 로고" class="logo" />
            <div class="loan-info">
            <div class="loan-text">{{ institution }}/{{ loanName }}</div>
            </div>
            <img
            :src="selected ? '/images/checkbox-on.png' : '/images/checkbox-off.png'"
            alt="선택 상태 아이콘"
            class="status-icon"
            />
        </div>
    </div>
  </template>
  
  <style scoped>
  .loan-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 6px 16px;
    border-radius: 16px;
    box-shadow: 0 4px 8px rgba(0, 44, 75, 0.2);
    border: 2px solid transparent;
    transition: all 0.2s;
  }
  .loan-item.selected {
    border-color: #002c4b; /* 선택 시 테두리 진하게 */
    background-color: #fff;
    box-shadow: 0 4px 6px rgba(0, 44, 75, 0.2);
  }
  .logo {
    width: 32px;
    height: 32px;
    border-radius: 6px;
    margin-left: 8px;
    margin-right: 8px;
  }
  .loan-info {
    flex: 1;
    font-size: 14px;
    color: #222;
  }
  .status-icon {
    width: 20px;
    height: 20px;
  }
  </style>
  