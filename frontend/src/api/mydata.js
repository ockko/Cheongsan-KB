import request from '@/api/index';

/**
 * 계정 데이터 동기화 관련 API
 */
export const mydataApi = {
  /**
   * CODEF를 통한 계정 데이터 동기화
   * @returns {Promise}
   */
  async syncAccountData() {
    const response = await request.post(
      '/cheongsan/mydata/sync',
      {},
      {
        timeout: 60000, // 60초로 타임아웃 증가 (CODEF 동기화는 시간이 오래 걸릴 수 있음)
      }
    );
    return response.data;
  },

  /**
   * 동기화 상태 조회
   * @returns {Promise<{isSync: boolean, lastSyncTime?: string}>}
   */
  async getSyncStatus() {
    const response = await request.get('/cheongsan/mydata/sync/status');
    return response.data;
  },
};
