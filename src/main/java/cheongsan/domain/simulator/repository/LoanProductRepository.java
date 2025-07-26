package cheongsan.domain.simulator.repository;

import cheongsan.domain.simulator.dto.LoanProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface LoanProductRepository {
    List<LoanProductDTO> findRecommendedLoans(
            @Param("principal") BigDecimal principal,
            @Param("userDsr") BigDecimal userDsr,
            @Param("dsrLimit") BigDecimal dsrLimit
    );
}
