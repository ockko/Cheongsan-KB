package cheongsan.domain.simulator.service.strategy;

import cheongsan.domain.simulator.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class OldestFirstStrategyTest {

    @Autowired
    private OldestFirstStrategy strategy;

    private RepaymentRequestDto request;

    @Before
    public void setUp() {
        List<LoanDto> loans = Arrays.asList(
                // 금융2 (고금리)
                LoanDto.builder().id(1L).loanName("loan1").principal(BigDecimal.valueOf(15_000_000))
                        .interestRate(12.8).monthlyPayment(BigDecimal.valueOf(450_000))
                        .startDate(LocalDate.of(2023, 1, 1)).endDate(LocalDate.of(2026, 1, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("금융2").build(),

                LoanDto.builder().id(2L).loanName("loan2").principal(BigDecimal.valueOf(6_000_000))
                        .interestRate(9.5).monthlyPayment(BigDecimal.valueOf(200_000))
                        .startDate(LocalDate.of(2023, 6, 1)).endDate(LocalDate.of(2025, 6, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("금융2").build(),

                LoanDto.builder().id(3L).loanName("loan3").principal(BigDecimal.valueOf(4_000_000))
                        .interestRate(11.2).monthlyPayment(BigDecimal.valueOf(180_000))
                        .startDate(LocalDate.of(2022, 11, 1)).endDate(LocalDate.of(2024, 11, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("금융2").build(),

                // 기타 (중간금리)
                LoanDto.builder().id(4L).loanName("loan4").principal(BigDecimal.valueOf(20_000_000))
                        .interestRate(6.5).monthlyPayment(BigDecimal.valueOf(500_000))
                        .startDate(LocalDate.of(2022, 9, 1)).endDate(LocalDate.of(2027, 9, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("기타").build(),

                LoanDto.builder().id(5L).loanName("loan5").principal(BigDecimal.valueOf(8_000_000))
                        .interestRate(7.2).monthlyPayment(BigDecimal.valueOf(220_000))
                        .startDate(LocalDate.of(2023, 4, 1)).endDate(LocalDate.of(2026, 4, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("기타").build(),

                LoanDto.builder().id(6L).loanName("loan6").principal(BigDecimal.valueOf(5_500_000))
                        .interestRate(5.5).monthlyPayment(BigDecimal.valueOf(160_000))
                        .startDate(LocalDate.of(2024, 1, 1)).endDate(LocalDate.of(2027, 1, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("기타").build(),

                // 금융1 (저금리)
                LoanDto.builder().id(7L).loanName("loan7").principal(BigDecimal.valueOf(10_000_000))
                        .interestRate(4.5).monthlyPayment(BigDecimal.valueOf(300_000))
                        .startDate(LocalDate.of(2022, 3, 1)).endDate(LocalDate.of(2026, 3, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("금융1").build(),

                LoanDto.builder().id(8L).loanName("loan8").principal(BigDecimal.valueOf(7_000_000))
                        .interestRate(3.8).monthlyPayment(BigDecimal.valueOf(250_000))
                        .startDate(LocalDate.of(2023, 5, 1)).endDate(LocalDate.of(2027, 5, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("금융1").build(),

                LoanDto.builder().id(9L).loanName("loan9").principal(BigDecimal.valueOf(12_000_000))
                        .interestRate(4.2).monthlyPayment(BigDecimal.valueOf(350_000))
                        .startDate(LocalDate.of(2021, 8, 1)).endDate(LocalDate.of(2026, 8, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("금융1").build(),

                LoanDto.builder().id(10L).loanName("loan10").principal(BigDecimal.valueOf(3_000_000))
                        .interestRate(3.5).monthlyPayment(BigDecimal.valueOf(130_000))
                        .startDate(LocalDate.of(2024, 2, 1)).endDate(LocalDate.of(2026, 2, 1))
                        .repaymentMethod(RepaymentMethod.EQUAL_INSTALLMENT).institutionType("금융1").build()
        );


        request = RepaymentRequestDto.builder()
                .loans(loans)
                .monthlyPayment(BigDecimal.valueOf(200_000))             // 기본 상환액
                .extraPaymentAmount(BigDecimal.valueOf(100_000))         // 추가 상환액
                .build();

        request.setMonthlyAvailableAmount(BigDecimal.valueOf(300_000)); // 총 상환 가능 금액
    }


    @Test
    public void testSimulate() {
        RepaymentResultDto result = strategy.simulate(request);

        assertNotNull(result);
        assertEquals(StrategyType.OLDEST_FIRST, result.getStrategyType());
        assertTrue(result.getTotalMonths() > 0);
        assertNotNull(result.getDebtFreeDate());

        List<String> expectedSortedNames = request.getLoans().stream()
                .sorted(Comparator.comparing(LoanDto::getStartDate))
                .map(LoanDto::getLoanName)
                .collect(Collectors.toList());

        assertEquals(expectedSortedNames, result.getSortedLoanNames());

        System.out.println("📌 전략 타입: " + result.getStrategyType());
        System.out.println("📆 빚 졸업일: " + result.getDebtFreeDate());
        System.out.println("⏳ 총 상환 개월 수: " + result.getTotalMonths());
        System.out.println("💸 절약 이자: " + result.getInterestSaved());
        System.out.println("🔢 정렬된 대출: " + result.getSortedLoanNames());
    }
}
