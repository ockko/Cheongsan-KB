package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDTO {
    private String strategy;
    private BigDecimal totalInterest;
    private BigDecimal monthlyPayment;
    private String description;
}
