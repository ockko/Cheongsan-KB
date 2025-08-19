package cheongsan.domain.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendationResultDTO {
    private final BigDecimal dsr;
    private final List<LoanProductDTO> items;
}
