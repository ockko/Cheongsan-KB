package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImpactDTO {
    private List<BigDecimal> principalSeries = new ArrayList<>();
    private List<BigDecimal> interestSeries = new ArrayList<>();
    private List<BigDecimal> totalPaymentSeries = new ArrayList<>();
}
