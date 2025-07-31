package cheongsan.domain.codef.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// ===== 대출 거래내역 조회 관련 =====
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanTransactionResponseDTO {
    private ResultInfo result;
    private LoanData data;
    private String connectedId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanData {
        private String resAccountDisplay;
        private String resAccount;
        private String resLoanKind;
        private String resAccountHolder;
        private String resAccountStartDate;
        private String resAccountEndDate;
        private String resLoanBalance;
        private String resPrincipal;
        private String resRate;
        private String resDatePayment;
        private String resState;
        private String commStartDate;
        private String commEndDate;
        private List<LoanTransactionDetail> resTrHistoryList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanTransactionDetail {
        private String transactionDate;
        private String principalAmount;
        private String interestAmount;
        private String remainingBalance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultInfo {
        private String code;
        private String message;
        private String transactionId;
        private String extraMessage;
    }
}
