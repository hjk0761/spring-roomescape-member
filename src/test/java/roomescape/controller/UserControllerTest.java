package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("정상적인 로그인 요청 시 200으로 응답한다.")
    @Test
    void loginTest() {
        Map<String, String> params = new HashMap<>();
        params.put("email", "email@email.com");
        params.put("password", "password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all().statusCode(200);
    }

    @DisplayName("존재하지 않는 아이디인 경우 400으로 응답한다.")
    @Test
    void wrongIdTest() {
        Map<String, String> params = new HashMap<>();
        params.put("email", "wrongEmail@email.com");
        params.put("password", "password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all().statusCode(400)
                .body(is("존재하지 않는 아이디입니다."));
    }

    @DisplayName("존재하지 않는 아이디인 경우 400으로 응답한다.")
    @Test
    void wrongPasswordTest() {
        Map<String, String> params = new HashMap<>();
        params.put("email", "email@email.com");
        params.put("password", "wrongPassword");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all().statusCode(400)
                .body(is("비밀번호가 일치하지 않습니다."));
    }
}
