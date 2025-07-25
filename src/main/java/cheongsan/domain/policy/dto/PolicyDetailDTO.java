package cheongsan.domain.policy.dto;


import lombok.Data;

import java.util.List;

@Data
public class PolicyDetailDTO {
    private String policyNumber;            // 정책 조회 번호 (inqNum)
    private String ministryName;            // 소관 부처명 (jurMnofNm)
    private String departmentName;          // 담당 부서명 (jurOrgNm)
    private String policyName;              // 정책명 (servNm)
    private List<String> policyTags;              // 정책 태그 (intrsThemaArray)
    private String policySummary;           // 정책 요약 설명 (servDgst)
    private String supportAge;              // 지원 연령
    private List<String> supportTarget;           // 지원 대상
    private String supportType;             // 지원 방법 (srvPvsnNm)
    private String supportCycle;            // 지원 주기 (sprtCycNm)
    private String isOnlineApplyAvailable;  // 온라인 신청 가능 여부 (onapPsbltYn)
    private String contactNumber;           // 대표 연락처 (rprsCtadr)
    private String detailPageUrl;           // 정책 상세 링크 (servDtlLink)
    private String policyId;                // 정책 서비스 ID (servId)
}
