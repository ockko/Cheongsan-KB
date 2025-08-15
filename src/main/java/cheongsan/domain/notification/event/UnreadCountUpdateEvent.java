package cheongsan.domain.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 읽지 않은 알림 개수 업데이트 이벤트
 */
@Getter
public class UnreadCountUpdateEvent extends ApplicationEvent {
    private final Long userId;
    private final int unreadCount;

    public UnreadCountUpdateEvent(Object source, Long userId, int unreadCount) {
        super(source);
        this.userId = userId;
        this.unreadCount = unreadCount;
    }
}
