package cheongsan.domain.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentRequestDTO {
    private List<LoanDTO> loans = new ArrayList<>();
    private BigDecimal monthlyAvailableAmount;
}
