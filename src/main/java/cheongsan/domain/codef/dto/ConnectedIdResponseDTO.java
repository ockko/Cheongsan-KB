package cheongsan.domain.codef.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectedIdResponseDTO {
    private ResultInfo result;
    private DataInfo data;
    private String connectedId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultInfo {
        private String code;
        private String message;
        private String transactionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataInfo {
        private String connectedId;
    }
}
