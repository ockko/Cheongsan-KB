<script setup>
import styles from '@/assets/styles/components/study/StudyContentItem.module.css';

defineProps({
  contents: {
    type: Array,
    required: true
  },
});
</script>

<template>
  <div :class="styles.contentList">
    <div v-for="(item, index) in contents" :key="index" :class="styles.contentItem">
      <div
        :class="styles.thumbnail"
        :style="{ backgroundImage: `url(${item.thumbnailUrl})` }"
      ></div>

      <div :class="styles.contentText">
        <!-- 태그: 최대 3개까지 -->
        <div :class="styles.tagList">
          <span
            v-for="(tag, tagIndex) in item.tags.slice(0, 3)"
            :key="tagIndex"
            :class="styles.tag"
          >
            #{{ tag }}
          </span>
        </div>
        <p :class="styles.title">{{ item.title }}</p>
      </div>
    </div>
  </div>
</template>
