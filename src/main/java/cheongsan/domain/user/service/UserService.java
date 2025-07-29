package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void submitDiagnosisAnswerToUser(Long userId, Long workoutId);

    UserDTO getUser(Long id);

    MyInfoResponseDTO getMyInfo(String userId);

    UpdateMyProfileResponseDTO updateMyProfile(String userId, UpdateMyProfileRequestDTO updateMyProfileRequestDTO);

    void deleteAccount(String userId, DeleteAccountRequestDTO dto);
}
