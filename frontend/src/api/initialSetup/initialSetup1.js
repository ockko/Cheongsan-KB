import { request } from '@/api/index.js';

export const saveNickname = async (nickname) => {
  try {
    const response = await request.patch('/cheongsan/user/profile/name', {
      nickname: nickname,
    });
    return response;
  } catch (error) {
    console.error('saveNickname API 에러:', error);
    throw error;
  }
};
