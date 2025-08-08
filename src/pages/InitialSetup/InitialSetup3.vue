<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import styles from '@/assets/styles/pages/InitialSetup/InitialSetup3.module.css'
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue'
import LoanItem from '@/components/domain/InitialSetup/LoanItem.vue'
import LoanModal from '@/components/domain/InitialSetup/LoanModal.vue'
  
const router = useRouter()

const loans = [
{
    id: 1,
    logo: '/images/kakaobank-logo.png',
    institution: '카카오뱅크',
    name: '파산대출'
},
{
    id: 2,
    logo: '/images/kakaobank-logo.png',
    institution: '사채업자',
    name: '그 까이꺼 대출'
},
{
    id: 3,
    logo: '/images/kakaobank-logo.png',
    institution: '뭐시기저축은행',
    name: '대애출'
},
{
    id: 4,
    logo: '/images/kakaobank-logo.png',
    institution: '카카오뱅크',
    name: '탕진대출'
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
    confirmedIndexes.value.add(clickedIndex.value)
  }
  isModalOpen.value = false
  clickedIndex.value = null
}

function cancelSelection() {
  clickedIndex.value = null
  isModalOpen.value = false
}

function goNext() {
    router.push('/onboarding/page4')
}

</script>

<template>
    <div :class="styles.page">
        <ProgressHeader :current="2" :total="3" />
        <div :class="styles.container">
            <div :class="styles.titleBox">
                <h2 :class="styles.titleBoxMain">연동된 대출 정보 입력</h2>
                <p :class="styles.titleBoxSub">
                000님의 자산 내역을 연동하였습니다. <br />
                대출 상환 관리를 위한 추가 정보가 필요합니다. <br />
                대출 항목을 선택하여 입력해주세요.
                </p>
            </div>

            <div :class="styles.loanList">
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
                :logo="loans[clickedIndex]?.logo"
                :institution="loans[clickedIndex]?.institution"
                :name="loans[clickedIndex]?.name"
                :loanId="loans[clickedIndex]?.id" @confirm="confirmSelection"
                @cancel="cancelSelection"
            />
            <button
            :class="styles.nextButton"
            :disabled="confirmedIndexes.size !== loans.length"
            @click="goNext">
                다음
            </button>
        </div>
    </div>
</template>