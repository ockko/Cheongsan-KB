import axios from 'axios';
import { request } from '.';

export const updateNickname = async (nickname) => {
  return await request.patch('/cheongsan/user/profile/name', { nickname });
};

export const updateEmail = async (email) => {
  return await request.patch('/cheongsan/user/profile/email', { email });
};

export const changePassword = async (payload) => {
  return await request.post('/cheongsan/user/profile/changePassword', payload);
};

export const verifyPassword = async (oldPassword) => {
  return await request.post('/cheongsan/user/profile/verifyPassword', {
    oldPassword,
    newPassword: '',
  });
};
export const getMyProfile = async () => {
  return await request.get('/cheongsan/user/profile');
};
// 회원 탈퇴 API
export const deleteAccount = async (payload) => {
  return await request.delete('/cheongsan/user/profile', { data: payload });
};
export const logout = async () => {
  return await request.post('/cheongsan/user/logout');
};
