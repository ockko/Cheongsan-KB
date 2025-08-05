package cheongsan.domain.user.service;


import cheongsan.common.constant.ResponseMessage;
import cheongsan.common.security.util.JwtProcessor;
import cheongsan.domain.codef.dto.ConnectedIdRequestDTO;
import cheongsan.domain.codef.service.CodefService;
import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final CodefService codefService;
    private final JwtProcessor jwtProcessor;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 14;

    @Autowired
    public AuthServiceImpl(
            UserMapper userMapper,
            JavaMailSender mailSender,
            CodefService codefService,
            JwtProcessor jwtProcessor,
            PasswordEncoder passwordEncoder,
            @Qualifier("redisTemplateForToken") RedisTemplate<String, Object> redisTemplate
    ) {
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.codefService = codefService;
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
                .build();
        log.info("signUp request: {}", user);
        userMapper.save(user);

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
        // JWT 사용 시 여기서 토큰을 발급해 응답에 포함하면 됨
        return new LogInResponseDTO(1L, "accessToken", "refreshToken");
    }

    @Override
    public NicknameResponseDTO submitNickname(NicknameRequestDTO nicknameRequestDTO) {
        // 1. 유저 존재 확인
        User user = userMapper.findByUserId(nicknameRequestDTO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 2. 닉네임 삽입
        userMapper.submitNickname(user.getUserId(), nicknameRequestDTO.getNickname());
        return new NicknameResponseDTO("닉네임이 성공적으로 반영되었습니다.", nicknameRequestDTO.getNickname());
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
}
