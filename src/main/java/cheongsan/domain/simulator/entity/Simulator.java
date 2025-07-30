package cheongsan.domain.simulator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Simulator {
    private String productName;      // 상품명
    private String institutionName;       // 기관명
    private String interestRate;           // 상품 금리
    private String loanLimit;         // 최대 한도
    private String siteUrl;       // 사이트 URL
    private String dsrCheck;      // DSR 적용 여부('Y' or 'N')
}
