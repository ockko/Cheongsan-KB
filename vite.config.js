import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';
import { VitePWA } from 'vite-plugin-pwa'; // 추가

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // vueDevTools(),
    // PWA 플러그인 추가
    VitePWA({
      registerType: 'autoUpdate',
      manifest: {
        name: 'Cheongsan',
        short_name: 'Cheongsan',
        description: '티끌모아 청산 - 개인 금융 관리 앱',
        theme_color: '#003e65',
        background_color: '#ffffff',
        display: 'standalone',
        scope: '/',
        start_url: '/',
        // 배포 시에는 절대 URL 사용 고려
        // scope: 'https://yourdomain.com/',
        // start_url: 'https://yourdomain.com/',
        icons: [
          {
            src: '/images/logo-blue.png', // 앞에 / 추가
            sizes: '192x192',
            type: 'image/png',
          },
          {
            src: '/images/logo-blue.png', // 앞에 / 추가
            sizes: '512x512',
            type: 'image/png',
          },
        ],
      },
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  css: {
    modules: {
      localsConvention: 'camelCase',
    },
  },
});
