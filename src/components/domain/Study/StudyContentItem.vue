<script setup>
defineProps({
  contents: {
    type: Array,
    required: true,
    // 각 item: { thumbnailUrl: string, tags: string[], title: string }
  },
});
</script>

<template>
  <div class="content-list">
    <div v-for="(item, index) in contents" :key="index" class="content-item">
      <div
        class="thumbnail"
        :style="{ backgroundImage: `url(${item.thumbnailUrl})` }"
      ></div>

      <div class="content-text">
        <!-- 태그: 최대 3개까지 -->
        <div class="tag-list">
          <span
            v-for="(tag, tagIndex) in item.tags.slice(0, 3)"
            :key="tagIndex"
            class="tag"
          >
            #{{ tag }}
          </span>
        </div>
        <p class="title">{{ item.title }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}
.content-item {
  display: flex;
  gap: 15px;
  align-items: flex-start;
}
.thumbnail {
  width: 90px;
  height: 70px;
  border-radius: 12px;
  background-color: var(--color-gray2);
  background-size: cover;
  background-position: center;
  flex-shrink: 0;
}
.content-text {
  margin-top: 2px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}
.tag-list {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.tag {
  background-color: var(--color-light2);
  color: var(--color-main);
  user-select: none;
  line-height: 1;
  font-size: 12px;
  font-weight: 400;
  padding: 3px 8px;
  border-radius: 10px;

  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: fit-content;
  width: fit-content;
}
.title {
  font-size: 15px;
  line-height: 1.3;
  color: #222;
  margin: 0;

  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-word;
}
</style>
