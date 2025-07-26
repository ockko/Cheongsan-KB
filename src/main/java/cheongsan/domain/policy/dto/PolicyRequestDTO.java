package cheongsan.domain.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestDTO {
    // 기본값 입력
    private String callTp = "L";         // 필수
    private Integer pageNo = 1;            // 필수
    private Integer numOfRows = 200;         // 필수
    private String srchKeyCode = "003";    // 필수
    private String searchWrd;      // 선택
}
