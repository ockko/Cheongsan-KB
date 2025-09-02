package cheongsan.domain.user.service;

import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.security.util.JwtProcessor;
import cheongsan.domain.auth.dto.SocialUserInfo;
import cheongsan.domain.codef.dto.ConnectedIdRequestDTO;
import cheongsan.domain.codef.service.CodefService;
import cheongsan.domain.codef.service.CodefSyncService;
import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final CodefService codefService;
    private final CodefSyncService codefSyncService;
    private final JwtProcessor jwtProcessor;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 14;

    @Autowired
    public AuthServiceImpl(
            UserMapper userMapper,
            JavaMailSender mailSender,
            CodefService codefService, CodefSyncService codefSyncService,
            JwtProcessor jwtProcessor,
            PasswordEncoder passwordEncoder,
            @Qualifier("redisTemplateForToken") RedisTemplate<String, Object> redisTemplate
    ) {
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.codefService = codefService;
        this.codefSyncService = codefSyncService;
        this.jwtProcessor = jwtProcessor;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean checkDuplicate(String userId) {
        User user = userMapper.findByUserId(userId);
        return user != null;
    }

    @Override
    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {

        if (userMapper.findByUserId(signUpRequestDTO.getUserId()) != null) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        if (userMapper.findByEmail(signUpRequestDTO.getEmail()) != null) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        User user = User.builder()
                .userId(signUpRequestDTO.getUserId())
                .password(passwordEncoder.encode(signUpRequestDTO.getPassword()))
                .email(signUpRequestDTO.getEmail())
                .connectedId(genereateConnectId(signUpRequestDTO.getUserId(), signUpRequestDTO.getPassword()))
                .accountType("GENERAL")  //일반 회원가입
                .build();

        log.info("signUp request: {}", user.getUserId());
        userMapper.save(user);

        // 회원가입 완료 후 자동으로 CODEF 동기화 실행
        try {
            log.info("🚀 회원가입 완료 후 자동 CODEF 동기화 시작: userId={}", user.getId());
            codefSyncService.syncUserAccountData(user.getId());
            log.info("✅ 회원가입 완료 후 자동 CODEF 동기화 성공: userId={}", user.getId());
        } catch (Exception e) {
            // 동기화 실패해도 회원가입은 성공으로 처리
            log.warn("⚠️ 회원가입 완료 후 CODEF 동기화 실패: userId={}, error={}", user.getId(), e.getMessage());
        }

        return new SignUpResponseDTO(user.getId(), user.getUserId());
    }

    public String genereateConnectId(String userId, String password) {
        // connectedId 랜덤 생성.
        ConnectedIdRequestDTO.AccountInfo accountInfo = ConnectedIdRequestDTO.AccountInfo.builder()
                .countryCode("KR")
                .businessType("BK")
                .organization("0081") // 하나은행 (필요시 파라미터로 받을 수 있음)
                .clientType("P")
                .loginType("1")
                .id(userId)        // DB의 user_id 사용
                .password(password) // DB의 password 사용 (평문)
                .build();

        ConnectedIdRequestDTO requestDTO = ConnectedIdRequestDTO.builder()
                .accountList(Arrays.asList(accountInfo))
                .build();

        log.info("DB에서 조회한 계정 정보로 Connected ID 생성: user_id={}", userId);

        // Connected ID 생성
        String connectedId = codefService.createConnectedId(requestDTO);
        return connectedId;
    }


    @Override
    public FindUserIdResponseDTO findUserIdByEmail(FindUserIdRequestDTO findUserIdRequestDTO) {
        User user = userMapper.findByEmail(findUserIdRequestDTO.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("등록된 이메일이 없습니다.");
        }
        return new FindUserIdResponseDTO(user.getUserId());
    }

    @Override
    public FindUserPasswordResponseDTO findUserPassword(FindUserPasswordRequestDTO findUserPasswordRequestDTO) {
        User user = userMapper.findByUserIdAndEmail(findUserPasswordRequestDTO.getUserId(), findUserPasswordRequestDTO.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("아이디/이메일이 일치하는 사용자가 없습니다.");
        }
        String tempPw = generateTempPassword();
        log.info("findUserPassword request: {}", tempPw);
        try {
            sendTempPasswordMail(user.getEmail(), tempPw);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String encoded = passwordEncoder.encode(tempPw);
        userMapper.updatePassword(user.getId(), encoded); // 비밀번호 DB에 암호화하여 저장

        // ※ 진짜 서비스에서는 tempPw를 메일/SMS로 안내하고 응답에 직접 노출하지 않습니다!
        return new FindUserPasswordResponseDTO(tempPw);
    }

    private String generateTempPassword() {
        int len = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendTempPasswordMail(String to, String tempPw) {
        log.info("sendTempPasswordMail to: {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[티끌 모아 청산] 임시 비밀번호 발송 안내");
        message.setText("임시 비밀번호: " + tempPw + "\n로그인 후 반드시 새 비밀번호로 변경해 주세요.");
        mailSender.send(message);
        log.info("sendTempPasswordMail to: {}", message);
    }

    @Override
    public LogInResponseDTO login(LogInRequestDTO logInRequestDTO) {
        User user = userMapper.findByUserId(logInRequestDTO.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }
        if (!passwordEncoder.matches(logInRequestDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        String role = user.getRole();
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return new LogInResponseDTO(
                user.getId(),
                user.getNickname(),
                "accessToken",
                "refreshToken",
                role
        );
    }

    @Override
    public TokenRefreshResponseDTO reissueTokens(String refreshToken) {
        if (!jwtProcessor.validateToken(refreshToken)) {
            throw new BadCredentialsException(ResponseMessage.INVALID_REFRESH_TOKEN.getMessage());
        }

        String userId = jwtProcessor.getUserIdFromToken(refreshToken);

        String storedRefreshToken = (String) redisTemplate.opsForValue().get("RT:" + userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BadCredentialsException(ResponseMessage.REFRESH_TOKEN_MISMATCH.getMessage());
        }

        String newAccessToken = jwtProcessor.generateAccessToken(userId);
        String newRefreshToken = jwtProcessor.generateRefreshToken(userId);

        redisTemplate.opsForValue().set(
                "RT:" + userId,
                newRefreshToken,
                REFRESH_TOKEN_VALIDITY_SECONDS,
                TimeUnit.SECONDS
        );

        return new TokenRefreshResponseDTO(newAccessToken, newRefreshToken);
    }

    @Override
    public LogInResponseDTO naverSignUpOrLogin(SocialUserInfo socialUserInfo) {
        log.info("네이버 로그인/회원가입 처리 시작: naverId={}, email={}",
                socialUserInfo.getProviderId(), socialUserInfo.getEmail());

        // 🔍 디버깅: 네이버 ID로 사용자 조회 시도
        log.info("DB에서 naverId로 사용자 조회 시도: {}", socialUserInfo.getProviderId());
        User existingNaverUser = userMapper.findByNaverId(socialUserInfo.getProviderId());

        if (existingNaverUser != null) {
            log.info("✅ 기존 네이버 사용자 발견: userId={}, email={}",
                    existingNaverUser.getUserId(), existingNaverUser.getEmail());
        } else {
            log.warn("❌ naverId로 사용자를 찾을 수 없음: {}", socialUserInfo.getProviderId());
        }

        // 🔍 디버깅: 이메일로도 조회해보기
        log.info("DB에서 이메일로 사용자 조회 시도: {}", socialUserInfo.getEmail());
        User existingEmailUser = userMapper.findByEmail(socialUserInfo.getEmail());

        if (existingEmailUser != null) {
            log.info("✅ 이메일로 사용자 발견: userId={}, naverId={}, accountType={}",
                    existingEmailUser.getUserId(), existingEmailUser.getNaverId(), existingEmailUser.getAccountType());
        } else {
            log.warn("❌ 이메일로도 사용자를 찾을 수 없음: {}", socialUserInfo.getEmail());
        }

        User user;
        if (existingNaverUser != null) {
            // 기존 사용자 로그인
            log.info("기존 네이버 사용자 로그인: userId={}", existingNaverUser.getUserId());
            user = existingNaverUser;
        } else if (existingEmailUser != null && "NAVER".equals(existingEmailUser.getAccountType())) {
            // 네이버 계정인데 naver_id가 NULL인 경우 (데이터 불일치 복구)
            log.warn("🔧 네이버 계정 데이터 복구 필요: naver_id가 NULL인 계정 발견");
            log.info("복구 대상: userId={}, email={}", existingEmailUser.getUserId(), existingEmailUser.getEmail());

            // naver_id 업데이트 시도
            try {
                userMapper.updateNaverId(existingEmailUser.getId(), socialUserInfo.getProviderId());
                log.info("✅ naver_id 업데이트 완료: userId={}, naverId={}",
                        existingEmailUser.getUserId(), socialUserInfo.getProviderId());

                // 업데이트된 사용자 정보 다시 조회
                user = userMapper.findById(existingEmailUser.getId());
                if (user != null) {
                    log.info("✅ 업데이트된 사용자 조회 성공: naverId={}", user.getNaverId());
                }
            } catch (Exception e) {
                log.error("❌ naver_id 업데이트 실패", e);
                throw new RuntimeException("사용자 데이터 업데이트에 실패했습니다.");
            }

            user = existingEmailUser;
        } else if (existingEmailUser != null) {
            // 일반 계정이 있는 경우
            throw new IllegalArgumentException("해당 이메일로 이미 가입된 일반 계정이 있습니다. 일반 로그인을 이용해주세요.");
        } else {
            // 완전히 새로운 사용자 회원가입
            log.info("신규 네이버 사용자 회원가입 진행: email={}", socialUserInfo.getEmail());
            user = createNaverUser(socialUserInfo);
            userMapper.save(user);
            log.info("네이버 사용자 회원가입 완료: userId={}, naverId={}",
                    user.getUserId(), user.getNaverId());
        }

        // JWT 토큰 생성
        String accessToken = jwtProcessor.generateAccessToken(user.getUserId());
        String refreshToken = jwtProcessor.generateRefreshToken(user.getUserId());

        // Refresh Token을 Redis에 저장
        redisTemplate.opsForValue().set(
                "RT:" + user.getUserId(),
                refreshToken,
                REFRESH_TOKEN_VALIDITY_SECONDS,
                TimeUnit.SECONDS
        );

        String role = user.getRole();
        if (role == null || role.isBlank()) role = "USER";
        if (role.startsWith("ROLE_")) role = role.substring(5);

        log.info("✅ 네이버 로그인 최종 성공: userId={}, role={}", user.getUserId(), role);

        return new LogInResponseDTO(
                user.getId(),
                user.getNickname(),
                accessToken,
                refreshToken,
                role
        );
    }

    @Override
    public boolean isNaverUserExists(String naverId) {
        User user = userMapper.findByNaverId(naverId);
        return user != null;
    }

    private User createNaverUser(SocialUserInfo socialUserInfo) {
        String dummyConnectedId = generateDummyConnectedId();
        String naverUserId = generateNaverUserId(socialUserInfo.getProviderId());

        return User.builder()
                .userId(naverUserId)
                .password(null)  // 네이버 사용자는 패스워드 없음 (네이버에서 인증)
                .email(socialUserInfo.getEmail())
                .nickname(socialUserInfo.getNickname())
                .connectedId(dummyConnectedId)
                .naverId(socialUserInfo.getProviderId())
                .accountType("NAVER")
                .role("USER")
                // ⭐ 누락된 필드들 추가
                .dailyLimit(BigDecimal.ZERO)        // 기본값 0으로 설정
                .dailyLimitDate(null)               // NULL 허용
                .status("기타")                     // 기본 상태값
                .recommendedProgramId(null)         // NULL 허용
                .build();
    }

    /**
     * 더미 ConnectedId 생성 (CODEF 연동 없이)
     */
    private String generateDummyConnectedId() {
        return "DUMMY_OAUTH_" + System.currentTimeMillis() + "_" +
                new Random().nextInt(10000);
    }

    /**
     * 네이버 사용자 ID 생성
     */
    private String generateNaverUserId(String naverId) {
        return "naver_" + naverId;
    }
}
