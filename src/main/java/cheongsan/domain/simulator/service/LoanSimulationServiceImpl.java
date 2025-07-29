package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanSimulationServiceImpl implements LoanSimulationService {

    private final LoanRepaymentCalculator calculator;

    @Override
    public LoanAnalyzeResponseDTO analyze(LoanAnalyzeRequestDTO request) {
        return null;
    }

    @Override
    public TotalComparisonResultDTO compareTotalRepaymentWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan) {
        if (newLoan == null) {
            throw new IllegalArgumentException("새 대출 정보(newLoan)는 null일 수 없습니다.");
        }
        BigDecimal existingTotal = calculateTotalRepayment(existingLoans);

        existingLoans.add(newLoan);
        BigDecimal withNewLoanTotal = calculateTotalRepayment(existingLoans);

        BigDecimal increaseAmount = withNewLoanTotal.subtract(existingTotal);
        BigDecimal increaseRate = existingTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : increaseAmount.divide(existingTotal, 4, RoundingMode.HALF_UP);
        return new TotalComparisonResultDTO(
                existingTotal.setScale(0, RoundingMode.HALF_UP),
                withNewLoanTotal.setScale(0, RoundingMode.HALF_UP),
                increaseRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal calculateTotalRepayment(List<LoanDTO> loans) {
        BigDecimal totalRepayment = BigDecimal.ZERO;

        for (LoanDTO loan : loans) {
            int yearsDiff = loan.getEndDate().getYear() - loan.getStartDate().getYear();
            int monthsDiff = loan.getEndDate().getMonthValue() - loan.getStartDate().getMonthValue();
            PaymentResultDTO result;
            int months = yearsDiff * 12 + monthsDiff + 1;
            BigDecimal annualRate = BigDecimal.valueOf(loan.getInterestRate());
            BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

            switch (loan.getRepaymentType()) {
                case EQUAL_PAYMENT:
                    result = calculator.calculateEqualPayment(
                            monthlyRate,
                            months,
                            loan.getPrincipal()
                    );
                    break;
                case EQUAL_PRINCIPAL:
                    result = calculator.calculateEqualPrincipal(
                            monthlyRate,
                            months,
                            loan.getPrincipal()
                    );
                    break;
                case LUMP_SUM:
                    result = calculator.calculateLumpSumRepayment(
                            loan.getPrincipal(),
                            annualRate,
                            months,
                            loan.getStartDate()
                    );
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + loan.getRepaymentType());
            }
            totalRepayment = totalRepayment.add(result.getTotalPayment());
        }
        return totalRepayment;
    }
}
