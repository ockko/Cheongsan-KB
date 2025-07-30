package cheongsan.domain.simulator.mapper;

import cheongsan.domain.simulator.entity.Simulator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface LoanProductMapper {
    List<Simulator> findRecommendedLoans(
            @Param("principal") BigDecimal principal
    );
}
