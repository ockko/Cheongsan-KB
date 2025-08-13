import { defineStore } from 'pinia';
import {
  updateNickname,
  updateEmail,
  changePassword,
  verifyPassword,
  getMyProfile,
  deleteAccount,
  logout,
} from '@/api/mypage.js';

export const useMyPageStore = defineStore('user', {
  state: () => ({
    id: '',
    userId: '',
    nickname: '',
    email: '',
  }),
  actions: {
    async updateNickname(newName) {
      try {
        await updateNickname(newName);
        this.nickname = newName;
      } catch (e) {
        throw e;
      }
    },
    async updateEmail(newEmail) {
      try {
        await updateEmail(newEmail);
        this.email = newEmail;
      } catch (e) {
        throw e;
      }
    },
    async verifyPassword(oldPassword) {
      try {
        const response = await verifyPassword(oldPassword);
        return response; // true or false 반환 예상
      } catch (e) {
        throw e;
      }
    },
    async changePassword({ oldPassword, newPassword }) {
      try {
        await changePassword({ oldPassword, newPassword });
      } catch (e) {
        throw e;
      }
    },
    async fetchMyProfile() {
      try {
        const data = await getMyProfile();

        // API 응답 구조에 맞춰 데이터 할당
        this.id = data.id;
        this.userId = data.userId;
        this.nickname = data.nickname;
        this.email = data.email;
      } catch (error) {
        console.error('내 정보 불러오기 실패:', error);
      }
    },
    async deleteAccount(payload) {
      try {
        await deleteAccount(payload);
        this.logout();
        return true;
      } catch (e) {
        console.error('회원탈퇴 실패', e);
        return false;
      }
    },
    async logout() {
      try {
        await logout(); // 서버에 로그아웃 요청
      } catch (e) {
        console.warn('로그아웃 API 호출 실패', e);
      }
      localStorage.removeItem('token'); // JWT 삭제
      this.$reset(); // 상태 초기화
    },
  },
});
