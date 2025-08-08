package cheongsan.domain.policy.service;

import cheongsan.domain.policy.dto.PolicyDTO;
import cheongsan.domain.policy.dto.PolicyDetailDTO;
import cheongsan.domain.policy.dto.PolicyRequestDTO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface PolicyService {

    List<PolicyDTO> getPolicyList(PolicyRequestDTO policyRequestDTO) throws UnsupportedEncodingException, IOException, Exception;

    List<PolicyDTO> getPolicyListSearch(PolicyRequestDTO policyRequestDTO) throws UnsupportedEncodingException, IOException, Exception;

    PolicyDetailDTO getPolicyDetail(PolicyRequestDTO policyRequestDTO) throws UnsupportedEncodingException, IOException, Exception;
}
