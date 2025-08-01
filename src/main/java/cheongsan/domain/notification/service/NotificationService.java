package cheongsan.domain.notification.service;

import cheongsan.domain.deposit.dto.WeeklyReportDTO;
import cheongsan.domain.notification.dto.CreateNotificationDTO;
import cheongsan.domain.notification.dto.NotificationDTO;
import cheongsan.domain.notification.entity.Notification;
import cheongsan.domain.user.entity.User;

import java.util.List;

public interface NotificationService {
    // 사용자의 알림 목록 조회
    List<NotificationDTO> getNotifications(Long userId);

    // 사용자의 안읽은 알림 개수 조회
    int getUnreadCount(Long userId);

    // 알림 읽음 일괄 처리
    void markAllAsRead(Long userId);

    // 알림 데이터 저장
    void createNotification(CreateNotificationDTO notificationDTO);

    // 소비 한도 초과 이메일
    void sendDailyLimitExceededEmail(User user, int dailyLimit, int totalSpent);

    void sendWeeklyReportEmail(User user, WeeklyReportDTO report);

    // 알림 소켓 전송
    Notification sendAlarm(Notification notification);
}
