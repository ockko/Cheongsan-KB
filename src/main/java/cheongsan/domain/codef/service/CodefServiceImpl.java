package cheongsan.domain.codef.service;

import cheongsan.common.util.CodefRSAUtil;
import cheongsan.domain.codef.dto.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class CodefServiceImpl implements CodefService {
    private final CodefRSAUtil codefRSAUtil;
    private final ObjectMapper codefObjectMapper;

    @Value("${codef.access-token}")
    private String accessToken;

    private static final String CREATE_ACCOUNT_URL = "https://development.codef.io/v1/account/create";
    private static final String ACCOUNT_LIST_URL = "https://development.codef.io/v1/kr/bank/p/account/account-list";
    private static final String TRANSACTION_LIST_URL = "https://development.codef.io/v1/kr/bank/p/account/transaction-list";
    private static final String LOAN_TRANSACTION_URL = "https://development.codef.io/v1/kr/bank/p/loan/transaction-list";

    public CodefServiceImpl(CodefRSAUtil codefRSAUtil) {
        this.codefRSAUtil = codefRSAUtil;

        // CODEF API 전용 ObjectMapper 설정
        this.codefObjectMapper = new ObjectMapper();
        this.codefObjectMapper.registerModule(new JavaTimeModule());
        this.codefObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.codefObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String createConnectedId(ConnectedIdRequestDTO requestDTO) {
        try {
            URL url = new URL(CREATE_ACCOUNT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            // RSA 암호화된 JSON 직접 생성
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

            // 응답 처리
            int responseCode = conn.getResponseCode();
            log.info("CODEF 응답 코드: {}", responseCode);

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
            log.info("CODEF 원본 응답: {}", responseBody);

            // URL 디코딩 처리
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

            // 결과 코드 확인
            if (responseDTO.getResult() != null && responseDTO.getResult().getCode() != null) {
                String resultCode = responseDTO.getResult().getCode();
                String message = responseDTO.getResult().getMessage();

                if (!"CF-00000".equals(resultCode)) {
                    log.error("CODEF API 오류 - 코드: {}, 메시지: {}", resultCode, message);
                    throw new RuntimeException("CODEF API 오류: " + message + " (코드: " + resultCode + ")");
                }
            }

            // 정규식으로 connectedId 추출
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

            // URL 디코딩 처리
            String decodedResponse = responseBody;
            try {
                if (responseBody.startsWith("%")) {
                    decodedResponse = URLDecoder.decode(responseBody, StandardCharsets.UTF_8.name());
                    log.info("URL 디코딩된 계좌 목록 응답: {}", decodedResponse);
                }
            } catch (Exception e) {
                log.warn("URL 디코딩 실패, 원본 사용: {}", e.getMessage());
            }

            AccountListResponseDTO responseDTO = codefObjectMapper.readValue(decodedResponse, AccountListResponseDTO.class);

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
            return responseDTO;

        } catch (Exception e) {
            log.error("계좌 목록 조회 실패", e);
            throw new RuntimeException("계좌 목록 조회 실패", e);
        }
    }

    @Override
    public TransactionListResponseDTO getTransactionListWithDates(String connectedId, String organizationCode, String accountNumber, String startDate, String endDate) {
        try {
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

            // URL 디코딩 처리
            String decodedResponse = responseBody;
            try {
                if (responseBody.startsWith("%")) {
                    decodedResponse = URLDecoder.decode(responseBody, StandardCharsets.UTF_8.name());
                    log.info("URL 디코딩된 대출 거래내역 응답: {}", decodedResponse);
                }
            } catch (Exception e) {
                log.warn("URL 디코딩 실패, 원본 사용: {}", e.getMessage());
            }

            LoanTransactionResponseDTO responseDTO = codefObjectMapper.readValue(decodedResponse, LoanTransactionResponseDTO.class);

            // 결과 코드 확인
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
}
