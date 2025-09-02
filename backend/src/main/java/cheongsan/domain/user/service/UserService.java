package cheongsan.domain.user.service;


import cheongsan.domain.user.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    void submitDiagnosisAnswerToUser(Long Id, Long workoutId);

    UserDTO getUser(Long id);

    MyInfoResponseDTO getMyInfo(Long Id);

    void changePassword(Long Id, ChangePasswordRequestDTO changePasswordRequestDTO);

    void deleteAccount(Long Id, DeleteAccountRequestDTO dto);

    List<UserDebtAccountResponseDTO> getUserDebtAccounts(Long Id);

    void logout(String userId);

    void updateName(Long id, UpdateNicknameRequestDTO request);

    void updateEmail(Long id, UpdateEmailRequestDTO request);

    boolean verifyPassword(Long id, String oldPassword);
}
