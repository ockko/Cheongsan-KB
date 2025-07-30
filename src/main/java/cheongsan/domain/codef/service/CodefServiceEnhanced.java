package cheongsan.domain.codef.service;

import cheongsan.common.util.CodefRSAUtil;
import cheongsan.domain.codef.dto.*;
import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.entity.DebtTransaction;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.debt.mapper.DebtTransactionMapper;
import cheongsan.domain.debt.mapper.FinancialInstitutionMapper;
import cheongsan.domain.deposit.entity.DepositAccount;
import cheongsan.domain.deposit.entity.Transaction;
import cheongsan.domain.deposit.mapper.DepositAccountMapper;
import cheongsan.domain.deposit.mapper.DepositMapper;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
@Service
//@RequiredArgsConstructor
public class CodefServiceEnhanced implements CodefService {
    private final UserMapper userMapper;
    private final DebtMapper debtMapper;
    private final DepositMapper depositMapper;
    private final DepositAccountMapper depositAccountMapper;
    private final FinancialInstitutionMapper financialInstitutionMapper;
    private final DebtTransactionMapper debtTransactionMapper;
    private final CodefRSAUtil codefRSAUtil;
    private final ObjectMapper codefObjectMapper;

    @Value("${codef.client-id}")
    private String clientId;

    @Value("${codef.client-secret}")
    private String clientSecret;

    private static final String TOKEN_URL = "https://oauth.codef.io/oauth/token";
    private static final String CREATE_ACCOUNT_URL = "https://development.codef.io/v1/account/create";
    private static final String ACCOUNT_LIST_URL = "https://development.codef.io/v1/kr/bank/p/account/account-list";
    private static final String TRANSACTION_LIST_URL = "https://development.codef.io/v1/kr/bank/p/account/transaction-list";
    private static final String LOAN_TRANSACTION_URL = "https://development.codef.io/v1/kr/bank/p/loan/transaction-list";

    // ✅ 생성자에서 CODEF 전용 ObjectMapper 초기화
    public CodefServiceEnhanced(UserMapper userMapper, DebtMapper debtMapper,
                                DepositMapper depositMapper, DepositAccountMapper depositAccountMapper,
                                FinancialInstitutionMapper financialInstitutionMapper,
                                DebtTransactionMapper debtTransactionMapper,
                                CodefRSAUtil codefRSAUtil) {
        this.userMapper = userMapper;
        this.debtMapper = debtMapper;
        this.depositMapper = depositMapper;
        this.depositAccountMapper = depositAccountMapper;
        this.financialInstitutionMapper = financialInstitutionMapper;
        this.debtTransactionMapper = debtTransactionMapper;
        this.codefRSAUtil = codefRSAUtil;

        // CODEF API 전용 ObjectMapper 설정
        this.codefObjectMapper = new ObjectMapper();
        this.codefObjectMapper.registerModule(new JavaTimeModule());
        this.codefObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.codefObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // ✅ snake_case 전략 제거 (camelCase 유지)
    }


    @Override
    public String getAccessToken() {
        try {
            URL url = new URL(TOKEN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Basic Auth 설정
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Body 작성
            String body = "grant_type=client_credentials&scope=read";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            // 응답 읽기
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            TokenResponseDTO tokenResponse = codefObjectMapper.readValue(response.toString(), TokenResponseDTO.class);
            log.info("토큰 발급 성공");
            return tokenResponse.getAccess_token();

        } catch (Exception e) {
            log.error("토큰 발급 실패", e);
            throw new RuntimeException("CODEF 토큰 발급 실패", e);
        }
    }

    // ✅ 새로 추가할 헬퍼 메서드들

    /**
     * 비밀번호 RSA 암호화
     */
    private ConnectedIdRequestDTO encryptPasswords(ConnectedIdRequestDTO requestDTO) {
        List<ConnectedIdRequestDTO.AccountInfo> encryptedAccountList = new ArrayList<>();

        for (ConnectedIdRequestDTO.AccountInfo account : requestDTO.getAccountList()) {
            ConnectedIdRequestDTO.AccountInfo encryptedAccount = ConnectedIdRequestDTO.AccountInfo.builder()
                    .countryCode(account.getCountryCode())
                    .businessType(account.getBusinessType())
                    .organization(account.getOrganization())
                    .clientType(account.getClientType())
                    .loginType(account.getLoginType())
                    .id(account.getId())
                    .password(codefRSAUtil.encryptPassword(account.getPassword())) // ✅ RSA 암호화
                    .build();

            encryptedAccountList.add(encryptedAccount);
            log.info("비밀번호 RSA 암호화 완료: organization={}, id={}",
                    account.getOrganization(), account.getId());
        }

        return ConnectedIdRequestDTO.builder()
                .accountList(encryptedAccountList)
                .build();
    }

    /**
     * 로그용 비밀번호 마스킹
     */
    private String maskPassword(String jsonBody) {
        return jsonBody.replaceAll("\"password\":\"[^\"]*\"", "\"password\":\"****\"");
    }

    @Override
    public String createConnectedId(ConnectedIdRequestDTO requestDTO) {
        try {
            // ✅ RSA 암호화 추가 - 비밀번호 암호화
            ConnectedIdRequestDTO encryptedRequestDTO = encryptPasswords(requestDTO);

            String accessToken = getAccessToken();

            URL url = new URL(CREATE_ACCOUNT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            // 🚨 올바른 JSON 직접 생성
            ConnectedIdRequestDTO.AccountInfo account = requestDTO.getAccountList().get(0);
            String encryptedPassword = codefRSAUtil.encryptPassword(account.getPassword());

            String jsonBody = String.format(
                    "{\"accountList\":[{\"countryCode\":\"%s\",\"businessType\":\"%s\",\"organization\":\"%s\",\"clientType\":\"%s\",\"loginType\":\"%s\",\"id\":\"%s\",\"password\":\"%s\"}]}",
                    account.getCountryCode(), account.getBusinessType(), account.getOrganization(),
                    account.getClientType(), account.getLoginType(), account.getId(), encryptedPassword
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            log.info("CODEF 응답 코드: {}", responseCode);

            StringBuilder response = new StringBuilder();

            // 성공 응답과 오류 응답을 구분해서 처리
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String responseBody = response.toString();
            log.info("CODEF 원본 응답: {}", responseBody);

            // ✅ URL 디코딩 처리 추가
            String decodedResponse = responseBody;
            try {
                if (responseBody.startsWith("%")) {
                    decodedResponse = URLDecoder.decode(responseBody, StandardCharsets.UTF_8.name());
                    log.info("URL 디코딩된 응답: {}", decodedResponse);
                }
            } catch (Exception e) {
                log.warn("URL 디코딩 실패, 원본 사용: {}", e.getMessage());
            }

            ConnectedIdResponseDTO responseDTO = codefObjectMapper.readValue(decodedResponse, ConnectedIdResponseDTO.class);

            // ✅ 결과 코드 확인 추가
            if (responseDTO.getResult() != null && responseDTO.getResult().getCode() != null) {
                String resultCode = responseDTO.getResult().getCode();
                String message = responseDTO.getResult().getMessage();

                if (!"CF-00000".equals(resultCode)) {
                    log.error("CODEF API 오류 - 코드: {}, 메시지: {}", resultCode, message);
                    throw new RuntimeException("CODEF API 오류: " + message + " (코드: " + resultCode + ")");
                }
            }

            // 🚨 DTO 파싱 대신 정규식으로 connectedId 직접 추출
            String connectedId = null;
            try {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"connectedId\":\"([^\"]+)\"");
                java.util.regex.Matcher matcher = pattern.matcher(decodedResponse);
                if (matcher.find()) {
                    connectedId = matcher.group(1);
                    log.info("정규식으로 connectedId 추출 성공: {}", connectedId);
                } else {
                    log.error("정규식으로 connectedId를 찾을 수 없습니다. JSON: {}", decodedResponse);
                }
            } catch (Exception e) {
                log.error("connectedId 추출 중 오류 발생", e);
            }

            if (connectedId == null || connectedId.isEmpty()) {
                throw new RuntimeException("Connected ID를 받지 못했습니다. JSON: " + decodedResponse);
            }

            log.info("Connected ID 생성 성공: {}", connectedId);
            return connectedId;

        } catch (Exception e) {
            log.error("Connected ID 생성 실패", e);
            throw new RuntimeException("Connected ID 생성 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public AccountListResponseDTO getAccountList(String connectedId, String organizationCode) {
        try {
            String accessToken = getAccessToken();

            URL url = new URL(ACCOUNT_LIST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("connectedId", connectedId);
            requestBody.put("organization", organizationCode);

            String jsonBody = codefObjectMapper.writeValueAsString(requestBody);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String responseBody = response.toString();
            log.info("CODEF 계좌 목록 원본 응답: {}", responseBody);

            // 🚨 URL 디코딩 처리 추가 (createConnectedId와 동일한 로직)
            String decodedResponse = responseBody;
            try {
                if (responseBody.startsWith("%")) {
                    decodedResponse = URLDecoder.decode(responseBody, StandardCharsets.UTF_8.name());
                    log.info("URL 디코딩된 계좌 목록 응답: {}", decodedResponse);
                }
            } catch (Exception e) {
                log.warn("URL 디코딩 실패, 원본 사용: {}", e.getMessage());
            }

            // 🚨 디코딩된 응답으로 JSON 파싱
            AccountListResponseDTO responseDTO = codefObjectMapper.readValue(decodedResponse, AccountListResponseDTO.class);
            // 🚨 null 체크 강화
            int depositCount = 0;
            int loanCount = 0;

            if (responseDTO.getData() != null) {
                if (responseDTO.getData().getResDepositTrust() != null) {
                    depositCount = responseDTO.getData().getResDepositTrust().size();
                }
                if (responseDTO.getData().getResLoan() != null) {
                    loanCount = responseDTO.getData().getResLoan().size();
                }
            }

            log.info("계좌 목록 조회 성공: 예금계좌 {}건, 대출계좌 {}건", depositCount, loanCount);
            log.info("응답 데이터 확인 - data: {}, resDepositTrust: {}, resLoan: {}",
                    responseDTO.getData() != null,
                    responseDTO.getData() != null ? responseDTO.getData().getResDepositTrust() != null : false,
                    responseDTO.getData() != null ? responseDTO.getData().getResLoan() != null : false);

            return responseDTO;

        } catch (Exception e) {
            log.error("계좌 목록 조회 실패", e);
            throw new RuntimeException("계좌 목록 조회 실패", e);
        }
    }

    @Override
    public TransactionListResponseDTO getTransactionListWithDates(String connectedId, String organizationCode, String accountNumber, String startDate, String endDate) {
        try {
            String accessToken = getAccessToken();

            URL url = new URL(TRANSACTION_LIST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("connectedId", connectedId);
            requestBody.put("organization", organizationCode);
            requestBody.put("account", accountNumber);
            requestBody.put("startDate", startDate);
            requestBody.put("endDate", endDate);
            requestBody.put("orderBy", "0");

            log.info("거래내역 조회 파라미터: startDate={}, endDate={}", startDate, endDate);

            String jsonBody = codefObjectMapper.writeValueAsString(requestBody);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            log.info("CODEF 거래내역 응답 코드: {}", responseCode);

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String responseBody = response.toString();
            log.info("CODEF 거래내역 원본 응답: {}", responseBody);

            // URL 디코딩 처리
            String decodedResponse = responseBody;
            try {
                if (responseBody.startsWith("%")) {
                    decodedResponse = URLDecoder.decode(responseBody, StandardCharsets.UTF_8.name());
                    log.info("URL 디코딩된 거래내역 응답: {}", decodedResponse);
                }
            } catch (Exception e) {
                log.warn("URL 디코딩 실패, 원본 사용: {}", e.getMessage());
            }

            TransactionListResponseDTO responseDTO = codefObjectMapper.readValue(decodedResponse, TransactionListResponseDTO.class);

            // 결과 코드 확인
            if (responseDTO.getResult() != null && responseDTO.getResult().getCode() != null) {
                String resultCode = responseDTO.getResult().getCode();
                String message = responseDTO.getResult().getMessage();

                if (!"CF-00000".equals(resultCode)) {
                    log.error("CODEF API 오류 - 코드: {}, 메시지: {}", resultCode, message);
                    throw new RuntimeException("CODEF API 오류: " + message + " (코드: " + resultCode + ")");
                }
            }

            log.info("거래내역 조회 성공: {}건",
                    responseDTO.getData() != null && responseDTO.getData().getResTrHistoryList() != null ?
                            responseDTO.getData().getResTrHistoryList().size() : 0);
            return responseDTO;

        } catch (Exception e) {
            log.error("거래내역 조회 실패", e);
            throw new RuntimeException("거래내역 조회 실패", e);
        }
    }

    @Override
    public LoanTransactionResponseDTO getLoanTransactionList(String connectedId, String organizationCode, String accountNumber, String startDate, String endDate) {
        try {
            String accessToken = getAccessToken();

            URL url = new URL(LOAN_TRANSACTION_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("connectedId", connectedId);
            requestBody.put("organization", organizationCode);
            requestBody.put("account", accountNumber);
            requestBody.put("startDate", startDate);
            requestBody.put("endDate", endDate);
            requestBody.put("orderBy", "0");

            String jsonBody = codefObjectMapper.writeValueAsString(requestBody);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            // ✅ 응답 코드 확인 추가
            int responseCode = conn.getResponseCode();
            log.info("CODEF 대출 거래내역 응답 코드: {}", responseCode);

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String responseBody = response.toString();
            log.info("CODEF 대출 거래내역 원본 응답: {}", responseBody);

            // ✅ URL 디코딩 처리 추가 (다른 메서드와 동일한 로직)
            String decodedResponse = responseBody;
            try {
                if (responseBody.startsWith("%")) {
                    decodedResponse = URLDecoder.decode(responseBody, StandardCharsets.UTF_8.name());
                    log.info("URL 디코딩된 대출 거래내역 응답: {}", decodedResponse);
                }
            } catch (Exception e) {
                log.warn("URL 디코딩 실패, 원본 사용: {}", e.getMessage());
            }

            // ✅ 디코딩된 응답으로 JSON 파싱
            LoanTransactionResponseDTO responseDTO = codefObjectMapper.readValue(decodedResponse, LoanTransactionResponseDTO.class);

            // ✅ 결과 코드 확인 추가
            if (responseDTO.getResult() != null && responseDTO.getResult().getCode() != null) {
                String resultCode = responseDTO.getResult().getCode();
                String message = responseDTO.getResult().getMessage();

                if (!"CF-00000".equals(resultCode)) {
                    log.error("CODEF API 오류 - 코드: {}, 메시지: {}", resultCode, message);
                    throw new RuntimeException("CODEF API 오류: " + message + " (코드: " + resultCode + ")");
                }
            }

            log.info("대출 거래내역 조회 성공: {}건",
                    responseDTO.getData() != null && responseDTO.getData().getResTrHistoryList() != null ?
                            responseDTO.getData().getResTrHistoryList().size() : 0);
            return responseDTO;

        } catch (Exception e) {
            log.error("대출 거래내역 조회 실패", e);
            throw new RuntimeException("대출 거래내역 조회 실패", e);
        }
    }

    @Override
    @Transactional
    public void syncUserAccountData(Long userId) {
        try {
            User user = userMapper.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            }

            String connectedId = user.getConnectedId();
            if (connectedId == null || connectedId.isEmpty()) {
                log.warn("사용자(ID: {})의 Connected ID가 없습니다.", userId);
                return;
            }

            // 금융기관별로 계좌 정보 조회 (하나은행 예시)
            List<String> organizationCodes = Arrays.asList("0081"); // 실제로는 사용자가 연결한 금융기관들

            for (String orgCode : organizationCodes) {
                syncAccountDataForOrganization(userId, connectedId, orgCode);
            }

        } catch (Exception e) {
            log.error("사용자 계좌 데이터 동기화 실패: userId={}", userId, e);
            throw new RuntimeException("계좌 데이터 동기화 실패", e);
        }
    }

    private void syncAccountDataForOrganization(Long userId, String connectedId, String organizationCode) {
        try {
            AccountListResponseDTO accountList = getAccountList(connectedId, organizationCode);

            if (accountList.getData() == null) {
                log.warn("계좌 정보가 없습니다. userId={}, orgCode={}", userId, organizationCode);
                return;
            }

            // 예금계좌 동기화
            if (accountList.getData().getResDepositTrust() != null) {
                syncDepositAccounts(userId, organizationCode, accountList.getData().getResDepositTrust(), connectedId);
            }

            // 대출계좌 동기화
            if (accountList.getData().getResLoan() != null) {
                syncLoanAccounts(userId, organizationCode, accountList.getData().getResLoan(), connectedId);
            }

        } catch (Exception e) {
            log.error("기관별 계좌 데이터 동기화 실패: userId={}, orgCode={}", userId, organizationCode, e);
        }
    }

    private void syncDepositAccounts(Long userId, String organizationCode, List<AccountListResponseDTO.DepositAccount> depositAccounts, String connectedId) {
        for (AccountListResponseDTO.DepositAccount account : depositAccounts) {
            try {
                // 이미 존재하는 예금계좌인지 확인
                if (depositAccountMapper.isDepositAccountExists(userId, account.getResAccount())) {
                    log.info("이미 존재하는 예금계좌: {}", account.getResAccount());
                    // 잔액만 업데이트
                    DepositAccount existingAccount = depositAccountMapper.findByUserIdAndAccountNumber(userId, account.getResAccount());
                    if (existingAccount != null) {
                        BigDecimal newBalance = new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0");
                        depositAccountMapper.updateBalance(existingAccount.getId(), newBalance);
                    }
                    continue;
                }

                // 새로운 예금계좌 정보 저장
                DepositAccount depositAccount = DepositAccount.builder()
                        .userId(userId)
                        .organizationCode(organizationCode)
                        .accountNumber(account.getResAccount())
                        .currentBalance(new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0"))
                        .build();

                depositAccountMapper.insertDepositAccount(depositAccount);
                log.info("새로운 예금계좌 저장: {}", account.getResAccount());

                // 거래내역 조회 및 저장
                syncTransactionHistory(userId, connectedId, organizationCode, account.getResAccount());

            } catch (Exception e) {
                log.error("예금계좌 동기화 실패: account={}", account.getResAccount(), e);
            }
        }
    }

    private void syncLoanAccounts(Long userId, String organizationCode, List<AccountListResponseDTO.LoanAccount> loanAccounts, String connectedId) {
        for (AccountListResponseDTO.LoanAccount account : loanAccounts) {
            try {
                // 금융기관 코드 조회 또는 등록
                Long orgCodeLong = getOrCreateFinancialInstitution(organizationCode);

                // 이미 존재하는 대출계좌인지 확인
                if (debtMapper.isDebtAccountExists(userId, account.getResAccount())) {
                    log.info("이미 존재하는 대출계좌: {}", account.getResAccount());
                    continue;
                }

                // 대출계좌 정보를 debt_accounts 테이블에 저장
                DebtAccount debtAccount = convertToDebtAccount(userId, orgCodeLong, account);
                debtMapper.insertDebt(debtAccount);
                log.info("새로운 대출계좌 저장: {}", account.getResAccount());

                // 대출 거래내역 조회 및 저장
                syncLoanTransactionHistory(userId, connectedId, organizationCode, account.getResAccount());

            } catch (Exception e) {
                log.error("대출계좌 동기화 실패: account={}", account.getResAccount(), e);
            }
        }
    }

    private void syncTransactionHistory(Long userId, String connectedId, String organizationCode, String accountNumber) {
        try {
            // ✅ 기본 조회 기간 설정 (최근 6개월)
            String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String startDate = LocalDate.now().minusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            log.info("거래내역 동기화 시작: account={}, 조회기간={}~{}", accountNumber, startDate, endDate);

            // ✅ 날짜 범위를 포함한 거래내역 조회
            TransactionListResponseDTO transactionList = getTransactionListWithDates(
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            if (transactionList.getData() == null || transactionList.getData().getResTrHistoryList() == null) {
                log.warn("거래내역이 없습니다: account={}", accountNumber);
                return;
            }

            // 예금계좌 정보 조회
            DepositAccount depositAccount = depositAccountMapper.findByUserIdAndAccountNumber(userId, accountNumber);
            if (depositAccount == null) {
                log.warn("예금계좌 정보를 찾을 수 없습니다: {}", accountNumber);
                return;
            }

            int savedCount = 0;
            int duplicateCount = 0;
            int totalCount = transactionList.getData().getResTrHistoryList().size();

            for (TransactionListResponseDTO.TransactionDetail transaction : transactionList.getData().getResTrHistoryList()) {
                try {
                    // 거래 날짜와 시간 파싱
                    LocalDateTime transactionTime = parseTransactionDateTime(
                            transaction.getResAccountTrDate(),
                            transaction.getResAccountTrTime()
                    );

                    // 거래 금액 및 타입 결정
                    BigDecimal amount = BigDecimal.ZERO;
                    String type = "";

                    if (transaction.getResAccountIn() != null && !transaction.getResAccountIn().equals("0")) {
                        // 입금
                        amount = new BigDecimal(transaction.getResAccountIn());
                        type = "TRANSFER";
                    } else if (transaction.getResAccountOut() != null && !transaction.getResAccountOut().equals("0")) {
                        // 출금
                        amount = new BigDecimal(transaction.getResAccountOut());
                        type = "WITHDRAW";
                    } else {
                        continue; // 금액이 0인 거래는 건너뛰기
                    }

                    // ✅ 개선된 중복 거래 체크
                    if (depositMapper.isTransactionExists(userId, transactionTime, amount, type)) {
                        duplicateCount++;
                        continue;
                    }

                    // 거래내역 저장
                    Transaction transactionEntity = Transaction.builder()
                            .depositAccountId(depositAccount.getId())
                            .userId(userId)
                            .transactionTime(transactionTime)
                            .afterBalance(new BigDecimal(transaction.getResAfterTranBalance() != null ? transaction.getResAfterTranBalance() : "0"))
                            .amount(amount)
                            .type(type)
                            .resAccountDesc1(transaction.getResAccountDesc1())
                            .resAccountDesc2(transaction.getResAccountDesc2())
                            .resAccountDesc3(transaction.getResAccountDesc3())
                            .build();

                    // ✅ 개선된 거래내역 저장
                    depositMapper.insertTransaction(transactionEntity);
                    savedCount++;

                } catch (Exception e) {
                    log.error("개별 거래내역 저장 실패: 날짜={}, 금액={}",
                            transaction.getResAccountTrDate(),
                            transaction.getResAccountIn() != null ? transaction.getResAccountIn() : transaction.getResAccountOut(), e);
                }
            }

            log.info("거래내역 동기화 완료: account={} -> 총 {}건 중 {}건 저장, {}건 중복 스킵",
                    accountNumber, totalCount, savedCount, duplicateCount);

        } catch (Exception e) {
            log.error("거래내역 동기화 실패: account={}", accountNumber, e);
        }
    }

    private void syncLoanTransactionHistory(Long userId, String connectedId, String organizationCode, String accountNumber) {
        try {
            // ✅ 조회 기간 설정 (최근 1년)
            String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String startDate = LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            log.info("대출 거래내역 동기화 시작: account={}, 조회기간={}~{}", accountNumber, startDate, endDate);

            LoanTransactionResponseDTO loanTransactionList = getLoanTransactionList(
                    connectedId, organizationCode, accountNumber, startDate, endDate);

            if (loanTransactionList.getData() == null || loanTransactionList.getData().getResTrHistoryList() == null) {
                log.warn("대출 거래내역이 없습니다: account={}", accountNumber);
                return;
            }

            // ✅ 대출계좌 정보 조회
            DebtAccount debtAccount = debtMapper.findByUserIdAndAccount(userId, accountNumber);
            if (debtAccount == null) {
                log.warn("대출계좌 정보를 찾을 수 없습니다: {}", accountNumber);
                return;
            }

            int savedCount = 0;
            int duplicateCount = 0;
            int totalCount = loanTransactionList.getData().getResTrHistoryList().size();

            for (LoanTransactionResponseDTO.LoanTransactionDetail transaction : loanTransactionList.getData().getResTrHistoryList()) {
                try {
                    // 거래 날짜 파싱
                    LocalDate transactionDate = parseDate(transaction.getTransactionDate());

                    // ✅ 중복 체크 (DebtTransactionMapper 필요)
                    if (debtTransactionMapper.isDebtTransactionExists(debtAccount.getId(), transactionDate)) {
                        duplicateCount++;
                        continue;
                    }

                    // ✅ 대출 거래내역 저장
                    DebtTransaction debtTransaction = DebtTransaction.builder()
                            .debtAccountId(debtAccount.getId())
                            .transactionDate(transactionDate)
                            .principalAmount(new BigDecimal(transaction.getPrincipalAmount() != null ? transaction.getPrincipalAmount() : "0"))
                            .interestAmount(new BigDecimal(transaction.getInterestAmount() != null ? transaction.getInterestAmount() : "0"))
                            .remainingBalance(new BigDecimal(transaction.getRemainingBalance() != null ? transaction.getRemainingBalance() : "0"))
                            .createdAt(LocalDateTime.now())
                            .build();

                    debtTransactionMapper.insertDebtTransaction(debtTransaction);
                    savedCount++;

                } catch (Exception e) {
                    log.error("개별 대출 거래내역 저장 실패: 날짜={}", transaction.getTransactionDate(), e);
                }
            }

            log.info("대출 거래내역 동기화 완료: account={} -> 총 {}건 중 {}건 저장, {}건 중복 스킵",
                    accountNumber, totalCount, savedCount, duplicateCount);

        } catch (Exception e) {
            log.error("대출 거래내역 동기화 실패: account={}", accountNumber, e);
        }
    }

    private Long getOrCreateFinancialInstitution(String organizationCode) {
        Long orgCodeLong = financialInstitutionMapper.findCodeByName(organizationCode);
        if (orgCodeLong == null) {
            // organizationCode를 이름으로 사용 (실제로는 매핑 테이블 필요)
            String organizationName = getOrganizationName(organizationCode);
            financialInstitutionMapper.insertInstitution(organizationName);
            orgCodeLong = financialInstitutionMapper.findCodeByName(organizationName);
        }
        return orgCodeLong;
    }

    private String getOrganizationName(String organizationCode) {
        // 금융기관 코드를 이름으로 매핑
        Map<String, String> codeToNameMap = new HashMap<>();
        codeToNameMap.put("0081", "하나은행");
        codeToNameMap.put("0020", "우리은행");
        codeToNameMap.put("0088", "신한은행");
        codeToNameMap.put("0004", "KB국민은행");
        // 추가 금융기관들...

        return codeToNameMap.getOrDefault(organizationCode, "기타금융기관");
    }

    private DebtAccount convertToDebtAccount(Long userId, Long organizationCode, AccountListResponseDTO.LoanAccount account) {
        return DebtAccount.builder()
                .userId(userId)
                .organizationCode(organizationCode)
                .resAccount(account.getResAccount())
                .debtName(account.getResAccountName() != null ? account.getResAccountName() : "대출")
                .currentBalance(new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0"))
                .originalAmount(new BigDecimal(account.getResAccountBalance() != null ? account.getResAccountBalance() : "0"))
                .interestRate(new BigDecimal("0.00")) // CODEF에서 이자율 정보를 별도로 조회해야 함
                .loanStartDate(parseDate(account.getResAccountStartDate()))
                .loanEndDate(parseDate(account.getResAccountEndDate()))
                .nextPaymentDate(LocalDate.now().plusMonths(1)) // 기본값
                .gracePeriodMonths(0L)
                .repaymentMethod("원리금균등상환") // 기본값
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateString);
            return LocalDate.now();
        }
    }

    private LocalDateTime parseTransactionDateTime(String dateString, String timeString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));

            if (timeString != null && timeString.length() >= 6) {
                int hour = Integer.parseInt(timeString.substring(0, 2));
                int minute = Integer.parseInt(timeString.substring(2, 4));
                int second = Integer.parseInt(timeString.substring(4, 6));
                return date.atTime(hour, minute, second);
            } else {
                return date.atStartOfDay();
            }
        } catch (Exception e) {
            log.warn("거래 날짜/시간 파싱 실패: date={}, time={}", dateString, timeString);
            return LocalDateTime.now();
        }
    }

    @Override
    public String getConnectedIdByUserId(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return user.getConnectedId();
    }

    /**
     * 사용자 Connected ID 업데이트
     */
    public void updateUserConnectedId(Long userId, String connectedId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // UserMapper에 updateConnectedId 메서드 추가 필요
        // userMapper.updateConnectedId(userId, connectedId);
        log.info("사용자 Connected ID 업데이트: userId={}, connectedId={}", userId, connectedId);
    }

    /**
     * 계좌 연동 상태 체크
     */
    public boolean isAccountLinked(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        return user.getConnectedId() != null && !user.getConnectedId().isEmpty();
    }

    /**
     * 전체 사용자 계좌 데이터 배치 동기화
     */
    @Transactional
    public void batchSyncAllUsers() {
        try {
            // 모든 Connected ID가 있는 사용자 조회 (실제로는 UserMapper에 메서드 추가 필요)
            List<Long> userIds = Arrays.asList(1L, 2L); // 임시 구현

            for (Long userId : userIds) {
                try {
                    syncUserAccountData(userId);
                    log.info("사용자 {}의 계좌 데이터 동기화 완료", userId);
                } catch (Exception e) {
                    log.error("사용자 {}의 계좌 데이터 동기화 실패", userId, e);
                    // 개별 사용자 실패해도 계속 진행
                }
            }

        } catch (Exception e) {
            log.error("전체 사용자 계좌 데이터 배치 동기화 실패", e);
            throw new RuntimeException("배치 동기화 실패", e);
        }
    }
}
