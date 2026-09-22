package com.example.myproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GoogleAuthService {

    @Value("${google.client.id:}")
    private String configuredClientId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class GoogleUserProfile {
        private final String email;
        private final String name;
        private final String pictureUrl;

        public GoogleUserProfile(String email, String name, String pictureUrl) {
            this.email = email;
            this.name = name;
            this.pictureUrl = pictureUrl;
        }

        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getPictureUrl() { return pictureUrl; }
    }

    /**
     * Verifies the Google ID token against Google's tokeninfo API.
     * Returns GoogleUserProfile if valid, or throws RuntimeException if invalid.
     */
    public GoogleUserProfile verifyToken(String idToken) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Invalid Google token response");
            }

            JsonNode root = objectMapper.readTree(response.getBody());

            String email = root.path("email").asText(null);
            boolean emailVerified = "true".equalsIgnoreCase(root.path("email_verified").asText("false")) ||
                    root.path("email_verified").asBoolean(false);

            if (email == null || !emailVerified) {
                throw new RuntimeException("Google account email is missing or unverified");
            }

            // If a client ID is configured on the backend, verify audience
            if (configuredClientId != null && !configuredClientId.isBlank()) {
                String aud = root.path("aud").asText("");
                if (!configuredClientId.equals(aud)) {
                    throw new RuntimeException("Google token audience mismatch");
                }
            }

            String name = root.path("name").asText("Google User");
            String picture = root.path("picture").asText(null);

            return new GoogleUserProfile(email, name, picture);

        } catch (Exception e) {
            throw new RuntimeException("Google ID Token verification failed: " + e.getMessage());
        }
    }
}
