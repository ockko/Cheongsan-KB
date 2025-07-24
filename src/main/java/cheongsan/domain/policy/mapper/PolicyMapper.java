package cheongsan.domain.policy.mapper;

import cheongsan.domain.policy.entity.DiagnosisEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PolicyMapper {
    DiagnosisEntity getDiagnosisResult(@Param("id") Long workOutId);

}
