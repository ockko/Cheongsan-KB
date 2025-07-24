package cheongsan.domain.spending.controller;

import cheongsan.domain.spending.dto.BudgetLimitDTO;
import cheongsan.domain.spending.service.BudgetService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheongsan/dashboard/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/recommendation")
    public ResponseEntity<?> getBudgetLimits(Principal principal) {
        try {
            // Principal 객체에서 사용자 ID를 추출하는 로직 필요
            // CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            // Long userId = userDetails.getUserId();
            Long userId = 1L; // <<-- 실제로는 Principal에서 추출한 사용자 ID 사용

            BudgetLimitDTO resultDto = budgetService.getBudgetLimits(userId);

            return new ResponseEntity<>(resultDto, HttpStatus.OK);

        } catch (IllegalStateException e) {
            ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getMessage());
            return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponseDto errorResponseDto = new ErrorResponseDto("서버 내부 오류가 발생했습니다.");
            return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- 에러 응답을 위한 내부 DTO 클래스 ---

    @Getter
    @AllArgsConstructor
    private static class ErrorResponseDto {
        private final String message;
    }
}
