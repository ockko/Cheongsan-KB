package cheongsan.domain.user.service;

import cheongsan.domain.user.dto.*;

public interface AuthService {
    boolean checkDuplicate(String userId);

    SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO);

    FindUserIdResponseDTO findUserIdByEmail(FindUserIdRequestDTO findUserIdRequestDTO);

    FindUserPasswordResponseDTO findUserPassword(FindUserPasswordRequestDTO findUserPasswordRequestDTO);

    LogInResponseDTO login(LogInRequestDTO logInRequestDTO);

    NicknameResponseDTO submitNickname(NicknameRequestDTO nicknameRequestDTO);

    TokenRefreshResponseDTO reissueTokens(String refreshToken);
}
