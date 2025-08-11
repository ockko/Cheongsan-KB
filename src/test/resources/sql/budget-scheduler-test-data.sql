-- 기존 테스트 데이터 정리
DELETE
FROM notifications;
DELETE
FROM transactions;
DELETE
FROM debt_accounts;
DELETE
FROM deposit_accounts;
DELETE
FROM weekly_report;
DELETE
FROM repayment_simulation;
DELETE
FROM user;

-- 테스트용 사용자 데이터 삽입
-- User 1: 한도 자동 조정 대상 (기존 한도 50000 > 새 최대 한도)
INSERT INTO user (id, connected_id, nickname, user_id, email, password, role, daily_limit)
VALUES (26, 12346, 'user1', 'user1', 'user1@test.com', '1234', 'USER', 50000);

-- User 2: 한도 유지 대상 (기존 한도 10000 < 새 최대 한도)
INSERT INTO user (id, connected_id, nickname, user_id, email, password, role, daily_limit)
VALUES (27, 12347, 'user2', 'user2', 'user2@test.com', '1234', 'USER', 10000);


-- 테스트용 부채 데이터 삽입 (User 1)
-- 월 상환액 약 5만원
INSERT INTO debt_accounts (user_id, organization_code, res_account, debt_name, original_amount, current_balance, interest_rate, loan_start_date,
                           loan_end_date, next_payment_date, repayment_method)
VALUES (26, 4, 12198018576143, 'A카드론', 5000000, 5000000, 12.0, '2025-01-01', '2027-01-01', '2020-08-24', 'BULLET_REPAYMENT');


-- 테스트용 거래내역 데이터 삽입 (모두 지난달 기준)
-- User 1의 거래내역 (지난달 소득 100만, 고정지출 10만)
INSERT INTO transactions (deposit_account_id, user_id, after_balance, type, amount, res_account_desc3, transaction_time)
VALUES (1, 26, 1000000, 'INCOME', 1000000, '7월 급여', CURDATE() - INTERVAL 1 MONTH),
       (1, 26, 900000, 'EXPENSE', 100000, '월세', CURDATE() - INTERVAL 1 MONTH);

-- User 2의 거래내역
INSERT INTO transactions (deposit_account_id, user_id, after_balance, type, amount, res_account_desc3, transaction_time)
VALUES (1, 27, 3000000, 'INCOME', 3000000, '7월 월급', CURDATE() - INTERVAL 1 MONTH);