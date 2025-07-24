package cheongsan.domain.policy.controller;


import cheongsan.domain.policy.dto.DiagnosisDTO;
import cheongsan.domain.policy.dto.UserDiagnosisDTO;
import cheongsan.domain.policy.service.DiagnosisService;
import cheongsan.domain.user.dto.UserDTO;
import cheongsan.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/answer")
    public ResponseEntity<DiagnosisDTO> submitDiagnosisAnswer(
            @RequestBody UserDiagnosisDTO userDiagnosisDTO
    ) {
        // (1) DTO 파싱: 프론트가 보낸 JSON이 UserDiagnosisDTO로 자동 매핑됨
        // (2) 서비스에 DTO 전달하여 진단 결과 반환
        DiagnosisDTO result = diagnosisService.processDiagnosis(userDiagnosisDTO);

        // 현재 로그인한 사용자 ID를 가져옵니다. (실제 구현에서는 Spring Security 등 사용)
//        Long currentUserId = getCurrentUserId();
        Long currentUserId = new Long(1);
        Long recommendDiagnosisId = new Long(result.getId());

        // UserService를 호출하여 DB에 ID를 저장합니다.
        userService.submitDiagnosisAnswerToUser(currentUserId, recommendDiagnosisId);

        // (3) 결과를 응답으로 전달
        return ResponseEntity.ok(result);
    }

//      JWT 구현 이후
//    @GetMapping("/result")
//    public ResponseEntity<DiagnosisDTO> getDiagnosisResult(
//            // 1. @AuthenticationPrincipal 어노테이션을 사용합니다.
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) {
//        // 2. Spring Security가 자동으로 로그인한 사용자의 정보를 넣어줍니다.
//        Long currentUserId = userDetails.getUserId(); // 예시: userDetails 객체에서 ID를 가져옵니다.
//
//        // 3. ID를 BigInteger로 변환합니다.
//        BigInteger recommendDiagnosisId = userService.getUser(new BigInteger(String.valueOf(currentUserId)));
//        DiagnosisDTO diagnosisDTO = diagnosisService.getDiagnosis(recommendDiagnosisId);
//
//        return ResponseEntity.ok(diagnosisDTO);
//    }

    @GetMapping("/result")
    public ResponseEntity<DiagnosisDTO> getDiagnosisResult() {
        // 현재 유저 아이디 받기
        Long currentUserId = new Long(1);   //임시

        // 유저 정보 가져오기z

        UserDTO userDTO = userService.getUser(currentUserId);
        log.info("userDTO={}", userDTO.getEmail());

        // 유저 정보 중 채무조정제도 진단 결과의 아이디 받아오기
        Long recommendedDiagnosisId = userDTO.getRecommendedProgramId();
        log.info("recommendDiagnosisId={}", recommendedDiagnosisId);
        if (recommendedDiagnosisId == null) {
            log.info("사용자(ID: {})가 추천받은 진단 결과가 없습니다. 진단을 먼저 진행하도록 안내합니다.", currentUserId);
            // 결과가 없음을 의미하는 204 No Content 응답을 보내거나,
            // "진단이 필요합니다" 라는 메시지를 담은 별도의 DTO를 보낼 수 있습니다.
            return ResponseEntity.noContent().build();
        }
        // ------------------------------------
        DiagnosisDTO diagnosisDTO = diagnosisService.getDiagnosis(recommendedDiagnosisId);


        return ResponseEntity.ok(diagnosisDTO);
    }

    @GetMapping("/result/{diagnosisId}")
    public ResponseEntity<DiagnosisDTO> getDebtProgramDetail(
            @PathVariable("diagnosisId") Long diagnosisId) {
        DiagnosisDTO diagnosisDTO = diagnosisService.getDiagnosis(diagnosisId);

        log.info("diagnosisDTO={}", diagnosisDTO);
        log.info("diagnosisDTO content={}", diagnosisDTO.getProgramName());
        return ResponseEntity.ok(diagnosisDTO);
    }


}
