package cheongsan.domain.user.mapper;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.user.entity.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
@Log4j2
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("사용자의 일일 소비 한도와 마지막 수정 시각을 정확히 업데이트해야 한다")
    void updateDailyLimit() {
        // given
        Long userId = 1L;
        BigDecimal newLimit = new BigDecimal("30000");
        LocalDateTime newTimestamp = LocalDateTime.now();

        // when
        userMapper.updateDailyLimit(userId, newLimit, newTimestamp);

        // then
        User updatedUser = userMapper.findById(userId);

        assertNotNull(updatedUser, "업데이트 후 사용자를 조회할 수 있어야 합니다.");

        assertEquals(0, newLimit.compareTo(updatedUser.getDailyLimit()), "daily_limit이 정확히 업데이트되어야 합니다.");

        // LocalDateTime은 초 단위 이하에서 미세한 차이가 날 수 있으므로, 초 단위까지만 비교
        assertEquals(newTimestamp.withNano(0), updatedUser.getDailyLimitDate().withNano(0), "daily_limit_date가 정확히 업데이트되어야 합니다.");

        log.info("업데이트된 사용자 정보: " + updatedUser);
    }
}