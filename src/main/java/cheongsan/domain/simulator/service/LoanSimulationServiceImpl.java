package cheongsan.domain.simulator.service;

import cheongsan.common.util.LoanCalculator;
import cheongsan.common.util.RepaymentTypeMapper;
import cheongsan.domain.debt.dto.DebtInfoResponseDTO;
import cheongsan.domain.debt.service.DebtService;
import cheongsan.domain.policy.dto.MonthlyInterestComparisonDTO;
import cheongsan.domain.simulator.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanSimulationServiceImpl implements LoanSimulationService {

    private final LoanRepaymentCalculator calculator;
    private final DebtService debtService;
    private final LoanRepaymentCalculatorFacade loanFacade;
    private final LoanCalculator loanCalculator;

    private static final BigDecimal DSR_LIMIT = new BigDecimal("0.4");

    @Override
    public LoanAnalyzeResponseDTO analyze(LoanAnalyzeRequestDTO request, Long userId) {
        if (request == null) throw new IllegalArgumentException("분석 요청(request)은 null일 수 없습니다.");
        if (request.getAnnualIncome() == null || request.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("연소득은 0보다 커야 합니다.");
        if (userId == null) throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");

        // 1) 기간: '년' → '개월'
        int months = Math.toIntExact(request.getLoanPeriod() * 12L);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(months);

        // 2) 신규 대출 DTO
        LoanDTO newLoan = LoanDTO.builder()
                .principal(request.getLoanAmount())
                .interestRate(request.getInterestRate())
                .startDate(startDate)
                .endDate(endDate)
                .repaymentType(request.getRepaymentType())
                .paymentDate(startDate)
                .build();

        // 디버그: 신규 대출만의 총상환액 확인
        try {
            PaymentResultDTO onlyNew = loanFacade.calculateByType(Scenario.NO_PREPAYMENT, newLoan, BigDecimal.ZERO);
            log.info("[CHK] newLoanOnly total={}, principal={}, annualRate(%)={}, months={}, type={}, start={}, end={}",
                    onlyNew.getTotalPayment(),
                    newLoan.getPrincipal(),
                    newLoan.getInterestRate(),
                    months,
                    newLoan.getRepaymentType(),
                    newLoan.getStartDate(),
                    newLoan.getEndDate());
        } catch (Exception e) {
            log.warn("[CHK] newLoanOnly calc failed: {}", e.getMessage(), e);
        }

        // 3) 기존 대출 조회 → LoanDTO 변환
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

        // 4) 신규 대출 월 상환액 계산(추천/DSR 용)
        LoanCalculator.RepaymentMethod repaymentMethod = RepaymentTypeMapper.toMethod(request.getRepaymentType());
        BigDecimal monthlyRepayment = loanCalculator.calculateMonthlyPayment(
                repaymentMethod,
                request.getLoanAmount(),
                request.getLoanAmount(),
                request.getInterestRate(),
                startDate,
                endDate
        );

        // 5) DSR 계산 및 한도 체크 (전체 DSR: 기존+신규)
        BigDecimal dsr = DsrCalculator.calculateDsr(existingLoans, monthlyRepayment, request.getAnnualIncome(), loanCalculator);
        if (dsr.compareTo(DSR_LIMIT) > 0) {
            throw new IllegalArgumentException("DSR이 " + dsr.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                    + "%로 허용 한도 40%를 초과합니다.");
        }

        // 6) 비교 결과
        TotalComparisonResultDTO totalComparison = compareTotalRepaymentWithNewLoan(existingLoans, newLoan);
        InterestComparisonResultDTO interestComparison = compareInterestWithNewLoan(existingLoans, newLoan);

        // 7) 통합 결과 (dsr을 응답에 포함)
        return new LoanAnalyzeResponseDTO(
                totalComparison,
                interestComparison,
                dsr
        );
    }

    @Override
    public BigDecimal computeDsr(Long userId, LoanRecommendationRequestDTO request) {
        if (userId == null) throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");
        if (request == null) throw new IllegalArgumentException("요청은 null일 수 없습니다.");
        if (request.getAnnualIncome() == null || request.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("연소득은 0보다 커야 합니다.");

        // 기간 환산
        long months = request.getTerm() * 12L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(months);

        // 상환 방식 매핑 (문자열 → enum 매핑 적용되어 있다면 그대로 사용)
        LoanCalculator.RepaymentMethod method = LoanCalculator.RepaymentMethod.EQUAL_PRINCIPAL_INTEREST;
        try {
            method = RepaymentTypeMapper.toMethod(RepaymentType.valueOf(request.getRepaymentType()));
        } catch (Exception ignore) {
            // 입력 문자열이 enum과 다를 수 있으니 기본값 방어
        }

        // 신규 대출 월 상환액
        BigDecimal newMonthly = loanCalculator.calculateMonthlyPayment(
                method,
                request.getPrincipal(),
                request.getPrincipal(),
                request.getInterestRate(), // 연이율(%)
                start,
                end
        );

        // 기존 대출 조회
        List<DebtInfoResponseDTO> existings = debtService.getUserDebtList(userId);
        List<LoanDTO> existingLoans = existings.stream()
                .map(d -> LoanDTO.builder()
                        .principal(d.getCurrentBalance())
                        .interestRate(d.getInterestRate())
                        .startDate(d.getLoanStartDate())
                        .endDate(d.getLoanEndDate())
                        .repaymentType(d.getRepaymentType())
                        .paymentDate(d.getLoanStartDate())
                        .build())
                .toList();

        // 전체 DSR 계산(기존+신규)
        return DsrCalculator.calculateDsr(existingLoans, newMonthly, request.getAnnualIncome(), loanCalculator);
    }

    @Override
    public TotalComparisonResultDTO compareTotalRepaymentWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan) {
        BigDecimal existingTotal = calculateTotalRepayment(existingLoans);

        List<LoanDTO> loansWithNew = new ArrayList<>(existingLoans);
        loansWithNew.add(newLoan);
        BigDecimal withNewLoanTotal = calculateTotalRepayment(loansWithNew);

        BigDecimal increaseAmount = withNewLoanTotal.subtract(existingTotal);
        BigDecimal increaseRate = existingTotal.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : increaseAmount.divide(existingTotal, 4, RoundingMode.HALF_UP);

        return new TotalComparisonResultDTO(
                existingTotal.setScale(0, RoundingMode.HALF_UP),
                withNewLoanTotal.setScale(0, RoundingMode.HALF_UP),
                increaseRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Override
    public InterestComparisonResultDTO compareInterestWithNewLoan(List<LoanDTO> existingLoans, LoanDTO newLoan) {
        BigDecimal existingTotalRepayment = calculateTotalRepayment(existingLoans);
        BigDecimal existingPrincipalSum = existingLoans.stream()
                .map(LoanDTO::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal existingInterest = existingTotalRepayment.subtract(existingPrincipalSum);

        List<LoanDTO> withNewLoanList = new ArrayList<>(existingLoans);
        withNewLoanList.add(newLoan);
        BigDecimal withNewLoanTotalRepayment = calculateTotalRepayment(withNewLoanList);
        BigDecimal withNewLoanPrincipalSum = withNewLoanList.stream()
                .map(LoanDTO::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withNewLoanInterest = withNewLoanTotalRepayment.subtract(withNewLoanPrincipalSum);

        BigDecimal increaseAmount = withNewLoanInterest.subtract(existingInterest);
        BigDecimal increaseRate = existingInterest.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : increaseAmount.divide(existingInterest, 4, RoundingMode.HALF_UP);

        return new InterestComparisonResultDTO(
                existingInterest.setScale(0, RoundingMode.HALF_UP),
                withNewLoanInterest.setScale(0, RoundingMode.HALF_UP)
        );
    }

    @Override
    public List<MonthlyInterestComparisonDTO> getMonthlyInterestComparison(
            Long userId,
            LoanAnalyzeRequestDTO request,
            Scenario scenario,
            BigDecimal monthlyPrepayment
    ) {
        // 기존 대출
        List<LoanDTO> existingLoans = convertToLoanDTOList(debtService.getUserDebtList(userId));
        List<MonthlyPaymentDetailDTO> existingPayments = calculateCombinedMonthlyPayments(
                existingLoans, scenario, monthlyPrepayment
        );

        // 신규 대출 포함
        List<LoanDTO> allLoans = new ArrayList<>(existingLoans);
        LoanDTO newLoan = fromAnalyzeRequest(request);
        allLoans.add(newLoan);
        List<MonthlyPaymentDetailDTO> afterAddingPayments = calculateCombinedMonthlyPayments(
                allLoans, scenario, monthlyPrepayment
        );

        // 월별 합계 Map 생성
        Map<YearMonth, MonthlyInterestComparisonDTO> monthlyMap = new LinkedHashMap<>();

        // 기존 대출 합산
        for (MonthlyPaymentDetailDTO existing : existingPayments) {
            YearMonth ym = YearMonth.from(existing.getPaymentDate());
            monthlyMap.computeIfAbsent(ym, k -> new MonthlyInterestComparisonDTO(
                    k.atEndOfMonth(), // 월말 날짜
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            ));
            MonthlyInterestComparisonDTO dto = monthlyMap.get(ym);
            dto.setExistingInterest(dto.getExistingInterest().add(existing.getInterest()));
            dto.setExistingPayment(dto.getExistingPayment().add(existing.getTotalPayment()));
        }

        // 신규 대출 포함 합산
        for (MonthlyPaymentDetailDTO after : afterAddingPayments) {
            YearMonth ym = YearMonth.from(after.getPaymentDate());
            monthlyMap.computeIfAbsent(ym, k -> new MonthlyInterestComparisonDTO(
                    k.atEndOfMonth(),
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            ));
            MonthlyInterestComparisonDTO dto = monthlyMap.get(ym);
            dto.setAfterAddingInterest(dto.getAfterAddingInterest().add(after.getInterest()));
            dto.setAfterAddingPayment(dto.getAfterAddingPayment().add(after.getTotalPayment()));
        }

        return new ArrayList<>(monthlyMap.values());
    }

    private BigDecimal calculateTotalRepayment(List<LoanDTO> loans) {
        BigDecimal totalRepayment = BigDecimal.ZERO;
        for (LoanDTO loan : loans) {
            PaymentResultDTO result = loanFacade.calculateByType(Scenario.NO_PREPAYMENT, loan, BigDecimal.ZERO);
            totalRepayment = totalRepayment.add(result.getTotalPayment());
        }
        return totalRepayment;
    }

    private List<MonthlyPaymentDetailDTO> calculateCombinedMonthlyPayments(
            List<LoanDTO> loans,
            Scenario scenario,
            BigDecimal monthlyPrepayment
    ) {
        if (loans.isEmpty()) {
            return new ArrayList<>();
        }

        // 각 대출별 전체 상환 계획 계산
        List<PaymentResultDTO> loanResults = loans.stream()
                .map(loan -> loanFacade.calculateByType(scenario, loan, monthlyPrepayment))
                .collect(Collectors.toList());

        // 모든 상환일자 수집 및 정렬
        List<LocalDate> allDates = loanResults.stream()
                .flatMap(result -> result.getPayments().stream())
                .map(MonthlyPaymentDetailDTO::getPaymentDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 날짜별로 상환내역 합산
        List<MonthlyPaymentDetailDTO> combinedPayments = new ArrayList<>();

        for (LocalDate date : allDates) {
            BigDecimal totalPrincipal = BigDecimal.ZERO;
            BigDecimal totalInterest = BigDecimal.ZERO;
            BigDecimal totalPrepayment = BigDecimal.ZERO;
            BigDecimal totalFee = BigDecimal.ZERO;
            BigDecimal totalMonthlyPayment = BigDecimal.ZERO;
            BigDecimal totalRemainingPrincipal = BigDecimal.ZERO;

            // 해당 날짜의 모든 대출 상환내역 합산
            for (PaymentResultDTO loanResult : loanResults) {
                MonthlyPaymentDetailDTO paymentForDate = loanResult.getPayments().stream()
                        .filter(payment -> payment.getPaymentDate().equals(date))
                        .findFirst()
                        .orElse(null);

                if (paymentForDate != null) {
                    totalPrincipal = totalPrincipal.add(paymentForDate.getPrincipal());
                    totalInterest = totalInterest.add(paymentForDate.getInterest());
                    totalPrepayment = totalPrepayment.add(paymentForDate.getPrepayment());
                    totalFee = totalFee.add(paymentForDate.getPrepaymentFee());
                    totalMonthlyPayment = totalMonthlyPayment.add(paymentForDate.getTotalPayment());
                }
            }

            // 해당 날짜의 모든 대출 잔여원금 합산
            for (int i = 0; i < loans.size(); i++) {
                PaymentResultDTO loanResult = loanResults.get(i);
                BigDecimal remainingForLoan = getRemainingPrincipalAtDate(loanResult, date);
                totalRemainingPrincipal = totalRemainingPrincipal.add(remainingForLoan);
            }


            if (totalMonthlyPayment.compareTo(BigDecimal.ZERO) > 0) {
                combinedPayments.add(new MonthlyPaymentDetailDTO(
                        totalPrincipal,
                        totalInterest,
                        totalPrepayment,
                        totalFee,
                        totalMonthlyPayment,
                        date,
                        totalRemainingPrincipal
                ));
            }
        }

        return combinedPayments;
    }

    private BigDecimal getRemainingPrincipalAtDate(PaymentResultDTO loanResult, LocalDate targetDate) {
        return loanResult.getPayments().stream()
                .filter(payment -> payment.getPaymentDate().equals(targetDate))
                .findFirst()
                .map(MonthlyPaymentDetailDTO::getRemainingPrincipal)
                .orElse(BigDecimal.ZERO);
    }


    private List<LoanDTO> convertToLoanDTOList(List<DebtInfoResponseDTO> debtInfoList) {
        return debtInfoList.stream()
                .map(debt -> LoanDTO.builder()
                        .id(debt.getDebtId())
                        .loanName(debt.getDebtName())
                        .institutionType(debt.getOrganizationName())
                        .principal(debt.getCurrentBalance())
                        .interestRate(debt.getInterestRate())
                        .startDate(debt.getLoanStartDate())
                        .endDate(debt.getLoanEndDate())
                        .repaymentType(debt.getRepaymentType())
                        .prepaymentFeeRate(BigDecimal.valueOf(0.01)) // 중도상환수수료 1%로 고정
                        .paymentDate(debt.getNextPaymentDate())
                        .build())
                .collect(Collectors.toList());
    }

    private LoanDTO fromAnalyzeRequest(LoanAnalyzeRequestDTO request) {
        LocalDate start = LocalDate.now();

        return LoanDTO.builder()
                .loanName("분석대출") // 예시 이름
                .principal(request.getLoanAmount())
                .interestRate(request.getInterestRate())
                .repaymentType(request.getRepaymentType()) // 문자열로 변환
                .startDate(start)
                .endDate(start.plusMonths(request.getLoanPeriod() * 12))
                .institutionType("기타")
                .prepaymentFeeRate(BigDecimal.ZERO)
                .paymentDate(start.withDayOfMonth(1)) // 예시: 매달 1일 납부
                .build();
    }
}
