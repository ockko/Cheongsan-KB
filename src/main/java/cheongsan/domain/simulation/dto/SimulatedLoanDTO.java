package cheongsan.domain.simulation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SimulatedLoanDTO {
    private LoanDTO baseLoan;
    private BigDecimal remainingPrincipal;
    private boolean finished;
    private LocalDate currentDate; // 대출별 상환 시작일
    private List<MonthlyPaymentDetailDTO> paymentHistory = new ArrayList<>();
    private LocalDate debtFreeDate; // 마지막 상환일

    // private 생성자: 내부 초기화 로직 포함
    private SimulatedLoanDTO(LoanDTO loan) {
        this.baseLoan = loan;
        this.remainingPrincipal = loan.getPrincipal();
        this.finished = false;
    }

    public void incrementMonth() {
        this.currentDate = this.currentDate.plusMonths(1);
    }

    // 정적 팩토리 메서드: 명시적 생성 방식
    public static SimulatedLoanDTO from(LoanDTO loan, LocalDate startDate) {
        SimulatedLoanDTO dto = new SimulatedLoanDTO();
        dto.baseLoan = LoanDTO.builder()
                .id(loan.getId())
                .loanName(loan.getLoanName())
                .principal(loan.getPrincipal())
                .interestRate(loan.getInterestRate())
                .startDate(loan.getStartDate())
                .endDate(loan.getEndDate())
                .institutionType(loan.getInstitutionType())
                .repaymentType(loan.getRepaymentType())
                .prepaymentFeeRate(loan.getPrepaymentFeeRate())
                .paymentDate(loan.getPaymentDate())
                .build();

        dto.remainingPrincipal = loan.getPrincipal();           // 남은 원금 초기화
        dto.currentDate = startDate;                            // 시뮬레이션 시작 날짜
        dto.paymentHistory = new ArrayList<>();                 // 납부 내역 초기화
        dto.finished = false;                                   // 상환 완료 여부 초기화
        dto.debtFreeDate = null;                                // 완제일 초기화
        return dto;
    }


    // 매월 상환금 적용 로직
    public void applyMonthlyPayment(MonthlyPaymentDetailDTO payment) {
        if (payment == null) return;

        BigDecimal totalPayment = payment.getPrincipal().add(payment.getPrepayment());

        // 남은 원금보다 상환 금액이 크면 잘라냄
        if (totalPayment.compareTo(remainingPrincipal) > 0) {
            BigDecimal adjustedPrincipal = payment.getPrincipal();
            BigDecimal adjustedPrepayment = payment.getPrepayment();

            // 1. 우선 중도상환액으로 줄이기
            if (adjustedPrepayment.compareTo(remainingPrincipal) >= 0) {
                adjustedPrepayment = remainingPrincipal;
                adjustedPrincipal = BigDecimal.ZERO;
            } else {
                BigDecimal leftover = remainingPrincipal.subtract(adjustedPrepayment);
                if (adjustedPrincipal.compareTo(leftover) > 0) {
                    adjustedPrincipal = leftover;
                }
            }

            totalPayment = adjustedPrincipal.add(adjustedPrepayment);

            // 갱신
            payment.setPrincipal(adjustedPrincipal);
            payment.setPrepayment(adjustedPrepayment);
        }

        // 원금 차감
        remainingPrincipal = remainingPrincipal.subtract(totalPayment).setScale(0, RoundingMode.HALF_UP);
        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            remainingPrincipal = BigDecimal.ZERO;
            finished = true;
        }

        this.paymentHistory.add(payment);
    }


}