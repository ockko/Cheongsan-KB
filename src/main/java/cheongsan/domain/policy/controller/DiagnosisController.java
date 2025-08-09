package cheongsan.domain.policy.controller;


import cheongsan.common.util.ExtractUserIdUtil;
import cheongsan.domain.policy.dto.DiagnosisResponseDTO;
import cheongsan.domain.policy.dto.SimpleDiagnosisResponseDTO;
import cheongsan.domain.policy.dto.UserDiagnosisRequestDTO;
import cheongsan.domain.policy.service.DiagnosisService;
import cheongsan.domain.user.dto.UserDTO;
import cheongsan.domain.user.entity.CustomUser;
import cheongsan.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cheongsan/diagnosis")
@RequiredArgsConstructor
@Log4j2
public class DiagnosisController {
    private final DiagnosisService diagnosisService;
    private final UserService userService;
    private final ExtractUserIdUtil extractUserIdUtil;

    /**
     * 진단 설문 제출 API
     * - 프론트엔드는 UserDiagnosisDTO 구조의 JSON을 POST로 전송
     * - 컨트롤러에서는 해당 DTO를 파싱 후 서비스에 전달
     */


    @PostMapping("/submit")
    public ResponseEntity<DiagnosisResponseDTO> submitDiagnosisSurvey(
            @RequestBody UserDiagnosisRequestDTO userDiagnosisRequestDTO,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUser().getId();
        try {
            DiagnosisResponseDTO result = diagnosisService.processDiagnosis(userDiagnosisRequestDTO);
            Long recommendDiagnosisId = result.getId();

            userService.submitDiagnosisAnswerToUser(userId, recommendDiagnosisId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing diagnosis survey for userId: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process diagnosis survey.");
        }
    }

    /**
     * 진단 결과 조회 API
     */
    @GetMapping("/result")
    public ResponseEntity<SimpleDiagnosisResponseDTO> getDiagnosisResult(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long currentUserId = customUser.getUser().getId();
        try {
            UserDTO userDTO = userService.getUser(currentUserId);
            log.info("userDTO email={}", userDTO.getEmail());

            Long recommendedDiagnosisId = userDTO.getRecommendedProgramId();
            if (recommendedDiagnosisId == null) {
                log.info("User(ID: {}) has no recommended diagnosis result. Prompt to complete diagnosis first.", currentUserId);
                return ResponseEntity.noContent().build();
            }

            SimpleDiagnosisResponseDTO simpleDiagnosisResponseDTO = diagnosisService.getSimpleDiagnosis(recommendedDiagnosisId);
            log.info("simpleDiagnosisDTO={}", simpleDiagnosisResponseDTO);
            simpleDiagnosisResponseDTO.setNickName(userDTO.getNickname());

            return ResponseEntity.ok(simpleDiagnosisResponseDTO);
        } catch (Exception e) {
            log.error("Error retrieving diagnosis result for userId: {}", currentUserId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve diagnosis result.");
        }
    }

    /**
     * 특정 진단 프로그램 상세 조회 API
     */
    @GetMapping("/result/{diagnosisId}")
    public ResponseEntity<DiagnosisResponseDTO> getDiagnosisProgramDetail(
            @PathVariable("diagnosisId") Long diagnosisId) {
        if (diagnosisId == null || diagnosisId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid diagnosisId parameter.");
        }

        try {
            DiagnosisResponseDTO diagnosisResponseDTO = diagnosisService.getDiagnosis(diagnosisId);
            if (diagnosisResponseDTO == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diagnosis program not found for id: " + diagnosisId);
            }
            log.info("diagnosisDTO={}", diagnosisResponseDTO);
            return ResponseEntity.ok(diagnosisResponseDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching diagnosis program detail for diagnosisId: {}", diagnosisId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch diagnosis program detail.");
        }
    }


}
