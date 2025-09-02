package cheongsan.domain.simulation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RepaymentSimulationMapper {

    String findByUserIdAndStrategyType(@Param("userId") Long userId,
                                       @Param("cacheKey") String cacheKey);

    int insertCacheData(@Param("userId") Long userId,
                        @Param("cacheKey") String cacheKey,
                        @Param("jsonValue") String jsonValue);

    int deleteByUserId(@Param("userId") Long userId);
}
