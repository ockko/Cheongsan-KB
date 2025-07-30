package cheongsan.domain.codef.controller;

import cheongsan.domain.codef.service.CodefAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

// codef token 테스트용 Controller
@Controller
@RequestMapping("/cheongsan/codef")
public class CodefTestController {
    @Autowired
    private CodefAuthService codefAuthService;

    @GetMapping("/token")
    @ResponseBody
    public String getToken(HttpServletResponse response) {
        try {
            String token = codefAuthService.getAccessToken();
            return "ACCESS TOKEN: " + token;
        } catch (Exception e) {
            response.setStatus(500);
            return "ERROR: " + e.getMessage();
        }
    }
}
