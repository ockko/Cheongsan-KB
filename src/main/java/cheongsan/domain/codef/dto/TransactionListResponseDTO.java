package cheongsan.domain.codef.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// ===== 거래내역 조회 관련 =====
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionListResponseDTO {
    private ResultInfo result;
    private TransactionData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionData {
        private String resAccount;
        private String resAccountDisplay;
        private String resAccountName;
        private String resAccountBalance;
        private String resAvailableBalance;
        private List<TransactionDetail> resTrHistoryList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDetail {
        private String resAccountTrDate;
        private String resAccountTrTime;
        private String resAccountOut;
        private String resAccountIn;
        private String resAccountDesc1;
        private String resAccountDesc2;
        private String resAccountDesc3;
        private String resAccountDesc4;
        private String resAfterTranBalance;
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
