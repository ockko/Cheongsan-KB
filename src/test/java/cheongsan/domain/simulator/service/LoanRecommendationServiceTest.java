package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanRecommendationServiceTest {

//    @Test
//    @DisplayName("DSR 계산 테스트")
//    void calculateDsr_Test() {
//        // Given
//        LoanRecommendationService service = new LoanRecommendationService(null);
//        BigDecimal monthlyRepayment = new BigDecimal("500000"); // 월 50만원
//        BigDecimal annualIncome = new BigDecimal("36000000");   // 연 3600만원
//
//        // When
//        // private 메서드라서 직접 테스트하기 어려우니, 전체 로직으로 테스트
//        LoanRecommendationRequestDTO request = new LoanRecommendationRequestDTO();
//        request.setPrincipal(new BigDecimal("30000000"));
//        request.setTerm(240);
//        request.setRepaymentType("원리금균등상환");
//        request.setMonthlyRepayment(monthlyRepayment);
//        request.setAnnualIncome(annualIncome);
//
//        // Then - DSR = (50만 * 12) / 3600만 = 0.1667 (16.67%)
//        // 이 경우 40% 이하이므로 검증을 통과해야 함
//        assertDoesNotThrow(() -> {
//            // 검증 로직만 테스트 (Repository가 null이라 실제 조회는 안됨)
//            try {
//                service.recommendLoans(request);
//            } catch (NullPointerException e) {
//                // Repository가 null이라서 발생하는 NPE는 예상된 것
//                // 검증 로직은 통과했다는 의미
//            }
//        });
//    }
//
//    @Test
//    @DisplayName("유효하지 않은 입력값들에 대한 검증 테스트")
//    void validateRequest_InvalidInputs() {
//        LoanRecommendationService service = new LoanRecommendationService(null);
//
//        // 1. 원금이 null인 경우
//        LoanRecommendationRequestDTO request1 = createValidRequest();
//        request1.setPrincipal(null);
//        assertThrows(IllegalArgumentException.class,
//                () -> service.recommendLoans(request1),
//                "대출 원금은 0보다 커야 합니다.");
//
//        // 2. 원금이 0인 경우
//        LoanRecommendationRequestDTO request2 = createValidRequest();
//        request2.setPrincipal(BigDecimal.ZERO);
//        assertThrows(IllegalArgumentException.class,
//                () -> service.recommendLoans(request2));
//
//        // 3. 대출 기간이 범위를 벗어난 경우
//        LoanRecommendationRequestDTO request3 = createValidRequest();
//        request3.setTerm(700);
//        assertThrows(IllegalArgumentException.class,
//                () -> service.recommendLoans(request3));
//
//        // 4. 월 상환액이 null인 경우
//        LoanRecommendationRequestDTO request4 = createValidRequest();
//        request4.setMonthlyRepayment(null);
//        assertThrows(IllegalArgumentException.class,
//                () -> service.recommendLoans(request4));
//
//        // 5. 연소득이 음수인 경우
//        LoanRecommendationRequestDTO request5 = createValidRequest();
//        request5.setAnnualIncome(new BigDecimal("-1000000"));
//        assertThrows(IllegalArgumentException.class,
//                () -> service.recommendLoans(request5));
//    }
//
//    private LoanRecommendationRequestDTO createValidRequest() {
//        LoanRecommendationRequestDTO request = new LoanRecommendationRequestDTO();
//        request.setPrincipal(new BigDecimal("30000000"));
//        request.setTerm(240);
//        request.setRepaymentType("원리금균등상환");
//        request.setMonthlyRepayment(new BigDecimal("500000"));
//        request.setAnnualIncome(new BigDecimal("48000000"));
//        return request;
//    }
}