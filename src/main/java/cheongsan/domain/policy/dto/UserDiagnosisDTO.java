package cheongsan.domain.policy.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDiagnosisDTO {
    private String sessionId;
    private List<Answer> answers;

    @Data
    @NoArgsConstructor
    public static class Answer {
        private int questionId;
        private int optionId;
    }
}
