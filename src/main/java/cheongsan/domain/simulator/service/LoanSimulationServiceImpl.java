package cheongsan.domain.simulator.service;

import cheongsan.common.util.DsrCalculator;
import cheongsan.common.util.LoanCalculator;
import cheongsan.domain.debt.dto.DebtInfoResponseDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.simulator.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanSimulationServiceImpl implements LoanSimulationService {

    private final LoanRepaymentCalculator calculator;
    private final DebtService debtService;
    private final LoanRepaymentCalculatorFacade loanFacade;
    private final LoanCalculator loanCalculator;

    private static final BigDecimal DSR_LIMIT = new BigDecimal("0.4");

    @Override
    public LoanAnalyzeResponseDTO analyze(LoanAnalyzeRequestDTO request) {
        Long userId = 1L;
        if (request == null) {
            throw new IllegalArgumentException("분석 요청(request)은 null일 수 없습니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID(userId)는 필수입니다.");
        }
        if (request.getAnnualIncome() == null || request.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("총 자산(assetTotalAmount)은 0보다 커야 합니다.");
        }

        // 1. 새 대출 DTO 생성
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(request.getLoanPeriod());

        LoanDTO newLoan = LoanDTO.builder()
                .principal(request.getLoanAmount())
                .interestRate(request.getInterestRate())
                .startDate(startDate)
                .endDate(endDate)
                .repaymentType(request.getRepaymentType())
                .paymentDate(startDate)
                .build();

        // 2. 기존 대출 리스트 조회 및 변환 (DebtInfoResponseDTO -> LoanDTO)
        BigDecimal monthlyRepayment = loanCalculator.calculateMonthlyPayment(
                LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST,
                request.getLoanAmount(),
                request.getLoanAmount(),
                request.getInterestRate(),
                LocalDate.now(),
                LocalDate.now().plusMonths(request.getLoanPeriod())
        );


        // 3. DSR 계산
        BigDecimal dsr = DsrCalculator.calculateDsr(monthlyRepayment, request.getAnnualIncome(), loanCalculator);

        // 4. DSR 한도 초과 시 예외
        if (dsr.compareTo(DSR_LIMIT) > 0) {
            throw new IllegalArgumentException("DSR" + dsr.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                    + "%가  허용 한도 40%를 초과합니다.");
        }

        // 5. 기존 대출 리스트 조회 및 반환
        List<DebtInfoResponseDTO> existings = debtService.getUserDebtList(userId);
        List<LoanDTO> existingLoans = existings.stream()
                .map(debt -> LoanDTO.builder()
                        .principal(debt.getCurrentBalance())
                        .interestRate(debt.getInterestRate())
                        .startDate(debt.getLoanStartDate())
                        .endDate(debt.getLoanEndDate())
                        .repaymentType(debt.getRepaymentType())
                        .paymentDate(debt.getLoanStartDate())
                        .build())
                .toList();

        // 6. 기존 메서드 호출해서 결과 얻기
        TotalComparisonResultDTO totalComparison = compareTotalRepaymentWithNewLoan(existingLoans, newLoan);
        InterestComparisonResultDTO interestComparison = compareInterestWithNewLoan(existingLoans, newLoan);
        DebtRatioComparisonResultDTO debtRatioComparison = compareDebtRatioWithNewLoan(existingLoans, newLoan, request.getAnnualIncome());

        // 7. 응답 DTO에 담아 반환
        return new LoanAnalyzeResponseDTO(
                totalComparison,
                interestComparison,
                debtRatioComparison
        );
    }

    @Override
    public TotalComparisonResultDTO compareTotalRepaymentWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan) {
        if (newLoan == null) {
            throw new IllegalArgumentException("새 대출 정보(newLoan)는 null일 수 없습니다.");
        }
        BigDecimal existingTotal = calculateTotalRepayment(existingLoans);

        List<LoanDTO> loansWithNew = new ArrayList<>(existingLoans);
        loansWithNew.add(newLoan);
        BigDecimal withNewLoanTotal = calculateTotalRepayment(loansWithNew);

        BigDecimal increaseAmount = withNewLoanTotal.subtract(existingTotal);
        BigDecimal increaseRate = existingTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : increaseAmount.divide(existingTotal, 4, RoundingMode.HALF_UP);
        return new TotalComparisonResultDTO(
                existingTotal.setScale(0, RoundingMode.HALF_UP),
                withNewLoanTotal.setScale(0, RoundingMode.HALF_UP),
                increaseRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Override
    public InterestComparisonResultDTO compareInterestWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan) {
        if (newLoan == null) {
            throw new IllegalArgumentException("새 대출 정보(newLoan)는 null일 수 없습니다.");
        }

        BigDecimal existingTotalRepayment = calculateTotalRepayment(existingLoans);
        BigDecimal existingPrincipalSum = existingLoans.stream()
                .map(LoanDTO::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal existingInterest = existingTotalRepayment.subtract(existingPrincipalSum);

        List<LoanDTO> withNewLoanList = new ArrayList<>(existingLoans);
        withNewLoanList.add(newLoan);

        BigDecimal withNewLoanTotalRepayment = calculateTotalRepayment(withNewLoanList);
        BigDecimal withNewLoanPrincipalSum = withNewLoanList.stream() // 🔧 수정된 부분
                .map(LoanDTO::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withNewLoanInterest = withNewLoanTotalRepayment.subtract(withNewLoanPrincipalSum);

        BigDecimal increaseAmount = withNewLoanInterest.subtract(existingInterest);
        BigDecimal increaseRate = existingInterest.compareTo(BigDecimal.ZERO) == 0 ?
                BigDecimal.ZERO : increaseAmount.divide(existingInterest, 4, RoundingMode.HALF_UP);

        return new InterestComparisonResultDTO(
                existingInterest.setScale(0, RoundingMode.HALF_UP),
                withNewLoanInterest.setScale(0, RoundingMode.HALF_UP),
                increaseRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Override
    public DebtRatioComparisonResultDTO compareDebtRatioWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan, BigDecimal assetTotalAmount) {
        if (newLoan == null) {
            throw new IllegalArgumentException("새 대출 정보(newLoan)는 null일 수 없습니다.");
        }
        if (assetTotalAmount == null || assetTotalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("총 자산(assetTotalAmount)은 0보다 커야 합니다.");
        }

        // 기존 부채 합계 (원금 합계)
        BigDecimal existingDebtTotal = existingLoans.stream()
                .map(LoanDTO::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 신규 대출 포함 부채 합계
        BigDecimal newDebtTotal = existingDebtTotal.add(newLoan.getPrincipal());

        // 부채비율 계산 = 부채총액 / 자산총액 * 100
        BigDecimal existingDebtRatio = existingDebtTotal.multiply(BigDecimal.valueOf(100))
                .divide(assetTotalAmount, 2, RoundingMode.HALF_UP);
        BigDecimal newDebtRatio = newDebtTotal.multiply(BigDecimal.valueOf(100))
                .divide(assetTotalAmount, 2, RoundingMode.HALF_UP);

        // 부채비율 증가량 (new - existing)
        BigDecimal increaseAmount = newDebtRatio.subtract(existingDebtRatio);

        // 부채비율 증가율 (증가량 / 기존 비율 * 100), 0일 때 처리
        BigDecimal increaseRate = existingDebtRatio.compareTo(BigDecimal.ZERO) == 0 ?
                BigDecimal.ZERO :
                increaseAmount.multiply(BigDecimal.valueOf(100))
                        .divide(existingDebtRatio, 2, RoundingMode.HALF_UP);


        return new DebtRatioComparisonResultDTO(
                existingDebtRatio.setScale(2, RoundingMode.HALF_UP),
                newDebtRatio.setScale(2, RoundingMode.HALF_UP),
                increaseAmount.setScale(2, RoundingMode.HALF_UP),
                increaseRate.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal calculateTotalRepayment(List<LoanDTO> loans) {
        BigDecimal totalRepayment = BigDecimal.ZERO;
        for (LoanDTO loan : loans) {
            PaymentResultDTO result = loanFacade.calculateByType(Scenario.NO_PREPAYMENT, loan, BigDecimal.ZERO);
            totalRepayment = totalRepayment.add(result.getTotalPayment());
        }
        return totalRepayment;
    }
}
