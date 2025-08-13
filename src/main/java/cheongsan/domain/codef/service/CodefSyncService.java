package cheongsan.domain.codef.service;

/**
 * CODEF 동기화 메인 서비스 인터페이스
 */
public interface CodefSyncService {
    /**
     * 사용자 계좌 정보 전체 동기화 - 잔액 변경 감지 방식 (최적화)
     */
    void syncUserAccountData(Long userId);

    /**
     * 전체 사용자 계좌 데이터 배치 동기화
     */
    void batchSyncAllUsers();

    /**
     * 계좌 연동 상태 체크
     */
    boolean isAccountLinked(Long userId);
}
