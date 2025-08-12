import { request } from '@/api/index.js';

export async function saveNickname(nickname) {
  return await request.post('/cheongsan/user/nickname', { nickname });
}
