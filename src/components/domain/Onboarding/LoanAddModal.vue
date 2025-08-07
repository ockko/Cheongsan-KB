<script setup>
import { ref } from 'vue'

const emit = defineEmits(['close', 'add-loan'])

const institution = ref('')
const loanName = ref('')

function submit() {
  if (!institution.value || !loanName.value) return

  emit('add-loan', {
    institution: institution.value,
    name: loanName.value,
    logo: '/images/kakaobank-logo.png' // 나중에 선택 가능하도록 바꿔도 됨
  })
}

function close() {
  emit('close')
}
</script>

<template>
  <div class="overlay">
    <div class="modal">
      <h3>대출 정보 입력</h3>
      <input v-model="institution" placeholder="대출 기관" />
      <input v-model="loanName" placeholder="대출 상품명" />
      <div class="actions">
        <button @click="submit">추가하기</button>
        <button @click="close">취소</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background: #fff;
  padding: 24px;
  border-radius: 16px;
  width: 280px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0,0,0,0.2);
}

input {
  width: 100%;
  padding: 8px;
  margin-top: 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
}

.actions button {
  flex: 1;
  padding: 10px;
  margin: 0 4px;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  background-color: #002c4b;
  color: white;
}
</style>
