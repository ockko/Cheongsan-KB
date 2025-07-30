package cheongsan.domain.codef.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CodefAuthService {
    @Value("${codef.token.url}")
    private String codefTokenUrl;

    @Value("${codef.client.id}")
    private String clientId;

    @Value("${codef.client.secret}")
    private String clientSecret;

    public String getAccessToken() throws IOException {
        OkHttpClient client = new OkHttpClient();

        // POST Body - application/x-www-form-urlencoded
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("scope", "read")
                .build();

        // client_id:client_secret Base64 인코딩 (Basic Auth용)
        String credential = Credentials.basic(clientId, clientSecret);

        Request request = new Request.Builder()
                .url(codefTokenUrl)
                .post(formBody)
                .header("Authorization", credential)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("토큰 요청 실패: " + response);
            }

            String responseBody = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseBody);

            return node.get("access_token").asText();
        }
    }
}
