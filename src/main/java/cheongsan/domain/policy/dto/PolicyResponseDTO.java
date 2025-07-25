package cheongsan.domain.policy.dto;

import lombok.Data;

import java.util.List;

@Data
public class PolicyResponseDTO {
    private int totalCount;
    private int pageNo;
    private List<PolicyDTO> itemList;
}
