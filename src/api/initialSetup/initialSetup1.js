import { request } from '@/api/index.js';

export const saveNickname = async (nickname) => {
  return await request.post('/cheongsan/user/nickname', { nickname });
};
