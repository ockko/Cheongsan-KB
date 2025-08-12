import { request } from '../index';

export async function fetchUserLoans() {
  try {
    return await request.get('/cheongsan/initialSetup/loans');
  } catch (error) {
    console.error('get API 호출 실패:', error);
    throw error;
  }
}
