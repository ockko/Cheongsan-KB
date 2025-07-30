package cheongsan.domain.codef.controller;

import cheongsan.common.exception.ResponseDTO;
import cheongsan.common.util.CodefRSAUtil;
import cheongsan.domain.codef.dto.AccountListResponseDTO;
import cheongsan.domain.codef.dto.ConnectedIdRequestDTO;
import cheongsan.domain.codef.dto.LoanTransactionResponseDTO;
import cheongsan.domain.codef.dto.TransactionListResponseDTO;
import cheongsan.domain.codef.service.CodefService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cheongsan/codef")
@RequiredArgsConstructor
@Log4j2
public class CodefController {
    private final CodefService codefService;
    private final CodefRSAUtil codefRSAUtil;


    /**
     * Connected ID 생성 (계정 등록)
     */
    @PostMapping("/account/create")
    public ResponseEntity<?> createConnectedId(@RequestBody ConnectedIdRequestDTO requestDTO) {
        try {
            log.info("Connected ID 생성 요청: {}", requestDTO);

            String connectedId = codefService.createConnectedId(requestDTO);

            Map<String, String> response = new HashMap<>();
            response.put("connectedId", connectedId);
            response.put("message", "Connected ID 생성 성공");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Connected ID 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("Connected ID 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 보유 계좌 조회
     */
    @PostMapping("/account/list")
    public ResponseEntity<?> getAccountList(
            @RequestParam String connectedId,
            @RequestParam String organizationCode) {
        try {
            log.info("계좌 목록 조회 요청: connectedId={}, organizationCode={}", connectedId, organizationCode);

            AccountListResponseDTO accountList = codefService.getAccountList(connectedId, organizationCode);

            return ResponseEntity.ok(accountList);

        } catch (Exception e) {
            log.error("계좌 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("계좌 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 수시입출 거래내역 조회
     */
    @PostMapping("/transaction/list")
    public ResponseEntity<?> getTransactionList(
            @RequestParam String connectedId,
            @RequestParam String organizationCode,
            @RequestParam String accountNumber,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            log.info("거래내역 조회 요청: connectedId={}, organizationCode={}, accountNumber={}, startDate={}, endDate={}",
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            // ✅ 날짜가 없으면 기본값 설정 (최근 3개월)
            if (startDate == null || startDate.isEmpty()) {
                startDate = LocalDate.now().minusMonths(3).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            if (endDate == null || endDate.isEmpty()) {
                endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }

            // ✅ Service 메서드에 날짜 파라미터 전달
            TransactionListResponseDTO transactionList = codefService.getTransactionListWithDates(
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            return ResponseEntity.ok(transactionList);

        } catch (Exception e) {
            log.error("거래내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("거래내역 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 대출 거래내역 조회
     */
    @PostMapping("/loan/transaction")
    public ResponseEntity<?> getLoanTransactionList(
            @RequestParam String connectedId,
            @RequestParam String organizationCode,
            @RequestParam String accountNumber,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            log.info("대출 거래내역 조회 요청: connectedId={}, organizationCode={}, accountNumber={}, startDate={}, endDate={}",
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            LoanTransactionResponseDTO loanTransactionList = codefService.getLoanTransactionList(
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            return ResponseEntity.ok(loanTransactionList);

        } catch (Exception e) {
            log.error("대출 거래내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("대출 거래내역 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자 계좌 데이터 전체 동기화
     */
    @PostMapping("/sync/{userId}")
    public ResponseEntity<?> syncUserAccountData(@PathVariable Long userId) {
        try {
            log.info("사용자 계좌 데이터 동기화 요청: userId={}", userId);

            codefService.syncUserAccountData(userId);

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
     * 토큰 발급 테스트용 엔드포인트
     */
    @GetMapping("/token/test")
    public ResponseEntity<?> testToken() {
        try {
            String token = codefService.getAccessToken();

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "토큰 발급 성공");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("토큰 발급 테스트 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("토큰 발급에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자 Connected ID 조회
     */
    @GetMapping("/connected-id/{userId}")
    public ResponseEntity<?> getConnectedIdByUserId(@PathVariable Long userId) {
        try {
            String connectedId = codefService.getConnectedIdByUserId(userId);

            if (connectedId == null || connectedId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO("해당 사용자의 Connected ID가 없습니다."));
            }

            Map<String, String> response = new HashMap<>();
            response.put("connectedId", connectedId);
            response.put("userId", userId.toString());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(e.getMessage()));
        } catch (Exception e) {
            log.error("Connected ID 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("Connected ID 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 간편 계정 등록 (하나은행 데모용)
     */
    @PostMapping("/account/create/demo")
    public ResponseEntity<?> createDemoAccount(
            @RequestParam String bankId,
            @RequestParam String bankPassword) {
        try {
            log.info("데모 계정 등록 요청: bankId={}", bankId);

            // 하나은행(0081) 데모 계정 생성
            ConnectedIdRequestDTO.AccountInfo accountInfo = ConnectedIdRequestDTO.AccountInfo.builder()
                    .countryCode("KR")
                    .businessType("BK")
                    .organization("0081") // 하나은행
                    .clientType("P")
                    .loginType("1")
                    .id(bankId)
                    .password(bankPassword)
                    .build();

            ConnectedIdRequestDTO requestDTO = ConnectedIdRequestDTO.builder()
                    .accountList(Arrays.asList(accountInfo))
                    .build();

            String connectedId = codefService.createConnectedId(requestDTO);

            Map<String, String> response = new HashMap<>();
            response.put("connectedId", connectedId);
            response.put("message", "데모 계정 등록 성공");
            response.put("organization", "0081");
            response.put("organizationName", "하나은행");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("데모 계정 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("데모 계정 등록에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * RSA 암호화 테스트 엔드포인트 (추가)
     */
    @PostMapping("/test/rsa")
    public ResponseEntity<?> testRSAEncryption(@RequestParam String testPassword) {
        try {
            log.info("RSA 암호화 테스트 요청");

            String encryptedPassword = codefRSAUtil.encryptPassword(testPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("original_length", testPassword.length());
            response.put("encrypted_length", encryptedPassword.length());
            response.put("encryption_success", true);
            response.put("message", "RSA 암호화 테스트 성공");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("RSA 암호화 테스트 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("RSA 암호화 테스트 실패: " + e.getMessage()));
        }
    }
}
