package cheongsan.domain.notification.mapper;

import cheongsan.domain.notification.entity.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(NotificationEntity notification);

    List<NotificationEntity> findNotificationByUserId(@Param("userId") Long userId);

    int countUnreadNotificationByUserId(@Param("userId") Long userId);

    void markNotificationAsRead(@Param("id") Long id);
}
