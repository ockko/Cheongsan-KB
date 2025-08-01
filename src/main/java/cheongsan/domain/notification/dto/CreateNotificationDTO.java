package cheongsan.domain.notification.dto;

import cheongsan.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDTO {
    private Long userId;
    private String contents;
    private String type;

    public Notification toEntity() {
        return Notification.builder()
                .userId(userId)
                .contents(contents)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
