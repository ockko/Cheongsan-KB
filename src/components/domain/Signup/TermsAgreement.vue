<script setup>
import { reactive, computed, defineEmits } from 'vue';
import styles from '@/assets/styles/components/Signup/TermsAgreement.module.css';

const emit = defineEmits(['validation-change']);

// 이용약관 동의 상태
const agreements = reactive({
  service: false,
  privacy: false,
  marketing: false,
});

// 필수 약관 동의 확인
const isRequiredAgreementsValid = computed(() => {
  return agreements.service && agreements.privacy;
});

// 이용약관 체크박스 토글
const toggleAgreement = (type) => {
  agreements[type] = !agreements[type];
  emitValidation();
};

// 유효성 검사 결과를 부모에게 전달
const emitValidation = () => {
  emit('validation-change', {
    isValid: isRequiredAgreementsValid.value,
    agreements: { ...agreements },
  });
};
</script>

<template>
  <div :class="styles.inputGroup">
    <div :class="styles.label">이용약관</div>
    <div :class="styles.agreementsList">
      <div :class="styles.agreementItem" @click="toggleAgreement('service')">
        <img
          :src="
            agreements.service
              ? '/images/checkbox-on.png'
              : '/images/checkbox-off.png'
          "
          alt="서비스 이용약관 동의"
          :class="styles.checkboxIcon"
        />
        <span :class="styles.agreementText">서비스 이용약관 동의 (필수)</span>
      </div>
      <div :class="styles.agreementItem" @click="toggleAgreement('privacy')">
        <img
          :src="
            agreements.privacy
              ? '/images/checkbox-on.png'
              : '/images/checkbox-off.png'
          "
          alt="개인정보 수집 및 이용 동의"
          :class="styles.checkboxIcon"
        />
        <span :class="styles.agreementText"
          >개인정보 수집 및 이용 동의 (필수)</span
        >
      </div>
      <div :class="styles.agreementItem" @click="toggleAgreement('marketing')">
        <img
          :src="
            agreements.marketing
              ? '/images/checkbox-on.png'
              : '/images/checkbox-off.png'
          "
          alt="마케팅 및 광고성 정보 수신 동의"
          :class="styles.checkboxIcon"
        />
        <span :class="styles.agreementText"
          >마케팅 및 광고성 정보 수신 동의 (선택)</span
        >
      </div>
    </div>
  </div>
</template>
