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
    public void submitDiagnosisAnswerToUser(Long userId, Long workoutId) {
        userMapper.saveDiagnosis(userId, workoutId);
    }

    public UserDTO getUser(Long id) {
        User user = userMapper.findById(id);
        UserDTO userDTO = UserDTO.of(user);
        return userDTO;
    }

    public MyInfoResponseDTO getMyInfo(Long userId) {
        User user = userMapper.findById(userId);
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

    public UpdateMyProfileResponseDTO updateMyProfile(Long userId, UpdateMyProfileRequestDTO updateMyProfileRequestDTO) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 닉네임 길이 검사 (여기 ↓)
        if (updateMyProfileRequestDTO.getNickname() != null && updateMyProfileRequestDTO.getNickname().length() > 10) {
            throw new IllegalArgumentException("닉네임은 10자 이내로 입력하세요.");
        }

        userMapper.updateProfile(userId, updateMyProfileRequestDTO.getNickname(), updateMyProfileRequestDTO.getEmail());
        return UpdateMyProfileResponseDTO.builder()
                .message("내 정보가 성공적으로 수정되었습니다.")
                .nickname(updateMyProfileRequestDTO.getNickname())
                .email(updateMyProfileRequestDTO.getEmail())
                .build();
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequestDTO changePasswordRequestDTO) {
        User user = userMapper.findById(userId);
        if (user == null) {
            log.info("존재하지 않는 사용자입니다.");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 2. 기존 비번 일치 확인
        if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            log.info("기존 비밀번호가 올바르지 않습니다.");
            throw new IllegalArgumentException("기존 비밀번호가 올바르지 않습니다.");
        }

        // 3. 새 비밀번호 암호화 후 저장
        String encodedNewPw = passwordEncoder.encode(changePasswordRequestDTO.getNewPassword());
        userMapper.updatePassword(userId, encodedNewPw);
    }

    public void deleteAccount(Long userId, DeleteAccountRequestDTO dto) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        // 회원 탈퇴(삭제)
        userMapper.deleteById(user.getUserId());
    }

    public List<UserDebtAccountResponseDTO> getUserDebtAccounts(Long userId) {
        List<DebtAccount> debtAccountList = debtMapper.findByUserId(userId);
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
}
