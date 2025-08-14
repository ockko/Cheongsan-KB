import request from '@/api/index';

/**
 * 알림 관련 API
 */
export const notificationApi = {
  /**
   * 읽지 않은 알림 개수 조회
   * @returns {Promise<{unreadCount: number}>}
   */
  async getUnreadCount() {
    const response = await request.get('/cheongsan/notifications/unread');
    return response.data;
  },

  /**
   * 알림 목록 조회
   * @returns {Promise<Array>}
   */
  async getNotifications() {
    const response = await request.get('/cheongsan/notifications');
    return response.data;
  },

  /**
   * 모든 알림을 읽음으로 처리
   * @returns {Promise}
   */
  async markAllAsRead() {
    return await request.patch('/cheongsan/notifications/readAll');
  },

  /**
   * 특정 알림을 읽음으로 처리
   * @param {number} notificationId - 알림 ID
   * @returns {Promise}
   */
  async markAsRead(notificationId) {
    return await request.patch(
      `/cheongsan/notifications/${notificationId}/read`
    );
  },
};
