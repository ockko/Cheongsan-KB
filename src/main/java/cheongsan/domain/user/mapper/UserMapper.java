package cheongsan.domain.user.mapper;

import cheongsan.domain.user.dto.SignUpRequestDTO;
import cheongsan.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    User findById(@Param("id") Long id);

    void saveDiagnosis(@Param("id") Long id,
                       @Param("programId") Long programId);

    void updateDailyLimit(@Param("id") Long id,
                          @Param("dailyLimit") BigDecimal dailyLimit,
                          @Param("dailyLimitDate") LocalDateTime dailyLimitDate);

    void save(@RequestBody User user);
    User findByUserId(String userId);
    User findByEmail(String email);

}
