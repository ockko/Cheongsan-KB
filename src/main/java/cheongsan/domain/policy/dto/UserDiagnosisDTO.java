package cheongsan.domain.policy.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserDiagnosisDTO {
    private String sessionId;
    private List<Answer> answers;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Answer {
        private int questionId;
        private int optionId;
    }
}
