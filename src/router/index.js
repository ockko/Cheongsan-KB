import { createRouter, createWebHistory } from 'vue-router';

import Home from '@/pages/Home.vue';
import Policy from '@/pages/Policy.vue';
import Calendar from '@/pages/Calendar.vue';
import Simulation from '@/pages/Simulation.vue';
import Study from '@/pages/Study.vue';
import Diagnosis from '@/pages/Diagnosis.vue';
import StudyDetail from '@/pages/StudyDetail.vue';
import Onboarding from '@/pages/Onboarding.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/onboarding',
    },
    {
      path: '/onboarding', // 온보딩 라우트 추가
      name: 'Onboarding',
      component: Onboarding,
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
    {
      // path: /study/:id
      path: '/study/detail',
      name: 'StudyDetail',
      component: StudyDetail,
    },
  ],
});

export default router;
