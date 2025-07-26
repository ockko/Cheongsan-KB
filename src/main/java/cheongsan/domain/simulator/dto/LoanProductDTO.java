package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LoanProductDTO {
    private String productName;       // 상품명
    private String institutionName;   // 기관명
    private BigDecimal interestRate;  // 상품 금리
    private BigDecimal loanLimit;     // 최대 한도
    private String siteUrl;           // 사이트 URL
}
