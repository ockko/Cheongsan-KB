package cheongsan.domain.notification.service;

import cheongsan.domain.notification.controller.NotificationWebSocketHandler;
import cheongsan.domain.notification.dto.NotificationDto;
import cheongsan.domain.notification.entity.NotificationEntity;
import cheongsan.domain.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationWebSocketHandler webSocketHandler;

    @Override
    public List<NotificationDto> getNotifications(Long userId) {
        log.debug("알림 목록 조회 시작: userId={}", userId);

        List<NotificationEntity> notifications = notificationMapper.findNotificationByUserId(userId);
        List<NotificationDto> result = notifications.stream()
                .map(NotificationDto::of)
                .collect(Collectors.toList());

        log.info("알림 목록 조회 완료: userId={}, count={}", userId, result.size());
        return result;
    }

    @Override
    @Transactional
    public void createAndSendNotification(Long userId, String contents) {
        log.debug("알림 생성 시작: userId={}, contents={}", userId, contents);

        try {
            // 1. DB에 알림 저장
            NotificationEntity notification = NotificationEntity.builder()
                    .userId(userId)
                    .contents(contents)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationMapper.insertNotification(notification);
            log.debug("알림 DB 저장 완료: id={}, userId={}", notification.getId(), userId);

            // 2. 실시간 알림 전송
            NotificationDto dto = NotificationDto.of(notification);
            webSocketHandler.sendNotificationToUser(userId, dto);

            log.info("알림 생성 및 전송 완료: id={}, userId={}, contents={}",
                    notification.getId(), userId, contents);

        } catch (Exception e) {
            log.error("알림 생성 실패: userId={}, message={}", userId, contents, e);
            throw new RuntimeException("알림 생성에 실패했습니다.", e);
        }
    }

    @Override
    public int getUnreadCount(Long userId) {
        log.debug("안읽은 알림 개수 조회: userId={}", userId);

        int count = notificationMapper.countUnreadNotificationByUserId(userId);

        log.info("안읽은 알림 개수 조회 완료: userId={}, count={}", userId, count);
        return count;
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        log.debug("알림 읽음 처리 시작: id={}", notificationId);

        try {
            notificationMapper.markNotificationAsRead(notificationId);
            log.info("알림 읽음 처리 완료: id={}", notificationId);

        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: id={}", notificationId, e);
            throw new RuntimeException("알림 읽음 처리에 실패했습니다.", e);
        }
    }
}
