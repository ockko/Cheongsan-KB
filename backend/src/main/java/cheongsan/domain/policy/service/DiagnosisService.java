package cheongsan.domain.policy.service;

import cheongsan.domain.policy.dto.DiagnosisResponseDTO;
import cheongsan.domain.policy.dto.SimpleDiagnosisResponseDTO;
import cheongsan.domain.policy.dto.UserDiagnosisRequestDTO;

public interface DiagnosisService {
    /**
     * 진단 응답을 분석해, 유저 상태 판정 및 추천 정책 반환
     */
    DiagnosisResponseDTO processDiagnosis(UserDiagnosisRequestDTO request);

    DiagnosisResponseDTO getDiagnosis(Long diagnosisId);

    public SimpleDiagnosisResponseDTO getSimpleDiagnosis(Long diagnosisId);
}
