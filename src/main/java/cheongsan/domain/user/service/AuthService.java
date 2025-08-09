package cheongsan.domain.user.service;

import cheongsan.domain.auth.dto.SocialUserInfo;
import cheongsan.domain.user.dto.*;

public interface AuthService {
    boolean checkDuplicate(String userId);

    SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO);

    FindUserIdResponseDTO findUserIdByEmail(FindUserIdRequestDTO findUserIdRequestDTO);

    FindUserPasswordResponseDTO findUserPassword(FindUserPasswordRequestDTO findUserPasswordRequestDTO);

    LogInResponseDTO login(LogInRequestDTO logInRequestDTO);


    TokenRefreshResponseDTO reissueTokens(String refreshToken);

    // 네이버 로그인 관련 메서드
    LogInResponseDTO naverSignUpOrLogin(SocialUserInfo socialUserInfo);

    boolean isNaverUserExists(String naverId);
}
