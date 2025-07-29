package cheongsan.domain.debt.service;

import cheongsan.common.config.RootConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class DebtServiceImplTest {

    @Autowired
    private DebtServiceImpl debtService;

    @Test
    @DisplayName("여러 개의 부채가 있을 때, 각 월 상환액의 총합을 정확히 계산해야 한다")
    void calculateTotalMonthlyPayment_WithMultipleDebts() {
        // given
        Long userId = 1L;

        // when
        BigDecimal totalPayment = debtService.calculateTotalMonthlyPayment(userId);

        // then
        log.info("계산된 월 총 상환액: " + totalPayment);
    }

    @Test
    @DisplayName("부채가 하나도 없을 때, 월 총 상환액은 0을 반환해야 한다")
    void calculateTotalMonthlyPayment_WithNoDebts() {
        // given
        Long userId = 3L;

        // when
        BigDecimal totalPayment = debtService.calculateTotalMonthlyPayment(userId);

        // then
        assertEquals(BigDecimal.ZERO, totalPayment, "부채가 없으면 총 상환액은 0이어야 합니다.");
        log.info("계산된 월 총 상환액: " + totalPayment);
    }
}