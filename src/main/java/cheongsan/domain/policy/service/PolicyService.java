package cheongsan.domain.policy.service;

import cheongsan.domain.policy.dto.PolicyDTO;
import cheongsan.domain.policy.dto.PolicyRequestDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public interface PolicyService {

    public List<PolicyDTO> getPolicyList(PolicyRequestDTO policyRequestDTO) throws UnsupportedEncodingException, IOException, Exception;

    public List<PolicyDTO> getPolicyListByTags(List<String> tags, List<PolicyDTO> policyList);

}
