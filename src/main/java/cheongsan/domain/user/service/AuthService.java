package cheongsan.domain.user.service;

import cheongsan.domain.user.dto.SignUpRequestDTO;
import cheongsan.domain.user.dto.SignUpResponseDTO;

public interface AuthService {
    SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO);
}
