import { createRouter, createWebHistory } from 'vue-router';

import Home from '@/pages/Home.vue';
import Policy from '@/pages/Policy.vue';
import Calendar from '@/pages/Calendar.vue';
import Simulation from '@/pages/Simulation.vue';
import Study from '@/pages/Study.vue';
import Onboarding1 from '@/pages/Onboarding/Onboarding1.vue';
import Onboarding2 from '@/pages/Onboarding/Onboarding2.vue';
import Onboarding3 from '@/pages/Onboarding/Onboarding3.vue';
import Onboarding4 from '@/pages/Onboarding/Onboarding4.vue';

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
    { path: '/onboarding/page1',
      name: 'Onboarding1',
      component: Onboarding1,
    },
    { path: '/onboarding/page2',
      name: 'Onboarding2',
      component: Onboarding2,
    },
    { path: '/onboarding/page3',
      name: 'Onboarding3',
      component: Onboarding3,
    },
    { path: '/onboarding/page4',
      name: 'Onboarding4',
      component: Onboarding4,
    }
  ],
});

export default router;
