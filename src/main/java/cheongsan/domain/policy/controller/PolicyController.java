package cheongsan.domain.policy.controller;

import cheongsan.common.util.ExtractUserIdUtil;
import cheongsan.domain.policy.dto.PolicyDetailResponseDTO;
import cheongsan.domain.policy.dto.PolicyRequestDTO;
import cheongsan.domain.policy.dto.PolicyResponseDTO;
import cheongsan.domain.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cheongsan/policies")
@RequiredArgsConstructor
@Slf4j
public class PolicyController {

    private final PolicyService policyService;
    private final ExtractUserIdUtil extractUserIdUtil;


    @GetMapping("/list")
    public ResponseEntity<List<PolicyResponseDTO>> getPolicyList(
            Principal principal
    ) throws Exception {
        Long userId = extractUserIdUtil.extractUserId(principal);
        PolicyRequestDTO requestDTO = new PolicyRequestDTO();
        requestDTO.setUserId(userId);

        try {
            List<PolicyResponseDTO> policyList = policyService.getPolicyList(requestDTO);
            return ResponseEntity.ok(policyList);
        } catch (Exception e) {
            log.error("Error fetching policy list for userId: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch policy list.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PolicyResponseDTO>> searchPolicyList(
            @RequestParam(required = false) String searchWrd,
            Principal principal
    ) throws Exception {
        Long userId = extractUserIdUtil.extractUserId(principal);
        PolicyRequestDTO requestDTO = new PolicyRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setSearchWrd(searchWrd);

        try {
            List<PolicyResponseDTO> searchedPolicyList = policyService.getPolicyListSearch(requestDTO);
            return ResponseEntity.ok(searchedPolicyList);
        } catch (Exception e) {
            log.error("Error searching policies for userId: {}, searchWord: {}", userId, searchWrd, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to search policies.");
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<PolicyDetailResponseDTO> getPolicyDetail(
            @RequestParam(required = false) String policyName) throws Exception {
        if (policyName == null || policyName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "policyName parameter is required.");
        }
        PolicyRequestDTO requestDTO = new PolicyRequestDTO();
        requestDTO.setSearchWrd(policyName);

        try {
            PolicyDetailResponseDTO detail = policyService.getPolicyDetail(requestDTO);
            if (detail == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found for name: " + policyName);
            }
            return ResponseEntity.ok(detail);
        } catch (ResponseStatusException e) {
            throw e; // rethrow known exceptions
        } catch (Exception e) {
            log.error("Error fetching policy detail for policyName: {}", policyName, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch policy detail.");
        }
    }


}
