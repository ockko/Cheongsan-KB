package cheongsan.domain.codef.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// ===== Connected ID 생성 관련 =====
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectedIdRequestDTO {
    private List<AccountInfo> accountList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountInfo {
        private String countryCode;
        private String businessType;
        private String organization;
        private String clientType;
        private String loginType;
        private String id;
        private String password;
    }
}
