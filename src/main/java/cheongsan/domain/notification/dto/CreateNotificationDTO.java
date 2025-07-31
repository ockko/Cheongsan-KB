package cheongsan.domain.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateNotificationDTO {
    private Long userId;
    private String contents;
    private String type;
}
