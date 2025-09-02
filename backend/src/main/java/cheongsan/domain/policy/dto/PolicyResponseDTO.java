package cheongsan.domain.policy.dto;

import lombok.Data;

import java.util.List;

@Data
public class PolicyResponseDTO {
    private String ministryName;   // 소관 부처명 (ex: 보건복지부)
    private String policySummary; // 정책 한줄 요약
    private String policyId;   // 정책(서비스) 고유 ID
    private String policyName; // 정책명
    private String supportCycle;    // 지원 주기 (ex: 월)
    private List<String> tagList; // 관심 주제 배열 (ex: 생활지원, 보호·돌봄) + 주요 지원대상 (ex: 저소득)
    private String policyOnline;
    private String policyDate;
}
