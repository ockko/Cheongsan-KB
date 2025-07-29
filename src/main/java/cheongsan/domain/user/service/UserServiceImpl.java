package cheongsan.domain.user.service;

import cheongsan.domain.user.dto.MyInfoResponseDTO;
import cheongsan.domain.user.dto.UserDTO;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

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

}
