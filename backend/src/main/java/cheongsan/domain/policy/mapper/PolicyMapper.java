package cheongsan.domain.policy.mapper;

import cheongsan.domain.policy.entity.Diagnosis;
import cheongsan.domain.policy.entity.SimpleDiagnosis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PolicyMapper {
    Diagnosis getDiagnosisResult(@Param("id") Long workOutId);

    SimpleDiagnosis getSimpleDiagnosisResult(@Param("id") Long workOutId);
}
