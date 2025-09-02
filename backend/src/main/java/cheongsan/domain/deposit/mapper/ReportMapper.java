package cheongsan.domain.deposit.mapper;

import cheongsan.domain.deposit.entity.WeeklyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReportMapper {

    void save(WeeklyReport report);

    WeeklyReport findLatestByUserId(Long userId);

    List<WeeklyReport> findAllByUserId(Long userId);

    WeeklyReport findByDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
