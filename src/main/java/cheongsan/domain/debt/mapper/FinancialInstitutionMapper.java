package cheongsan.domain.debt.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FinancialInstitutionMapper {
    // 금융 기관명으로 금융 기관 코드 조회
    Long findCodeByName(@Param("organizationName") String organizationName);

    String findNameByCode(@Param("code") long code);

    // 금융 기관 데이터 적재
    void insertInstitution(@Param("organizationName") String organizationName);
}
