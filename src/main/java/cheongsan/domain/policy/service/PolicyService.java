package cheongsan.domain.policy.service;

import cheongsan.domain.policy.dto.PolicyDetailResponseDTO;
import cheongsan.domain.policy.dto.PolicyRequestDTO;
import cheongsan.domain.policy.dto.PolicyResponseDTO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface PolicyService {

    List<PolicyResponseDTO> getPolicyList(PolicyRequestDTO policyRequestDTO) throws UnsupportedEncodingException, IOException, Exception;

    List<PolicyResponseDTO> getPolicyListSearch(PolicyRequestDTO policyRequestDTO) throws UnsupportedEncodingException, IOException, Exception;

    PolicyDetailResponseDTO getPolicyDetail(PolicyRequestDTO policyRequestDTO) throws UnsupportedEncodingException, IOException, Exception;
}
