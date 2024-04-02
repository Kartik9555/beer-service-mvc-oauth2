package com.example.practice.beerservicemvc.controller;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(BeerControllerRestAssuredTest.TestConfig.class)
@ComponentScan("com.example.practice.beerservicemvc")
public class BeerControllerRestAssuredTest {

    OpenApiValidationFilter filter = new OpenApiValidationFilter(
        OpenApiInteractionValidator
            .createForSpecificationUrl("oa3.yaml")
            .withWhitelist(
                ValidationErrorsWhitelist
                    .create()
                    .withRule(
                        "Ignore date formats",
                        WhitelistRules.messageHasKey("validation.response.body.schema.format.date-time")
                    )
            )
            .build()
    );

    @Configuration
    public static class TestConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http.authorizeHttpRequests(
                    auth -> auth.anyRequest().permitAll()
                )
                .build();
        }
    }

    @LocalServerPort
    Integer localPort;

    @MockBean
    private JwtDecoder jwtDecoder;


    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }


    @Test
    void listBeers() {
        given()
            .contentType(ContentType.JSON)
            .filter(filter)
            .when()
            .get("/api/v1/beer")
            .then()
            .assertThat().statusCode(200);
    }
}
