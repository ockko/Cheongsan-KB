<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const navItems = [
  {
    name: 'Policy',
    icon: 'policy-icon.png',
    clickedIcon: 'policy-icon-clicked.png',
    route: '/policy',
  },
  {
    name: 'Calendar',
    icon: 'calendar-icon.png',
    clickedIcon: 'calendar-icon-clicked.png',
    route: '/calendar',
  },
  {
    name: 'Home',
    icon: 'home-icon.png',
    clickedIcon: 'home-icon-clicked.png',
    route: '/home',
  },
  {
    name: 'Simulation',
    icon: 'simulator-icon.png',
    clickedIcon: 'simulator-icon-clicked.png',
    route: '/simulation',
  },
  {
    name: 'Study',
    icon: 'study-icon.png',
    clickedIcon: 'study-icon-clicked.png',
    route: '/study',
  },
];

const isActive = (itemRoute) => {
  return route.path === itemRoute;
};

const getIcon = (item) => {
  return isActive(item.route) ? item.clickedIcon : item.icon;
};

const navigateTo = (route) => {
  router.push(route);
};
</script>

<template>
  <nav class="navigation-bar">
    <button
      v-for="item in navItems"
      :key="item.name"
      class="nav-item"
      :class="{ 'nav-item--active': isActive(item.route) }"
      @click="navigateTo(item.route)"
    >
      <img
        :src="`/images/${getIcon(item)}`"
        :alt="item.name"
        class="nav-icon"
      />
      <span class="nav-label">{{ item.name }}</span>
    </button>
  </nav>
</template>

<style scoped>
.navigation-bar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 375px;
  height: 60px;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 8px 16px;
  box-sizing: border-box;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  box-shadow: 0 -4px 16px rgba(0, 62, 101, 0.1);
  z-index: 1000;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
  flex: 1;
  max-width: 60px;
}

.nav-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.nav-label {
  font-family: var(--font-family);
  font-size: 10px;
  font-weight: 400;
  color: var(--color-gray0);
  text-align: center;
}

.nav-item--active .nav-label {
  color: var(--color-main);
  font-weight: 500;
}
</style>
