package cheongsan.domain.policy.controller;


import cheongsan.domain.policy.dto.DiagnosisDTO;
import cheongsan.domain.policy.dto.SimpleDiagnosisDTO;
import cheongsan.domain.policy.dto.UserDiagnosisDTO;
import cheongsan.domain.policy.service.DiagnosisService;
import cheongsan.domain.user.dto.UserDTO;
import cheongsan.domain.user.entity.CustomUser;
import cheongsan.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/cheongsan/diagnosis")
@RequiredArgsConstructor
@Log4j2
public class DiagnosisController {
    private final DiagnosisService diagnosisService;
    private final UserService userService;

    /**
     * 진단 설문 제출 API
     * - 프론트엔드는 UserDiagnosisDTO 구조의 JSON을 POST로 전송
     * - 컨트롤러에서는 해당 DTO를 파싱 후 서비스에 전달
     */
    @PostMapping("/submit")
    public ResponseEntity<DiagnosisDTO> submitDiagnosisSurvey(
            @RequestBody UserDiagnosisDTO userDiagnosisDTO,
            Principal principal
    ) {
        DiagnosisDTO result = diagnosisService.processDiagnosis(userDiagnosisDTO);

        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long currentUserId = customUser.getUser().getId();
        Long recommendDiagnosisId = result.getId();

        userService.submitDiagnosisAnswerToUser(currentUserId, recommendDiagnosisId);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/result")
    public ResponseEntity<SimpleDiagnosisDTO> getDiagnosisResult(
            Principal principal
    ) {
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long currentUserId = customUser.getUser().getId();

        UserDTO userDTO = userService.getUser(currentUserId);
        log.info("userDTO={}", userDTO.getEmail());

        Long recommendedDiagnosisId = userDTO.getRecommendedProgramId();
        log.info("recommendDiagnosisId={}", recommendedDiagnosisId);
        if (recommendedDiagnosisId == null) {
            log.info("사용자(ID: {})가 추천받은 진단 결과가 없습니다. 진단을 먼저 진행하도록 안내합니다.", currentUserId);
            // 진단페이지를 띄워놔야한다.
            return ResponseEntity.noContent().build();
        }
        SimpleDiagnosisDTO simpleDiagnosisDTO = diagnosisService.getSimpleDiagnosis(recommendedDiagnosisId);
        log.info("simpleDiagnosisDTO={}", simpleDiagnosisDTO.toString());
        simpleDiagnosisDTO.setNickName(customUser.getUser().getNickname());

        return ResponseEntity.ok(simpleDiagnosisDTO);
    }

    @GetMapping("/result/{diagnosisId}")
    public ResponseEntity<DiagnosisDTO> getDiagnosisProgramDetail(
            @PathVariable("diagnosisId") Long diagnosisId) {
        DiagnosisDTO diagnosisDTO = diagnosisService.getDiagnosis(diagnosisId);

        log.info("diagnosisDTO={}", diagnosisDTO);
        log.info("diagnosisDTO content={}", diagnosisDTO.getProgramName());
        return ResponseEntity.ok(diagnosisDTO);
    }


}
