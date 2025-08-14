<script setup>
import styles from "@/assets/styles/components/admin/DeleteModal.module.css";

const props = defineProps({
  showModal: { type: Boolean, required: true },
  memberName: { type: String, required: true },
});
const emit = defineEmits(["confirm", "cancel"]);
</script>

<template>
  <teleport to="body">
    <div v-if="props.showModal" :class="styles.modal">
      <div :class="styles.modalContent">
        <h2 :class="styles.modalTitle">회원 삭제</h2>
        <p :class="styles.modalText">
          <strong :class="styles.highlight">{{ props.memberName }}</strong>
          님을<br />
          삭제하시겠습니까?
        </p>
        <div :class="styles.modalActions">
          <button :class="styles.confirmBtn" @click="emit('confirm')">
            삭제
          </button>
          <button :class="styles.cancelBtn" @click="emit('cancel')">
            취소
          </button>
        </div>
      </div>
    </div>
  </teleport>
</template>
