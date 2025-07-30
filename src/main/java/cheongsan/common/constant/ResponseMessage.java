package cheongsan.common.constant;

import lombok.Getter;

@Getter
public enum ResponseMessage {

    // --- 성공 ---
    BUDGET_LIMIT_SAVED("일일 소비 한도가 성공적으로 저장되었습니다."),

    // --- 에러 ---
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    USER_NOT_FOUND("일치하는 회원 정보가 없습니다."),
    UNSUPPORTED_REPAYMENT_METHOD("지원하지 않는 상환방식입니다."),
    BUDGET_LIMIT_EXCEEDED("설정된 한도는 시스템 추천 한도를 초과할 수 없습니다."),
    BUDGET_UPDATE_RULE_VIOLATED("소비 한도는 주 1회만 수정할 수 있습니다."),
    INCOME_NOT_FOUND_FOR_BUDGET_RECOMMENDATION("추천 한도를 계산하기 위해 월 소득 정보가 필요합니다.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
