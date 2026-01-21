package com.example.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

/**
 * Security configuration integration tests.
 * Validates that the gateway enforces authentication and authorization rules correctly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
    "spring.profiles.active=test",
    "spring.cloud.gateway.routes[0].id=test-route",
    "spring.cloud.gateway.routes[0].uri=http://localhost:9999",
    "spring.cloud.gateway.routes[0].predicates[0]=Path=/api/**"
})
class SecurityConfigTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void protectedEndpointReturns401WithoutAuth() {
        webClient.get()
            .uri("/api/countries")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void authenticatedUserCanAccessProtectedEndpoint() {
        webClient
            .mutateWith(mockJwt())
            .get()
            .uri("/actuator/info")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void healthEndpointIsPublic() {
        webClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void healthLivenessProbeIsPublic() {
        webClient.get()
            .uri("/actuator/health/liveness")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void infoEndpointRequiresAuth() {
        webClient.get()
            .uri("/actuator/info")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void randomPathRequiresAuthentication() {
        webClient.get()
            .uri("/some/random/path")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
