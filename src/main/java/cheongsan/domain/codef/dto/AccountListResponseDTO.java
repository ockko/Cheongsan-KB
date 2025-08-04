package cheongsan.domain.codef.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// ===== 계좌 목록 조회 관련 =====
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountListResponseDTO {
    private ResultInfo result;
    private AccountData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountData {
        private String connectedId;
        private List<DepositAccount> resDepositTrust;
        private List<LoanAccount> resLoan;
        private List<Object> resForeignCurrency;
        private List<Object> resFund;
        private List<Object> resInsurance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositAccount {
        private String resAccount;
        private String resAccountDisplay;
        private String resAccountBalance;
        private String resAccountDeposit;
        private String resAccountNickName;
        private String resAccountStartDate;
        private String resAccountEndDate;
        private String resAccountName;
        private String resAccountCurrency;
        private String resAccountLifetime;
        private String resLastTranDate;
        private String resOverdraftAcctYN;
        private String resLoanKind;
        private String resLoanBalance;
        private String resLoanStartDate;
        private String resLoanEndDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanAccount {
        private String resAccount;
        private String resAccountDisplay;
        private String resAccountBalance;
        private String resAccountDeposit;
        private String resAccountNickName;
        private String resAccountStartDate;
        private String resAccountEndDate;
        private String resAccountName;
        private String resAccountCurrency;
        private String resAccountLoanExecNo;
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
