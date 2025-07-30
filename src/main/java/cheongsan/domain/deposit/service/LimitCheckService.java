package cheongsan.domain.deposit.service;

public interface LimitCheckService {

    void checkDailyLimitExceeded(Long userId);
}
