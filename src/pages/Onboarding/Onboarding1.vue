<script setup>
import ProgressHeader from '@/components/domain/Onboarding/ProgressHeader.vue'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const nickname = ref('')
const router = useRouter()

function goNext() {
  const nicknameValue = nickname.value.trim()

  const nicknameRegex = /^[가-힣a-zA-Z0-9_]{2,10}$/

  if (!nicknameRegex.test(nicknameValue)) {
    alert('닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용 가능하며 2~10자 이내여야 합니다.')
    return
  }

  // 유효성 통과 시 다음 페이지로 이동
  router.push('/onboarding/page2')
}

</script>

<template>
    <div class="page">
        <ProgressHeader :current="1" :total="3" />
        <div class="container">
            <div class="title-box">
                <h2>닉네임 설정</h2>
                <p>티끌모아청산에서 사용하실<br/>
                닉네임을 입력해주세요.</p>
            </div>
    
            <div class="input">
                <input class="nickname-input" v-model="nickname" maxlength="10" placeholder="닉네임을 입력하세요." />
                <p class="text-limit">(최대 10자)</p>
            </div>
            <button class="next-button" @click="goNext">다음</button>
        </div>
    </div>
</template>
  
  <style scoped>
  .page {
    margin-top: 2rem;
  }
  .container {
    margin-top: 1rem;
    padding: 0 1.5rem;
    text-align: center;
  }
  
  .title-box {
    margin: 0 2rem 6rem 2rem;
  }
  
  .title-box h2 {
    font-size: 30px;
    font-weight: bold;
    color: navy;
  }
  .title-box p {
  margin-top: 2rem;
  font-size: 16px;
  color: #666;
  line-height: 150%;
  }
  .input {
    margin-bottom: 3rem;

  }

  .nickname-input {
    width: 100%;
    padding: 1rem;
    border: 2px solid grey;
    border-radius: 18px;
    text-align: center;
    font-size: 14px;
  }

  .text-limit {
    margin-top: -1rem;
    text-align: right;
    font-size: 14px;
    color: gray;
    margin-right: 8px;
  }
  
  .next-button {
    width: 100%;
    height: 44px;
    background-color: #00497a;
    color: #fff;
    border: none;
    border-radius: 14px;
    font-weight: 600;
    font-size: 16px;
    cursor: pointer;
  }
  </style>
  