import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/pages/HomePage.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    // ...authRoutes, 이런 느낌으로 각 도메인별로 라우터를 커스터 마이징 해주면 됩니다.
    //예: src/router/auth.js :
    // export default [
    //   {
    //     path: '/auth/login',
    //     name: 'login',
    //     component: () => import('../pages/auth/LoginPage.vue'),
    //   },
    //   {
    //     path: '/auth/join',
    //     name: 'join',
    //     component: () => import('../pages/auth/JoinPage.vue'),
    //   },
    //   {
    //     path: '/auth/profile',
    //     name: 'profile',
    //     component: () => import('../pages/auth/ProfilePage.vue'),
    //   },
    //   {
    //     path: '/auth/changepassword',
    //     name: 'changepassword',
    //     component: () => import('../pages/auth/ChangePasswordPage.vue'),
    //   },
    // ];
  ],
});

export default router;
