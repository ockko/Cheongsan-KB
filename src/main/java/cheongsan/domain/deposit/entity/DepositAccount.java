package cheongsan.domain.deposit.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositAccount {
    private Long id;
    private Long userId;
    private String organizationCode;
    private String accountNumber;
    private BigDecimal currentBalance;
}
