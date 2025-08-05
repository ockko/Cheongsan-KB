package cheongsan.domain.user.controller;

import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // 더미 테스트용 AuthService 구현 (내부 클래스)
    private static class StubAuthService implements AuthService {
        @Override
        public boolean checkDuplicate(String userId) {
            return false;
        }

        @Override
        public SignUpResponseDTO signUp(SignUpRequestDTO request) {
            if ("dupeid".equals(request.getUserId())) {
                throw new IllegalArgumentException("중복된 아이디입니다.");
            } else if ("causeError".equals(request.getUserId())) {
                throw new RuntimeException("강제 에러!");
            }
            return new SignUpResponseDTO(101L, request.getUserId());
        }

        @Override
        public FindUserIdResponseDTO findUserIdByEmail(FindUserIdRequestDTO findUserIdRequestDTO) {
            return null;
        }

        @Override
        public FindUserPasswordResponseDTO findUserPassword(FindUserPasswordRequestDTO findUserPasswordRequestDTO) {
            return null;
        }

        @Override
        public LogInResponseDTO login(LogInRequestDTO logInRequestDTO) {
            return null;
        }

        @Override
        public NicknameResponseDTO submitNickname(NicknameRequestDTO nicknameRequestDTO) {
            return null;
        }

        @Override
        public TokenRefreshResponseDTO reissueTokens(String refreshToken) {
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        AuthService stubService = new StubAuthService();
//        AuthController authController = new AuthController(stubService);
//        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("회원가입 성공(200)")
    void signUpSuccess() throws Exception {
        SignUpRequestDTO req = SignUpRequestDTO.builder()
                .userId("tester99")
                .password("testpw@123")
                .email("tester99@email.com")
                .build();

        mockMvc.perform(post("/cheongsan/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.userId").value("tester99"));
    }

    @Test
    @DisplayName("중복 아이디(400)")
    void signUpDuplicateUser() throws Exception {
        SignUpRequestDTO req = SignUpRequestDTO.builder()
                .userId("dupeid")
                .password("testpw@123")
                .email("dupe@email.com")
                .build();

        mockMvc.perform(post("/cheongsan/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("중복된 아이디입니다."));
    }

    @Test
    @DisplayName("서버 내부 에러(500)")
    void signUpServerError() throws Exception {
        SignUpRequestDTO req = SignUpRequestDTO.builder()
                .userId("causeError")
                .password("pass1234")
                .email("any@email.com")
                .build();

        mockMvc.perform(post("/cheongsan/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }
}
