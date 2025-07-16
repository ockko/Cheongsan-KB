package cheongsan.domain.user.repository;

import cheongsan.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    User findById(Long id);
}
