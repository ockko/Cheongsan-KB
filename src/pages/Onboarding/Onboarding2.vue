<script setup>
import ProgressHeader from '@/components/domain/Onboarding/ProgressHeader.vue'
</script>

<template>
    <div class="page">
        <ProgressHeader :current="2" :total="3" />
        <div class="container">
            <div class="title-box">
                <h2>마이데이터<br/>연동 중</h2>
                <p>000님의 자산내역을 불러오는 중입니다. <br/> 잠시만 기다려주세요.</p>
            </div>
            <img src="/images/logo-blue.png" alt="로고" width="200" height="200" />
        </div>
    </div>
</template>

 
<style scoped>
.page {
  margin-top: 2rem;
}
.container {
  margin-top: 1rem;
  padding: 0 1rem;
  text-align: center;
}
.title-box {
  margin: 0 1rem 3.5rem 2rem;
}
.title-box h2 {
  font-size: 30px;
  font-weight: bold;
  color: navy;
  line-height: 2.3rem;
}
.title-box p {
  margin-top: 2rem;
  font-size: 16px;
  color: #666;
  line-height: 150%;
}
</style>