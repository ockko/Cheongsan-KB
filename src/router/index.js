import { createRouter, createWebHistory } from 'vue-router';

import Home from '@/pages/Home.vue';
import Policy from '@/pages/Policy.vue';
import Calendar from '@/pages/Calendar.vue';
import Simulation from '@/pages/Simulation.vue';
import Study from '@/pages/Study.vue';
import Diagnosis from '@/pages/Diagnosis.vue';
import StudyDetail from '@/pages/StudyDetail.vue';
import Onboarding from '@/pages/Onboarding.vue';
import Login from '@/pages/Login.vue';

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
      path: '/login', // 로그인 라우트 추가
      name: 'Login',
      component: Login,
    },
    {
      path: '/home',
      name: 'Home',
      component: Home,
      meta: { requiresAuth: true }, // 인증 필요
    },
    {
      path: '/policy',
      name: 'Policy',
      component: Policy,
      meta: { requiresAuth: true }, // 인증 필요
    },
    {
      path: '/calendar',
      name: 'Calendar',
      component: Calendar,
      meta: { requiresAuth: true }, // 인증 필요
    },
    {
      path: '/simulation',
      name: 'Simulation',
      component: Simulation,
      meta: { requiresAuth: true }, // 인증 필요
    },
    {
      path: '/study',
      name: 'Study',
      component: Study,
      meta: { requiresAuth: true }, // 인증 필요
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
      meta: { requiresAuth: true }, // 인증 필요
    },
  ],
});

// 네비게이션 가드 - 인증 확인
router.beforeEach((to, from, next) => {
  // 인증이 필요한 페이지인지 확인
  if (to.meta.requiresAuth) {
    // localStorage에서 토큰 확인
    const auth = localStorage.getItem('auth');

    if (!auth) {
      // 토큰이 없으면 로그인 페이지로 리다이렉트
      next('/login?error=login_required');
      return;
    }

    try {
      const authData = JSON.parse(auth);
      if (!authData.accessToken) {
        // accessToken이 없으면 로그인 페이지로 리다이렉트
        next('/login?error=login_required');
        return;
      }
    } catch (error) {
      // JSON 파싱 에러시 로그인 페이지로 리다이렉트
      console.error('Auth data parsing error:', error);
      localStorage.removeItem('auth');
      next('/login?error=login_required');
      return;
    }
  }

  next();
});

export default router;
