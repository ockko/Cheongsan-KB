package cheongsan.domain.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 알림 이벤트 - 알림 생성 완료 후 웹소켓 전송용
 */
@Getter
public class NotificationEvent extends ApplicationEvent {
    private final Long userId;
    private final String contents;
    private final String type;
    private final Long notificationId;

    public NotificationEvent(Object source, Long userId, String contents, String type, Long notificationId) {
        super(source);
        this.userId = userId;
        this.contents = contents;
        this.type = type;
        this.notificationId = notificationId;
    }
}
