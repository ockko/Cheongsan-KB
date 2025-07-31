package cheongsan.domain.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class GraphDTO {
    private String label;   // 기존, 신규 포함
    private BigDecimal value;   // 값(ex. 상환액, 이자, DSR 등)
}
