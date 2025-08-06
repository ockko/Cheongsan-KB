import { createRouter, createWebHistory } from 'vue-router';

import Home from '@/pages/Home.vue';
import Policy from '@/pages/Policy.vue';
import Calendar from '@/pages/Calendar.vue';
import Simulation from '@/pages/Simulation.vue';
import Study from '@/pages/Study.vue';
import Diagnosis from '@/pages/Diagnosis.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home', // 기본 경로를 홈으로 리다이렉트 했지만, 이 부분은 나중에 로그인 페이지가 생성되면 그 때 바꿀 예정입니다.
    },
    {
      path: '/home',
      name: 'Home',
      component: Home,
    },
    {
      path: '/policy',
      name: 'Policy',
      component: Policy,
    },
    {
      path: '/calendar',
      name: 'Calendar',
      component: Calendar,
    },
    {
      path: '/simulation',
      name: 'Simulation',
      component: Simulation,
    },
    {
      path: '/study',
      name: 'Study',
      component: Study,
    },
    {
      path: '/diagnosis',
      name: 'Diagnosis',
      component: Diagnosis,
    },
  ],
});

export default router;
