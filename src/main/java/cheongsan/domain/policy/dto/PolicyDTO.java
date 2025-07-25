package cheongsan.domain.policy.dto;

import lombok.Data;

import java.util.List;

@Data
public class PolicyDTO {
    private String jurMnofNm;   // 소관 부처명 (ex: 보건복지부)
    private String summary; // 정책 한줄 요약
    private String serviceId;   // 정책(서비스) 고유 ID
    private String serviceName; // 정책명
    private String supportCycle;    // 지원 주기 (ex: 월)
    private List<String> tagList; // 관심 주제 배열 (ex: 생활지원, 보호·돌봄) + 주요 지원대상 (ex: 저소득)
}
