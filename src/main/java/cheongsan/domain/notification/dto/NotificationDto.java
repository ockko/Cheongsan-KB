package cheongsan.domain.notification.dto;

import cheongsan.domain.notification.entity.NotificationEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String type;
    private Boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static NotificationDto of(NotificationEntity entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .contents(entity.getContents())
                .type(entity.getType())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
