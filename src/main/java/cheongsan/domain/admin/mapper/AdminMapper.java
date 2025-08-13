package cheongsan.domain.admin.mapper;

import cheongsan.domain.admin.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<AdminUser> getAllUsers();

    int deleteUserById(Long userId);
}
