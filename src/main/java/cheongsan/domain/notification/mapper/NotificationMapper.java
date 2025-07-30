package cheongsan.domain.notification.mapper;

import cheongsan.domain.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    List<Notification> findNotificationByUserId(@Param("userId") Long userId);

    int countUnreadNotificationByUserId(@Param("userId") Long userId);

    int countTodayNotificationsByType(@Param("userId") Long userId, @Param("type") String type);

    void save(Notification notification);

    void markNotificationAsRead(@Param("userId") Long id);
}
