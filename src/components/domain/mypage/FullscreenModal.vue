<script setup>
import { ref } from 'vue';
import styles from '@/assets/styles/components/mypage/FullscreenModal.module.css';

defineProps({
  title: { type: String, required: true },
});

const emit = defineEmits(['close']);
const isClosing = ref(false);

const close = () => {
  isClosing.value = true;
  setTimeout(() => emit('close'), 300);
};
</script>

<template>
  <div :class="styles.modalOverlay" @click.self="close">
    <div :class="[styles.modalContainer, { [styles.closing]: isClosing }]">
      <!-- Header -->
      <header :class="styles.modalHeader">
        <i
          :class="[styles.backBtn, 'fa-solid', 'fa-arrow-left']"
          @click="close"
        ></i>
        <h1 :class="styles.modalTitle" class="text-light">{{ title }}</h1>
      </header>

      <!-- Body -->
      <main :class="styles.modalBody">
        <slot name="description" />
        <slot name="input" />
        <slot />
      </main>

      <!-- Footer -->
      <footer :class="styles.modalFooter">
        <slot name="footer" />
      </footer>
    </div>
  </div>
</template>
