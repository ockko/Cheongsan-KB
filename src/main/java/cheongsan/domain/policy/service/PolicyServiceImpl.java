package cheongsan.domain.policy.service;

import cheongsan.domain.policy.dto.PolicyDetailResponseDTO;
import cheongsan.domain.policy.dto.PolicyRequestDTO;
import cheongsan.domain.policy.dto.PolicyResponseDTO;
import cheongsan.domain.user.dto.UserDTO;
import cheongsan.domain.user.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {
    private static final String POLICY_API_URL = "https://apis.data.go.kr/B554287/NationalWelfareInformationsV001/NationalWelfarelistV001";
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${policy.service-key}")
    private String SERVICE_KEY;

    public List<PolicyResponseDTO> fetchPoliciesFromApi(PolicyRequestDTO policyRequestDTO) throws Exception {
        log.info("Fetching Policies from API");
        StringBuilder urlBuilder = new StringBuilder(POLICY_API_URL);

        urlBuilder.append("?serviceKey=").append(URLEncoder.encode(SERVICE_KEY, "UTF-8"));

        String callTp = policyRequestDTO.getCallTp();
        urlBuilder.append("&callTp=").append(callTp);

        Integer pageNo = policyRequestDTO.getPageNo();
        urlBuilder.append("&pageNo=").append(pageNo);

        Integer numOfRows = policyRequestDTO.getNumOfRows();
        urlBuilder.append("&numOfRows=").append(numOfRows);

        String srchKeyCode = policyRequestDTO.getSrchKeyCode();
        urlBuilder.append("&srchKeyCode=").append(srchKeyCode);

        // searchWrd가 null이거나 빈값이면 유저 신용 상태 가져오고 "청년"을 기본값으로
        String searchWrd = policyRequestDTO.getSearchWrd();
        System.out.println("policyServie에서" + searchWrd);
        if (searchWrd == null || searchWrd.trim().isEmpty()) {
            //유저 신용 상태 가져오기
            UserDTO userDTO = userService.getUser(policyRequestDTO.getUserId());

            // 신용 상태에 따라 "부채","대출","청년"
            Long userDiagnosisResult = userDTO.getRecommendedProgramId();
            log.info("userDianosisResult={}", userDiagnosisResult);

            if (userDiagnosisResult != null) {
                if (userDiagnosisResult.equals(1L) || userDiagnosisResult.equals(2L)) {
                    searchWrd = "부채";
                } else if (userDiagnosisResult.equals(3L) || userDiagnosisResult.equals(4L) || userDiagnosisResult.equals(5L)) {
                    searchWrd = "대출";
                } else {
                    searchWrd = "신용";
                }
            } else {
                searchWrd = "청년";
            }


        }
        urlBuilder.append("&searchWrd=").append(URLEncoder.encode(searchWrd, "UTF-8"));

        log.info("urlBuilder={}", urlBuilder);
        System.out.println(urlBuilder.toString());

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        List<PolicyResponseDTO> policyList = new ArrayList<>();
        try {
            conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("API 호출 실패: " + responseCode);
            }


            // XML 파싱
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("servList");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                log.info(node.toString());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    PolicyResponseDTO dto = new PolicyResponseDTO();

                    List<String> themaList = toStringList(getTagValue("intrsThemaArray", e));
                    List<String> groupList = toStringList(getTagValue("trgterIndvdlArray", e));
                    dto.setMinistryName(getTagValue("jurMnofNm", e));
                    dto.setPolicySummary(getTagValue("servDgst", e));
                    dto.setPolicyId(getTagValue("servId", e));
                    dto.setPolicyName(getTagValue("servNm", e));
                    dto.setSupportCycle(getTagValue("sprtCycNm", e));
                    dto.setPolicyOnline(getTagValue("onapPsbltYn", e));
                    dto.setPolicyDate(getTagValue("svcfrstRegTs", e));

                    List<String> combinedList = new ArrayList<>();
                    if (themaList != null) combinedList.addAll(themaList);
                    if (groupList != null) combinedList.addAll(groupList);

                    dto.setTagList(combinedList);
                    policyList.add(dto);

                }
            }


        } finally {
            conn.disconnect();
        }

        return policyList;
    }


    @Override
    public List<PolicyResponseDTO> getPolicyList(PolicyRequestDTO policyRequestDTO) throws Exception {
        Long userId = policyRequestDTO.getUserId();
        String redisKey = "policies:" + userId;
        String cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            log.info("Redis hit: {}", redisKey);
            return objectMapper.readValue(cached, new TypeReference<List<PolicyResponseDTO>>() {
            });
        }
        List<PolicyResponseDTO> policyList = fetchPoliciesFromApi(policyRequestDTO);
        String json = objectMapper.writeValueAsString(policyList);
        redisTemplate.opsForValue().set(redisKey, json, 1, TimeUnit.HOURS);

        return policyList;


    }

    @Override
    public List<PolicyResponseDTO> getPolicyListSearch(PolicyRequestDTO policyRequestDTO) throws Exception {
        Long userId = policyRequestDTO.getUserId();
        String redisKey = "searchPolicies:" + userId + policyRequestDTO.getSearchWrd();
        String searchCached = redisTemplate.opsForValue().get(redisKey);
        if (searchCached != null) {
            log.info("Redis hit: {}", redisKey);
            return objectMapper.readValue(searchCached, new TypeReference<List<PolicyResponseDTO>>() {
            });
        }
        List<PolicyResponseDTO> policyList = fetchPoliciesFromApi(policyRequestDTO);
        String json = objectMapper.writeValueAsString(policyList);
        redisTemplate.opsForValue().set(redisKey, json, 1, TimeUnit.HOURS);

        return policyList;
    }


    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        Node node = nodeList.item(0).getFirstChild();
        return node == null ? null : node.getNodeValue();
    }

    // 콤마(,) 구분 문자열을 리스트로 변환하는 함수
    private List<String> toStringList(String value) {
        if (value == null || value.trim().isEmpty()) return Collections.emptyList();
        String[] arr = value.split(",");
        List<String> result = new ArrayList<>();
        for (String str : arr) {
            String trimmed = str.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }


    // 정책 리스트에서 하나를 누르면 그 이름을 searchWrd로 검색
    @Override
    public PolicyDetailResponseDTO getPolicyDetail(PolicyRequestDTO policyRequestDTO) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(POLICY_API_URL);

        urlBuilder.append("?serviceKey=").append(URLEncoder.encode(SERVICE_KEY, "UTF-8"));

        String callTp = policyRequestDTO.getCallTp();
        urlBuilder.append("&callTp=").append(callTp);

        Integer pageNo = policyRequestDTO.getPageNo();
        urlBuilder.append("&pageNo=").append(pageNo);

        Integer numOfRows = policyRequestDTO.getNumOfRows();
        urlBuilder.append("&numOfRows=").append(numOfRows);

        String srchKeyCode = policyRequestDTO.getSrchKeyCode();
        urlBuilder.append("&srchKeyCode=").append(srchKeyCode);

        String searchWrd = policyRequestDTO.getSearchWrd();
        log.info(URLEncoder.encode(searchWrd, "UTF-8"));
        urlBuilder.append("&searchWrd=").append(URLEncoder.encode(searchWrd, "UTF-8"));

        log.info("urlBuilder={}", urlBuilder);

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("API 호출 실패: " + responseCode);
        }


        // XML 파싱
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("servList");
        PolicyDetailResponseDTO policyDetailResponseDTO = new PolicyDetailResponseDTO();
        Node node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) node;


            policyDetailResponseDTO.setPolicyNumber(getTagValue("inqNum", e));
            policyDetailResponseDTO.setMinistryName(getTagValue("jurMnofNm", e));
            policyDetailResponseDTO.setDepartmentName(getTagValue("jurOrgNm", e));
            policyDetailResponseDTO.setPolicyName(getTagValue("servNm", e));
            policyDetailResponseDTO.setPolicyTags(toStringList(getTagValue("intrsThemaArray", e))); // 콤마로 split해서 리스트 변환
            policyDetailResponseDTO.setPolicySummary(getTagValue("servDgst", e));
            policyDetailResponseDTO.setSupportAge(getTagValue("ageInfo", e)); // '지원 연령' 태그가 다를 수 있음. 실제 XML 구조에 따라 수정
            policyDetailResponseDTO.setSupportTarget(toStringList(getTagValue("trgterIndvdlArray", e))); // list 형태
            policyDetailResponseDTO.setSupportType(getTagValue("srvPvsnNm", e));
            policyDetailResponseDTO.setSupportCycle(getTagValue("sprtCycNm", e));
            policyDetailResponseDTO.setIsOnlineApplyAvailable(getTagValue("onapPsbltYn", e));
            policyDetailResponseDTO.setContactNumber(getTagValue("rprsCtadr", e));
            policyDetailResponseDTO.setDetailPageUrl(getTagValue("servDtlLink", e));
            policyDetailResponseDTO.setPolicyId(getTagValue("servId", e));

        }


        conn.disconnect();

        return policyDetailResponseDTO;

    }

}
