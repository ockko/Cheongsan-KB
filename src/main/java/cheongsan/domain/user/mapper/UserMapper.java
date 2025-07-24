package cheongsan.domain.user.mapper;

import cheongsan.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findById(@Param("userId") Long id);

    void saveDiagnosis(@Param("userId") Long userId,
                       @Param("programId") Long programId);
}
