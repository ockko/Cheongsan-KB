package cheongsan.domain.debt.mapper;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.debt.entity.DebtAccount;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class DebtMapperTest {

    @Autowired
    private DebtMapper mapper;

    @Test
    @DisplayName("사용자의 부채 정보 조회")
    void findByUserId() {
        // given
        Long userId = 1L;

        // when
        List<DebtAccount> debts = mapper.findByUserId(userId);

        // then
        for (DebtAccount debt : debts) {
            log.info("========" + debt);
        }
    }
}