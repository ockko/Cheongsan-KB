package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public void submitDiagnosisAnswerToUser(Long userId, Long workoutId);

    public UserDTO getUser(Long userId);
}
