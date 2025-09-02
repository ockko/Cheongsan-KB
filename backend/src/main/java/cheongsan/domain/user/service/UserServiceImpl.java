package cheongsan.domain.user.service;

import cheongsan.domain.debt.entity.DebtAccount;
import cheongsan.domain.debt.mapper.DebtMapper;
import cheongsan.domain.user.dto.*;
import cheongsan.domain.user.entity.User;
import cheongsan.domain.user.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final DebtMapper debtMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserServiceImpl(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            DebtMapper debtMapper,
            @Qualifier("redisTemplateForToken") RedisTemplate<String, Object> redisTemplate
    ) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.debtMapper = debtMapper;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void submitDiagnosisAnswerToUser(Long id, Long workoutId) {
        userMapper.saveDiagnosis(id, workoutId);
    }

    public UserDTO getUser(Long id) {
        User user = userMapper.findById(id);
        UserDTO userDTO = UserDTO.of(user);
        return userDTO;
    }

    public MyInfoResponseDTO getMyInfo(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return MyInfoResponseDTO.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    @Override
    public boolean verifyPassword(Long id, String oldPassword) {
        User user = userMapper.findById(id);
        if (user == null) {
            log.info("존재하지 않는 사용자입니다.");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 기존 비밀번호 일치 여부 반환
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }


    @Override
    public void changePassword(Long id, ChangePasswordRequestDTO changePasswordRequestDTO) {
        User user = userMapper.findById(id);
        if (user == null) {
            log.info("존재하지 않는 사용자입니다.");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // verifyPassword 메서드로 기존 비밀번호 검증
        if (!verifyPassword(id, changePasswordRequestDTO.getOldPassword())) {
            log.info("기존 비밀번호가 올바르지 않습니다.");
            throw new IllegalArgumentException("기존 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        String encodedNewPw = passwordEncoder.encode(changePasswordRequestDTO.getNewPassword());
        userMapper.updatePassword(id, encodedNewPw);
    }


    public void deleteAccount(Long id, DeleteAccountRequestDTO dto) {
        // 비밀번호 일치 확인
        if (!verifyPassword(id, dto.getPassword())) {
            log.info("기존 비밀번호가 올바르지 않습니다.");
            throw new IllegalArgumentException("기존 비밀번호가 올바르지 않습니다.");
        }
        // 회원 탈퇴(삭제)
        userMapper.deleteById(id);
    }

    public List<UserDebtAccountResponseDTO> getUserDebtAccounts(Long id) {
        List<DebtAccount> debtAccountList = debtMapper.findByUserId(id);
        log.info(debtAccountList.toString());
        List<UserDebtAccountResponseDTO> userDebtAccountResponseDTOList = new ArrayList<>();

        for (DebtAccount debtAccount : debtAccountList) {
            String debtInstitution = debtMapper.getFinancialInstitutionByCode(debtAccount.getOrganizationCode()).getOrganizationName();

            userDebtAccountResponseDTOList.add(UserDebtAccountResponseDTO.builder()
                    .debtId(debtAccount.getId())
                    .debt_name(debtAccount.getDebtName())
                    .organization_code(debtAccount.getOrganizationCode())
                    .organization_name(debtInstitution)
                    .build());
        }
        log.info(userDebtAccountResponseDTOList.toString());
        return userDebtAccountResponseDTOList;
    }

    @Override
    public void logout(String userId) {
        redisTemplate.delete("RT:" + userId);
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void updateName(Long id, UpdateNicknameRequestDTO request) {
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수 입력값입니다.");
        }

        userMapper.updateNickname(id, request.getNickname());

    }

    @Transactional
    public void updateEmail(Long id, UpdateEmailRequestDTO request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }
        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

        userMapper.updateEmail(id, request.getEmail());

    }
}
