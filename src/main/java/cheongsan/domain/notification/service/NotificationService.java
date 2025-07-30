package cheongsan.domain.notification.service;

import cheongsan.domain.notification.dto.NotificationDTO;
import cheongsan.domain.user.entity.User;

import java.util.List;

public interface NotificationService {
    // 사용자의 알림 목록 조회
    List<NotificationDTO> getNotifications(Long userId);

    // 사용자의 안읽은 알림 개수 조회
    int getUnreadCount(Long userId);

    // 알림 읽음 처리
    void markAllAsRead(Long userId);

    public void createNotification(Long userId, String contents, String type);

    void markAsRead(Long userId);

    void sendDailyLimitExceededEmail(User user, int dailyLimit, int totalSpent);
}
