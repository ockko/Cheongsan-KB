package cheongsan.domain.user.service;

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

    public UserDTO getUser(Long userId) {
        User user = userMapper.findById(userId);
        UserDTO userDTO = new UserDTO(user);
        return userDTO;
    }
}
