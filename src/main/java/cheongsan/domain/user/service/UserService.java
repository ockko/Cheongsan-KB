package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    void submitDiagnosisAnswerToUser(Long userId, Long workoutId);

    UserDTO getUser(Long id);

    MyInfoResponseDTO getMyInfo(Long userId);

    UpdateMyProfileResponseDTO updateMyProfile(Long userId, UpdateMyProfileRequestDTO updateMyProfileRequestDTO);

    void changePassword(Long userId, ChangePasswordRequestDTO changePasswordRequestDTO);

    void deleteAccount(Long userId, DeleteAccountRequestDTO dto);

    List<UserDebtAccountResponseDTO> getUserDebtAccounts(Long userId);

    void logout(String userId);
}
