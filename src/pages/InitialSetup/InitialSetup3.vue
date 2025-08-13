<script setup>
import { useAuthStore } from '@/stores/auth.js';
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchUserLoans } from '@/api/initialSetup/initialSetup3.js';

import styles from '@/assets/styles/pages/InitialSetup/InitialSetup3.module.css';
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue';
import LoanItem from '@/components/domain/InitialSetup/LoanItem.vue';
import LoanModal from '@/components/domain/InitialSetup/InitialLoanAddModal.vue';

const router = useRouter();
const authStore = useAuthStore();

const nickname = computed(() => authStore.getUser().nickName || '000');

const loans = ref([]);

const confirmedIndexes = ref(new Set());
const clickedIndex = ref(null);
const isModalOpen = ref(false);

async function loadLoans() {
  try {
    const data = await fetchUserLoans();
    console.log('API 응답:', data);

    loans.value = data.map((loan) => ({
      id: loan.debtId,
      logo: '/images/logo-blue.png',
      institution: loan.organizationName,
      name: loan.debtName,
    }));
  } catch (error) {
    console.error('대출 목록 불러오기 실패:', error);
  }
}

// 페이지 로드 시 대출 목록 불러오기
onMounted(() => {
  loadLoans();
});

function handleLoanClick(index) {
  clickedIndex.value = index;

  const selectedLoan = loans.value[index];
  console.log('[Loan Click]', {
    index,
    debtId: selectedLoan?.id,
    institution: selectedLoan?.institution,
    name: selectedLoan?.name,
  });

  isModalOpen.value = true;
}

function confirmSelection() {
  if (clickedIndex.value !== null) {
    confirmedIndexes.value.add(clickedIndex.value);
  }
  isModalOpen.value = false;
  clickedIndex.value = null;
}

function cancelSelection() {
  clickedIndex.value = null;
  isModalOpen.value = false;
}

function goNext() {
  router.push('/initialSetup/page4');
}
</script>

<template>
  <div :class="styles.page">
    <ProgressHeader :current="2" :total="3" />
    <div :class="styles.container">
      <div :class="styles.titleBox">
        <h2 :class="styles.titleBoxMain">연동된 대출 정보 입력</h2>
        <p :class="styles.titleBoxSub">
          <span :class="styles.nickname">{{ nickname }}</span>
          님의 자산 내역을 연동하였습니다. <br />
          대출 상환 관리를 위한 추가 정보가 필요합니다. <br />
          대출 항목을 선택하여 입력해주세요.
        </p>
      </div>

      <div :class="styles.loanList">
        <template v-if="loans.length > 0">
          <LoanItem
            v-for="(item, index) in loans"
            :key="item.id"
            :logoUrl="item.logo"
            :institution="item.institution"
            :loanName="item.name"
            :selected="confirmedIndexes.has(index)"
            @click="handleLoanClick(index)"
          />
        </template>
        <p v-else :class="styles.emptyMessage">✔ 연동된 대출이 없습니다.</p>
      </div>
      <LoanModal
        v-if="isModalOpen"
        :logo="loans[clickedIndex]?.logo"
        :institution="loans[clickedIndex]?.institution"
        :name="loans[clickedIndex]?.name"
        :loanId="loans[clickedIndex]?.id"
        @confirm="confirmSelection"
        @cancel="cancelSelection"
      />
      <button
        :class="styles.nextButton"
        :disabled="confirmedIndexes.size !== loans.length"
        @click="goNext"
      >
        다음
      </button>
    </div>
  </div>
</template>
