package cheongsan.domain.codef.controller;

import cheongsan.common.exception.ResponseDTO;
import cheongsan.domain.codef.service.CodefSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cheongsan/mydata")
@RequiredArgsConstructor
@Log4j2
public class CodefController {
    private final CodefSyncService codefSyncService;

    /**
     * 사용자 계좌 데이터 동기화 새로고침 전용
     */
    @PostMapping("/sync/{userId}")
    public ResponseEntity<?> startMyDataSync(@PathVariable Long userId) {
        try {
            log.info("사용자 계좌 데이터 동기화 요청: userId={}", userId);

            codefSyncService.syncUserAccountData(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("message", "계좌 데이터 동기화가 완료되었습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("사용자 계좌 데이터 동기화 실패: 잘못된 요청", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            log.error("사용자 계좌 데이터 동기화 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("계좌 데이터 동기화에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 계좌 연동 상태 확인
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getMyDataStatus(@PathVariable Long userId) {
        try {
            boolean isLinked = codefSyncService.isAccountLinked(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("isLinked", isLinked);
            response.put("message", isLinked ? "계좌가 연동되어 있습니다." : "계좌가 연동되어 있지 않습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("계좌 연동 상태 확인 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("계좌 연동 상태 확인에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 전체 사용자 배치 동기화 (관리자용)
     */

}
