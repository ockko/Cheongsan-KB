import { request } from '@/api/index.js';

export const saveNickname = async (nickname) => {
  try {
    // 백엔드 컨트롤러가 PATCH 요청을 기대하므로 PATCH 사용
    // UpdateNicknameRequestDTO 형식에 맞춰 데이터 전송
    const response = await request.patch('/cheongsan/user/profile/name', {
      nickname: nickname, // UpdateNicknameRequestDTO의 nickname 필드
    });
    return response;
  } catch (error) {
    console.error('saveNickname API 에러:', error);
    throw error;
  }
};
