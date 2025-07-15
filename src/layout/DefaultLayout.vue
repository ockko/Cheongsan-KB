<script setup>
import Header from '@/components/common/Header.vue';
//Layout은 페이지의 전체적인 구조/골격을 정의하는 템플릿입니다.
</script>

<template>
  <div>
    <Header />
    <div>
      <slot></slot>
    </div>
  </div>
</template>
