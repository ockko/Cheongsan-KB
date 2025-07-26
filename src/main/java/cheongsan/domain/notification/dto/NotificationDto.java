package cheongsan.domain.notification.dto;

import cheongsan.domain.notification.entity.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String contents;        // type, title 제거하고 message만 사용
    private LocalDateTime createdAt;
    private Boolean isRead;

    public static NotificationDto of(NotificationEntity entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .contents(entity.getContents())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
