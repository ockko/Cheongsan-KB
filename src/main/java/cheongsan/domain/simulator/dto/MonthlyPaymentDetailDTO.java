package cheongsan.domain.simulator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MonthlyPaymentDetailDTO {
    private BigDecimal principal; // 해당 월에 상환한 원금
    private BigDecimal interest; //  해당 월에 발생한 이자
    private BigDecimal prepayment; // 중도상환 금액
    private BigDecimal prepaymentFee; // 중도상환 수수료
    private BigDecimal totalPayment; // 해당 월의 총 납입액 (원금 + 이자)
    private LocalDate paymentDate; // 실제 납입 날
    private BigDecimal remainingPrincipal; // 이달 납부 후 남은 원금

    @JsonCreator
    public MonthlyPaymentDetailDTO(
            @JsonProperty("principal") BigDecimal principal,
            @JsonProperty("interest") BigDecimal interest,
            @JsonProperty("prepayment") BigDecimal prepayment,
            @JsonProperty("prepaymentFee") BigDecimal prepaymentFee,
            @JsonProperty("totalPayment") BigDecimal totalPayment,
            @JsonProperty("paymentDate") LocalDate paymentDate,
            @JsonProperty("remainingPrincipal") BigDecimal remainingPrincipal
    ) {
        this.principal = principal;
        this.interest = interest;
        this.prepayment = prepayment;
        this.prepaymentFee = prepaymentFee;
        this.totalPayment = totalPayment;
        this.paymentDate = paymentDate;
        this.remainingPrincipal = remainingPrincipal;
    }
}
