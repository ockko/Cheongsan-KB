<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import styles from '@/assets/styles/pages/InitialSetup/InitialSetup4.module.css';
import ProgressHeader from '@/components/domain/InitialSetup/ProgressHeader.vue';
import LoanItem from '@/components/domain/InitialSetup/LoanItem.vue';
import LoanAddModal from '@/components/domain/home/LoanAddModal.vue';

const router = useRouter();

const customLoans = ref([]);
const isModalOpen = ref(false);

const openModal = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const handleAddLoan = (loan) => {
  customLoans.value.push(loan);
  isModalOpen.value = false;
};

const goNext = () => {
  router.push('/home');
};
</script>

<template>
  <div :class="styles.page">
    <ProgressHeader :current="3" :total="3" />
    <div :class="styles.container">
      <div :class="styles.titleBox">
        <h2 :class="styles.titleBoxMain">대출 상품 추가</h2>
        <p :class="styles.titleBoxSub">
          연동되지 않은 대출 상품이 있다면 추가해주세요.
        </p>
      </div>

      <div :class="styles.loanList">
        <LoanItem
          v-for="(loan, index) in customLoans"
          :key="index"
          :logoUrl="loan.logo"
          :institution="loan.institution"
          :loanName="loan.name"
          :selected="loan.selected"
        />

        <div :class="styles.addBox" @click="openModal">
          <span>대출 상품 추가하기</span>
          <img
            src="/images/add-off.png"
            alt="추가"
            width="30px"
            height="30px"
          />
        </div>
      </div>

      <button :class="styles.nextButton" @click="goNext">
        티모청 시작하기
      </button>
      <LoanAddModal
        v-show="isModalOpen"
        :visible="isModalOpen"
        @close="closeModal"
        @add-loan="
          (loan) => {
            handleAddLoan(loan);
            closeModal();
          }
        "
      />
      <p v-if="isModalOpen">모달 상태 true</p>
    </div>
  </div>
</template>
