package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.FindUserIdRequestDTO;
import cheongsan.domain.user.dto.FindUserIdResponseDTO;
import cheongsan.domain.user.dto.SignUpRequestDTO;
import cheongsan.domain.user.dto.SignUpResponseDTO;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {

        if (userMapper.findByUserId(signUpRequestDTO.getUserId()) != null) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        if (userMapper.findByEmail(signUpRequestDTO.getEmail()) != null) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        String encodedPw = passwordEncoder.encode(signUpRequestDTO.getPassword());
        User user = User.builder()
                .userId(signUpRequestDTO.getUserId())
                .password(encodedPw)
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
}
