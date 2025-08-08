<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useModalStore } from '@/stores/modal';
import styles from '@/assets/styles/components/NavigationBar.module.css';

const route = useRoute();
const router = useRouter();
const modalStore = useModalStore();

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
  <nav v-show="!modalStore.isAnyModalOpen" :class="styles.navigationBar">
    <button
      v-for="item in navItems"
      :key="item.name"
      :class="[
        styles.navItem,
        { [styles.navItemActive]: isActive(item.route) },
      ]"
      @click="navigateTo(item.route)"
    >
      <img
        :src="`/images/${getIcon(item)}`"
        :alt="item.name"
        :class="styles.navIcon"
      />
      <span :class="styles.navLabel">{{ item.name }}</span>
    </button>
  </nav>
</template>
