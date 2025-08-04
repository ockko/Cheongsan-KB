package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    void submitDiagnosisAnswerToUser(Long userId, Long workoutId);

    UserDTO getUser(Long id);

    MyInfoResponseDTO getMyInfo(String userId);

    UpdateMyProfileResponseDTO updateMyProfile(String userId, UpdateMyProfileRequestDTO updateMyProfileRequestDTO);

    void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

    void deleteAccount(String userId, DeleteAccountRequestDTO dto);

    List<UserDebtAccountResponseDTO> getUserDebtAccounts(Long userId);
}
