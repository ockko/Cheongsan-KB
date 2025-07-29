package cheongsan.domain.user.service;

import cheongsan.domain.user.dto.*;

public interface AuthService {
    SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO);

    FindUserIdResponseDTO findUserIdByEmail(FindUserIdRequestDTO findUserIdRequestDTO);

    FindUserPasswordResponseDTO findUserPassword(FindUserPasswordRequestDTO findUserPasswordRequestDTO);

    void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

    LogInResponseDTO login(LogInRequestDTO logInRequestDTO);

    NicknameResponseDTO submitNickname(NicknameRequestDTO nicknameRequestDTO);
}
