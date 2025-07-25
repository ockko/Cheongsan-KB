package cheongsan.domain.debt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class DelinquentLoan {
    private String debtName;
    private String organizationName;
    private LocalDate nextPaymentDate;
}
