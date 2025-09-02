<script setup>
import { ref } from 'vue';
import RepaymentSimulationInput from '@/pages/RepaymentSimulationInput.vue';
import LoanSimulation from '@/pages/LoanFormSection.vue';
import styles from '@/assets/styles/components/simulation/SimulationTabMenu.module.css';
const activeTab = ref('repayment');
</script>

<template>
  <div>
    <nav :class="styles.tabMenu">
      <span
        :class="[
          styles.tab,
          activeTab === 'repayment' ? styles.active : styles.inactive,
        ]"
        @click="activeTab = 'repayment'"
      >
        상환 시뮬레이션
      </span>

      <span
        :class="[
          styles.tab,
          activeTab === 'loan' ? styles.active : styles.inactive,
        ]"
        @click="activeTab = 'loan'"
      >
        대출 시뮬레이션
      </span>
    </nav>

    <div :class="styles.tabContent">
      <RepaymentSimulationInput v-if="activeTab === 'repayment'" />
      <LoanSimulation v-else-if="activeTab === 'loan'" />
    </div>
  </div>
</template>
