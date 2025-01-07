package com.example.mywalletapp.service.serviceimpl;

import com.example.mywalletapp.service.BVNVerificationService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BVNVerificationServiceImpl implements BVNVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(BVNVerificationServiceImpl.class);

    @Value("${youverify.api.key}")
    private String apiKey;

    @Value("${youverify.api.secret}")
    private String apiSecret;

    @Value("${youverify.api.base-url}")
    private String baseUrl;

    @Override
    public boolean verifyBVN(String bvn, String surname, String dateOfBirth) {
        OkHttpClient client = new OkHttpClient();
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", bvn);
        requestBody.put("isSubjectConsent", true);
        requestBody.put("premiumBVN", true);
        requestBody.put("surname", surname);
        requestBody.put("dateOfBirth", dateOfBirth);

        Request request = new Request.Builder()
                .url(baseUrl)
                .header("Authorization", "Bearer " + apiKey + ":" + apiSecret)
                .post(RequestBody.create(
                        requestBody.toString(),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                return parseResponse(response.body().string(), surname, dateOfBirth);
            } else {
                logger.error("Failed to verify BVN: {}", response.message());
                return false;
            }
        } catch (IOException e) {
            logger.error("IOException occurred while verifying BVN", e);
            return false;
        }
    }

    private boolean parseResponse(String responseBody, String surname, String dateOfBirth) {
        JSONObject responseJson = new JSONObject(responseBody);
        if (responseJson.getBoolean("success")) {
            JSONObject data = responseJson.getJSONObject("data");
            return surname.equalsIgnoreCase(data.getString("surname")) &&
                    dateOfBirth.equals(data.getString("dateOfBirth"));
        }
        return false;
    }
}
