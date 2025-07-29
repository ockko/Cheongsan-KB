package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.MyInfoResponseDTO;
import cheongsan.domain.user.dto.UpdateMyProfileRequestDTO;
import cheongsan.domain.user.dto.UpdateMyProfileResponseDTO;
import cheongsan.domain.user.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void submitDiagnosisAnswerToUser(Long userId, Long workoutId);

    UserDTO getUser(Long id);

    MyInfoResponseDTO getMyInfo(String userId);

    UpdateMyProfileResponseDTO updateMyProfile(String userId, UpdateMyProfileRequestDTO updateMyProfileRequestDTO);


}
