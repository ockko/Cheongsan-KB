package cheongsan.domain.simulator.service;

import cheongsan.domain.simulator.dto.LoanProductDTO;
import cheongsan.domain.simulator.dto.LoanRecommendationRequestDTO;
import cheongsan.domain.simulator.dto.RecommendationResultDTO;
import cheongsan.domain.simulator.entity.Simulator;
import cheongsan.domain.simulator.mapper.LoanProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanRecommendationServiceImpl implements LoanRecommendationService {

    private static final BigDecimal DSR_LIMIT = new BigDecimal("0.4"); // 40%
    private final LoanProductMapper loanProductMapper;

    @Override
    public RecommendationResultDTO recommendLoans(LoanRecommendationRequestDTO request,
                                                  BigDecimal monthlyRepayment,
                                                  BigDecimal dsr) {
        validateRequest(request);

        List<LoanProductDTO> list = loanProductMapper.findRecommendedLoans(request.getPrincipal())
                .stream()
                .filter(entity -> {
                    BigDecimal loanLimit = parseLoanLimit(entity.getLoanLimit());
                    boolean limitOk = (loanLimit == null) || loanLimit.compareTo(request.getPrincipal()) >= 0;
                    boolean dsrOk = "N".equals(entity.getDsrCheck()) || dsr.compareTo(DSR_LIMIT) <= 0;

                    return limitOk && dsrOk;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new RecommendationResultDTO(dsr, list);
    }

    private LoanProductDTO convertToDTO(Simulator entity) {
        return new LoanProductDTO(
                entity.getProductName(),
                entity.getInstitutionName(),
                entity.getInterestRate(),
                entity.getLoanLimit(),
                entity.getSiteUrl(),
                entity.getDsrCheck()
        );
    }

    private BigDecimal parseLoanLimit(String limitStr) {
        if (limitStr == null || limitStr.trim().isEmpty()) return null;

        try {
            String cleaned = limitStr.trim();

            if (cleaned.matches("\\d+만원")) {
                return new BigDecimal(cleaned.replaceAll("[^0-9]", "")).multiply(new BigDecimal("10000"));
            }

            if (cleaned.contains(",")) {
                String[] parts = cleaned.split("[,~()가-힣]");
                for (String part : parts) {
                    if (part.matches("\\d+")) {
                        BigDecimal value = new BigDecimal(part);
                        return value.compareTo(new BigDecimal("10000")) < 0
                                ? value.multiply(new BigDecimal("10000"))
                                : value;
                    }
                }
            }

            if (cleaned.contains("~")) {
                String[] range = cleaned.split("~");
                if (range.length == 2) {
                    String maxValue = range[1].replaceAll("[^0-9]", "");
                    if (!maxValue.isEmpty()) {
                        BigDecimal value = new BigDecimal(maxValue);
                        return value.compareTo(new BigDecimal("10000")) < 0
                                ? value.multiply(new BigDecimal("10000"))
                                : value;
                    }
                }
            }

            String numbersOnly = cleaned.replaceAll("[^0-9]", "");
            if (!numbersOnly.isEmpty()) {
                BigDecimal value = new BigDecimal(numbersOnly);
                return value.compareTo(new BigDecimal("10000")) < 0
                        ? value.multiply(new BigDecimal("10000"))
                        : value;
            }

        } catch (Exception e) {
            System.out.println("한도 파싱 실패: " + limitStr + " -> " + e.getMessage());
        }

        return null;
    }

    private void validateRequest(LoanRecommendationRequestDTO request) {
        if (request.getPrincipal() == null || request.getPrincipal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("대출 원금은 0보다 커야 합니다.");
        }
        if (request.getTerm() <= 0 || request.getTerm() > 50) {
            throw new IllegalArgumentException("대출 기간은 1년 이상 50년 이하여야 합니다.");
        }
        if (request.getAnnualIncome() == null || request.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("연소득은 0보다 커야 합니다.");
        }
    }
}
