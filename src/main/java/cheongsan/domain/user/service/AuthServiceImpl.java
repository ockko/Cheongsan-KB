package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


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
        return UUID.randomUUID().toString();
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
        message.setSubject("[티모청] 임시 비밀번호 발송 안내");
        message.setText("임시 비밀번호: " + tempPw + "\n로그인 후 반드시 새 비밀번호로 변경해 주세요.");
        mailSender.send(message);
        log.info("sendTempPasswordMail to: {}", message);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        User user = userMapper.findByUserId(changePasswordRequestDTO.getUserId());
        if (user == null) {
            log.info("존재하지 않는 사용자입니다.");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 2. 기존 비번 일치 확인
        if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            log.info("기존 비밀번호가 올바르지 않습니다.");
            throw new IllegalArgumentException("기존 비밀번호가 올바르지 않습니다.");
        }

        // 3. 새 비밀번호 암호화 후 저장
        String encodedNewPw = passwordEncoder.encode(changePasswordRequestDTO.getNewPassword());
        userMapper.updatePassword(user.getId(), encodedNewPw);
    }

    @Override
    public LogInResponseDTO login(LogInRequestDTO logInRequestDTO) {
        User user = userMapper.findByUserId(logInRequestDTO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }
        if (!passwordEncoder.matches(logInRequestDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        // JWT 사용 시 여기서 토큰을 발급해 응답에 포함하면 됨
        return new LogInResponseDTO("accessToken", "refreshToken");
    }


}
