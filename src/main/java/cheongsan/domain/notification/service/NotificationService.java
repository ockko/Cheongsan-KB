package cheongsan.domain.notification.service;

import cheongsan.domain.notification.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    // 사용자의 알림 목록 조회
    List<NotificationDto> getNotifications(Long userId);

    // 사용자의 안읽은 알림 개수 조회
    int getUnreadCount(Long userId);

    // 알림 읽음 처리
    void markAsRead(Long userId);
}
