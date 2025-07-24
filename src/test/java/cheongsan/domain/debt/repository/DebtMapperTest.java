package cheongsan.domain.debt.repository;

import cheongsan.common.config.RootConfig;
import cheongsan.domain.debt.dto.DebtDTO;
import cheongsan.domain.debt.mapper.DebtMapper;
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
        List<DebtDTO> debts = mapper.findByUserId(userId);

        // then
        for (DebtDTO debt : debts) {
            log.info("========" + debt);
        }
    }
}