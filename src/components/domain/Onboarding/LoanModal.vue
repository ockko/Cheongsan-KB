<template>
    <div class="modal-overlay">
      <div class="modal">
        <p class="message">이 대출 항목을 선택하시겠습니까?</p>
        <div class="button-group">
          <button @click="$emit('confirm')" class="confirm-btn">확인</button>
          <button @click="$emit('cancel')" class="cancel-btn">취소</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  defineEmits(['confirm', 'cancel'])
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
  }
  
  .modal {
    background: white;
    border-radius: 12px;
    padding: 2rem;
    width: 280px;
    text-align: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  }
  
  .message {
    margin-bottom: 1.5rem;
    font-weight: bold;
  }
  
  .button-group {
    display: flex;
    justify-content: space-around;
  }
  
  .confirm-btn {
    background-color: #007bff;
    color: white;
    padding: 0.6rem 1.2rem;
    border-radius: 6px;
    border: none;
    cursor: pointer;
  }
  
  .cancel-btn {
    background-color: #ccc;
    color: black;
    padding: 0.6rem 1.2rem;
    border-radius: 6px;
    border: none;
    cursor: pointer;
  }
  </style>
  