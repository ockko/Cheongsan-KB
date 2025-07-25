package cheongsan.domain.policy.dto;

import lombok.Data;

@Data
public class PolicyRequestDTO {
    // 기본값 입력
    private String callTp = "L";         // 필수
    private Integer pageNo = 1;            // 필수
    private Integer numOfRows = 200;         // 필수
    private String srchKeyCode = "003";    // 필수
    private String searchWrd;      // 선택
}
