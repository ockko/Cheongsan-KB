package cheongsan.domain.user.service;

import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void submitDiagnosisAnswerToUser(Long userId, Long workoutId) {
        userMapper.saveDiagnosis(userId, workoutId);
    }

    public UserDTO getUser(Long id) {
        User user = userMapper.findById(id);
        UserDTO userDTO = UserDTO.of(user);
        return userDTO;
    }

    public MyInfoResponseDTO getMyInfo(String userId) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return MyInfoResponseDTO.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    public UpdateMyProfileResponseDTO updateMyProfile(String userId, UpdateMyProfileRequestDTO updateMyProfileRequestDTO) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 닉네임 길이 검사 (여기 ↓)
        if (updateMyProfileRequestDTO.getNickname() != null && updateMyProfileRequestDTO.getNickname().length() > 10) {
            throw new IllegalArgumentException("닉네임은 10자 이내로 입력하세요.");
        }

        userMapper.updateProfile(userId, updateMyProfileRequestDTO.getNickname(), updateMyProfileRequestDTO.getEmail());
        return UpdateMyProfileResponseDTO.builder()
                .message("내 정보가 성공적으로 수정되었습니다.")
                .nickname(updateMyProfileRequestDTO.getNickname())
                .email(updateMyProfileRequestDTO.getEmail())
                .build();
    }

    public void deleteAccount(String userId, DeleteAccountRequestDTO dto) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        // 회원 탈퇴(삭제)
        userMapper.deleteById(user.getUserId());
    }

}
