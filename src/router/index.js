import { createRouter, createWebHistory } from 'vue-router';

import Home from '@/pages/Home.vue';
import Policy from '@/pages/Policy.vue';
import Calendar from '@/pages/Calendar.vue';
import Simulation from '@/pages/Simulation.vue';
import Study from '@/pages/Study.vue';
import Onboarding1 from '@/pages/InitialSetup/InitialSetup1.vue';
import Onboarding2 from '@/pages/InitialSetup/InitialSetup2.vue';
import Onboarding3 from '@/pages/InitialSetup/InitialSetup3.vue';
import Onboarding4 from '@/pages/InitialSetup/InitialSetup4.vue';
import Diagnosis from '@/pages/Diagnosis.vue';
import StudyDetail from '@/pages/StudyDetail.vue';
import InitialSetup1 from '@/pages/InitialSetup/InitialSetup1.vue';
import InitialSetup2 from '@/pages/InitialSetup/InitialSetup2.vue';
import InitialSetup3 from '@/pages/InitialSetup/InitialSetup3.vue';
import InitialSetup4 from '@/pages/InitialSetup/InitialSetup4.vue';

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
    { path: '/initialSetup/page1',
      name: 'InitialSetup1',
      component: InitialSetup1,
    },
    { path: '/initialSetup/page2',
      name: 'InitialSetup2',
      component: InitialSetup2,
    },
    { path: '/initialSetup/page3',
      name: 'InitialSetup3',
      component: InitialSetup3,
    },
    { path: '/initialSetup/page4',
      name: 'InitialSetup4',
      component: InitialSetup4,
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
