<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import ProgressHeader from '@/components/domain/Onboarding/ProgressHeader.vue'
import LoanItem from '@/components/domain/Onboarding/LoanItem.vue'
import LoanModal from '@/components/domain/Onboarding/LoanModal.vue'
  
const router = useRouter()

const loans = [
{
    logo: '/images/kakaobank-logo.png',
    institution: '카카오뱅크',
    name: '개쩌는 대출'
},
{
    logo: '/images/kakaobank-logo.png',
    institution: '사채업자뱅크',
    name: '그 까이꺼 대출'
},
{
    logo: '/images/kakaobank-logo.png',
    institution: '사채업자뱅크',
    name: '그 까이꺼 대출'
},
{
    logo: '/images/kakaobank-logo.png',
    institution: '카카오뱅크',
    name: '개쩌는 대출'
}
]
  
const confirmedIndexes = ref(new Set())   // 최종 선택된 인덱스
const clickedIndex = ref(null)       // 현재 클릭된 인덱스
const isModalOpen = ref(false)       // 모달 표시 여부

function handleLoanClick(index) {
  clickedIndex.value = index
  isModalOpen.value = true
}

function confirmSelection() {
  if (clickedIndex.value !== null) {
    confirmedIndexes.value.add(clickedIndex.value) // ✅ 선택 추가
  }
  isModalOpen.value = false
  clickedIndex.value = null
}

// 모달 취소
function cancelSelection() {
  clickedIndex.value = null
  isModalOpen.value = false
}

function goNext() {
    router.push('/onboarding/page1')
}

</script>

<template>
    <div class="page">
        <ProgressHeader :current="2" :total="3" />
        <div class="container">
            <div class="title-box">
                <h2>연동된 대출 정보 입력</h2>
                <p>000님의 자산 내역을 연동하였습니다. <br/> 
                    대출 상환 관리를 위한 추가 정보가 필요합니다. <br/>
                    대출 항목을 선택하여 입력해주세요.</p>
            </div>

            <div class="loanList">
                <LoanItem
                    v-for="(item, index) in loans"
                      :key="index"
                      :logoUrl="item.logo"
                      :institution="item.institution"
                      :loanName="item.name"
                      :selected="confirmedIndexes.has(index)"
                      @click="handleLoanClick(index)"
                 />
            </div>
            <LoanModal
                    v-if="isModalOpen"
                    @confirm="confirmSelection"
                    @cancel="cancelSelection"
                 />
            <button
            class="next-button"
            :disabled="confirmedIndexes.size !== loans.length"
            @click="goNext">
                다음
            </button>
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
  margin: 0 1rem 2rem 2rem;
}
.title-box h2 {
  font-size: 30px;
  font-weight: bold;
  color: navy;
  line-height: 2.3rem;
}
.title-box p {
  margin-top: 1rem;
  font-size: 15px;
  color: #666;
  line-height: 150%;
}
.loanList{
  margin: 0 1rem 2rem 1rem;
  max-height: 300px;
  overflow-x: auto;

  padding-right: 4px;
  padding-bottom: 10px;
  
  scrollbar-width: thin;
  scrollbar-color: #ccc transparent;
}

/* 크롬용 */
.loanList::-webkit-scrollbar {
  width: 6px;
}
.loanList::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 10px;
}
.loanList::-webkit-scrollbar-track {
  background: transparent;
}
.next-button {
    width: calc(100% - 4rem); /* 좌우 여백 2rem씩 고려 */
    margin: 0 2rem;
    height: 44px;
    padding: 14px;
    background-color: #00497a;
    color: #fff;
    border: none;
    border-radius: 14px;
    font-weight: 600;
    font-size: 16px;
    cursor: pointer;
}
.next-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>