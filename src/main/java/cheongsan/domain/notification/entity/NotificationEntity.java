package cheongsan.domain.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {
    private Long id;
    private Long userId;
    private String contents;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
