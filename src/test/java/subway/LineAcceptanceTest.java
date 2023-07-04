package subway;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helper.StationTestHelper.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class LineAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        지하철역을_생성한다("지하철역");
        지하철역을_생성한다("새로운지하철역");

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 10);
        ExtractableResponse<Response> response = 지하철노선을_생성한다(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        String lineId = response.jsonPath()
                .get("id")
                .toString();

        // then
        List<String> lines = 지하철노선_목록을_조회한다();

        assertThat(lines).contains(lineId);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        // given
        지하철역을_생성한다("지하철역");
        지하철역을_생성한다("새로운지하철역");
        지하철역을_생성한다("또다른지하철역");

        Map<String, Object> line1 = new HashMap<>();
        line1.put("name", "신분당선");
        line1.put("color", "bg-red-600");
        line1.put("upStationId", 1);
        line1.put("downStationId", 2);
        line1.put("distance", 10);
        지하철노선을_생성한다(line1);

        Map<String, Object> line2 = new HashMap<>();
        line2.put("name", "분당선");
        line2.put("color", "bg-red-600");
        line2.put("upStationId", 1);
        line2.put("downStationId", 3);
        line2.put("distance", 10);
        지하철노선을_생성한다(line2);

        // when
        List<String> lines = 지하철노선_목록을_조회한다();

        // then
        assertThat(lines).hasSize(2);
    }

    private static ExtractableResponse<Response> 지하철노선을_생성한다(Map<String, Object> params) {
        ExtractableResponse<Response> response =
                RestAssured.given()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .post("/lines")
                        .then()
                        .log().all()
                        .extract();
        return response;
    }

    private static List<String> 지하철노선_목록을_조회한다() {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .log().all()
                .extract()
                .jsonPath()
                .getList("id", String.class);
    }
}