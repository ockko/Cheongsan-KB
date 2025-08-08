package cheongsan.domain.policy.controller;

import cheongsan.domain.policy.dto.PolicyDTO;
import cheongsan.domain.policy.dto.PolicyDetailDTO;
import cheongsan.domain.policy.dto.PolicyRequestDTO;
import cheongsan.domain.policy.service.PolicyService;
import cheongsan.domain.user.entity.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cheongsan/policies")
@RequiredArgsConstructor
@Slf4j
public class PolicyController {

    private final PolicyService policyService;


    @GetMapping("/list")
    public ResponseEntity<List<PolicyDTO>> getPolicyList(
            Principal principal
    ) throws Exception {
        PolicyRequestDTO policyRequestDTO = new PolicyRequestDTO();
        // JWT에서 userId 추출
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        policyRequestDTO.setUserId(userId);

        List<PolicyDTO> policyList = policyService.getPolicyList(policyRequestDTO);
        return ResponseEntity.ok(policyList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PolicyDTO>> searchPolicyList(
            @RequestParam(required = false) String searchWrd,
            Principal principal
    ) throws Exception {
        PolicyRequestDTO policyRequestDTO = new PolicyRequestDTO();

        // JWT에서 userId 추출
        Authentication authentication = (Authentication) principal;
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long userId = customUser.getUser().getId();
        policyRequestDTO.setUserId(userId);
        System.out.println(searchWrd);

        policyRequestDTO.setSearchWrd(searchWrd);

        List<PolicyDTO> searchedPolicyList = policyService.getPolicyListSearch(policyRequestDTO);


        return ResponseEntity.ok(searchedPolicyList);
    }

    @GetMapping("/detail")
    public ResponseEntity<PolicyDetailDTO> getPolicyDetail(
            @RequestParam(required = false) String policyName) throws Exception {
        PolicyRequestDTO policyRequestDTO = new PolicyRequestDTO();
        policyRequestDTO.setSearchWrd(policyName);
        PolicyDetailDTO policyDetailDTO = policyService.getPolicyDetail(policyRequestDTO);

        return ResponseEntity.ok(policyDetailDTO);
    }


}
