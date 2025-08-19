package cheongsan.domain.simulation.mapper;

import cheongsan.domain.simulation.entity.Simulation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface LoanProductMapper {
    List<Simulation> findRecommendedLoans(
            @Param("principal") BigDecimal principal
    );
}
