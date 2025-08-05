package cheongsan.domain.user.dto;

import cheongsan.common.constant.ResponseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInRequestDTO {
    private String username;
    private String password;

    public static LogInRequestDTO of(HttpServletRequest request) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(request.getInputStream(), LogInRequestDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadCredentialsException(ResponseMessage.CREDENTIALS_MISSING.getMessage());
        }
    }
}
