<template>
  <div>
    <nav class="tab-menu">
      <span
        :class="['tab', activeTab === 'repayment' ? 'active' : 'inactive']"
        @click="activeTab = 'repayment'"
        >상환 시뮬레이션</span
      >

      <span
        :class="['tab', activeTab === 'loan' ? 'active' : 'inactive']"
        @click="activeTab = 'loan'"
        >대출 시뮬레이션</span
      >
    </nav>

    <div class="tab-content">
      <RepaymentSimulationInput v-if="activeTab === 'repayment'" />
      <LoanSimulation v-else />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import RepaymentSimulationInput from '@/pages/RepaymentSimulationInput.vue';
import LoanSimulation from '@/pages/LoanSimulation.vue';

const activeTab = ref('repayment');
</script>

<style scoped>
.tab-menu {
  display: flex;
  width: 100%;
}

.tab {
  flex: 1;
  text-align: center;
  cursor: pointer;
  font-size: 16px;
}

.active {
  border-bottom: 2px solid var(--color-main);
}
.inactive {
  color: gray;
}
.tab-content {
  padding: 1rem;
}
.tab + .tab {
  margin-left: 24px; /* 탭 사이에 공간 부여 */
}
</style>
