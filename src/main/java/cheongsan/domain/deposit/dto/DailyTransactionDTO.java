package cheongsan.domain.deposit.dto;

import cheongsan.domain.deposit.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyTransactionDTO {
    private BigDecimal amount;  // 거래 금액
    private String type;        // 거래 유형 (TRANSFER/WITHDRAW)
    private String accountNumber;

    // Entity → DTO 변환 (간소화)
    public static DailyTransactionDTO of(Transaction entity) {
        return DailyTransactionDTO.builder()
                .amount(entity.getAmount())
                .type(entity.getType())
                .build();
    }

    // 프론트엔드에서 사용할 수 있도록 형식화된 금액 반환
    public String getFormattedAmount() {
        if (isIncome()) {
            return "+" + amount.toString();
        } else {
            return "-" + amount.toString();
        }
    }

    // 거래 유형 판별 메서드
    public boolean isIncome() {
        return "TRANSFER".equals(type);  // 입금
    }

    public boolean isExpense() {
        return "WITHDRAW".equals(type);  // 출금
    }
//    private Long id;
//    private Long depositAccountId;
//    private LocalDateTime transactionTime;
//    private BigDecimal afterBalance;
//    private BigDecimal amount;
//    private String type;
//    private String resAccountDesc1;
//    private String resAccountDesc2;
//    private String resAccountDesc3;
//    private String accountNumber;
//
//    // Entity → DTO 변환
//    public static DailyTransactionDTO of(TransactionEntity entity) {
//        return DailyTransactionDTO.builder()
//                .id(entity.getId())
//                .depositAccountId(entity.getDepositAccountId())
//                .transactionTime(entity.getTransactionTime())
//                .afterBalance(entity.getAfterBalance())
//                .amount(entity.getAmount())
//                .type(entity.getType())
//                .resAccountDesc1(entity.getResAccountDesc1())
//                .resAccountDesc2(entity.getResAccountDesc2())
//                .resAccountDesc3(entity.getResAccountDesc3())
//                .build();
//    }
//
//    // 거래 유형에 따른 표시용 메서드
//    public boolean isIncome() {
//        return "TRANSFER".equals(type);
//    }
//
//    public boolean isExpense() {
//        return "WITHDRAW".equals(type);
//    }
}
