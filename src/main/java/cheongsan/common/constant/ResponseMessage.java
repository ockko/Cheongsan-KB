package cheongsan.common.constant;

import lombok.Getter;

@Getter
public enum ResponseMessage {

    // --- 성공 ---
    BUDGET_LIMIT_SAVED("일일 소비 한도가 성공적으로 저장되었습니다."),

    // --- 에러 ---
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    CREDENTIALS_MISSING("username 또는 password가 없습니다."),
    INVALID_CREDENTIALS("사용자 ID 또는 비밀번호가 일치하지 않습니다."),
    EXPIRED_JWT_TOKEN("토큰의 유효시간이 지났습니다."),
    USER_NOT_FOUND("일치하는 회원 정보가 없습니다."),
    UNSUPPORTED_REPAYMENT_METHOD("지원하지 않는 상환방식입니다."),
    BUDGET_LIMIT_EXCEEDED("설정된 한도는 시스템 추천 한도를 초과할 수 없습니다."),
    BUDGET_UPDATE_RULE_VIOLATED("소비 한도는 주 1회만 수정할 수 있습니다."),
    INCOME_NOT_FOUND_FOR_BUDGET_RECOMMENDATION("추천 한도를 계산하기 위해 월 소득 정보가 필요합니다."),
    FAILED_TO_SAVE_REPORT("리포트 저장 중 오류가 발생했습니다."),
    WEEKLY_REPORT_NOT_FOUND("저장된 주간 리포트가 없습니다."),
    WEEKLY_REPORT_NOT_FOUND_FOR_DATE("해당 주의 리포트가 존재하지 않습니다."),
    FAILED_TO_CONVERT_REPORT_DATA("리포트 데이터 변환이 실패했습니다.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
