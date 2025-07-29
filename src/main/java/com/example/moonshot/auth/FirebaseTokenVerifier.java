package com.example.moonshot.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jose.proc.JWSKeySelector;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;



import java.net.URI;
import java.net.URL;

import java.util.Map;

@Component
public class FirebaseTokenVerifier {

    private static final String FIREBASE_PROJECT_ID = "inclusive-trip-planner";
    private static final String ISSUER = "https://securetoken.google.com/" + FIREBASE_PROJECT_ID;
    private static final String EXPECTED_AUDIENCE = "inclusive-trip-planner";
    private static final String KEYS_URL = "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com";

    private static final Logger log = LoggerFactory.getLogger(FirebaseTokenVerifier.class);
    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @PostConstruct
    private void init() {
        try {
            URL jwkUrl = URI.create(KEYS_URL).toURL();
            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(jwkUrl);

            JWSKeySelector<SecurityContext> keySelector =
                    new JWSAlgorithmFamilyJWSKeySelector<>(com.nimbusds.jose.JWSAlgorithm.Family.RSA, keySource);

            jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(keySelector);

        } catch (Exception e) {
            throw new RuntimeException("Error initializing FirebaseTokenVerifier", e);
        }
    }

    public Map<String, Object> verify(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            var claims = jwtProcessor.process(signedJWT, null);

            log.info("Parsed claims: {}", claims.getClaims());

            boolean validIssuer = ISSUER.equals(claims.getIssuer());
            boolean validAudience = claims.getAudience().contains(EXPECTED_AUDIENCE);

            if (!validIssuer || !validAudience) {
                log.info("Issuer check: {}", claims.getIssuer());
                log.info("Audience check: {}", claims.getAudience());
                throw new IllegalArgumentException("Invalid Firebase token: issuer or audience mismatch");
            }

            return new ObjectMapper().convertValue(
                    claims.getClaims(),
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (Exception e) {
            log.error(" Firebase token verification failed", e);
            throw new RuntimeException("Firebase token verification failed", e);
        }
    }
}
