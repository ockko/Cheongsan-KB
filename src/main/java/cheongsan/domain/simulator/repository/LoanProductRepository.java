package cheongsan.domain.simulator.repository;

import cheongsan.domain.simulator.dto.LoanProductDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LoanProductRepository {
    List<LoanProductDTO> findRecommendedLoans(
            @Param("principal") BigDecimal principal
    );
}
