package subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static helper.StationTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class StationAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        String stationName = "강남역";

        // when
        ExtractableResponse<Response> response = 지하철역을_생성한다(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철역_목록을_조회한다();
        assertThat(stationNames).containsAnyOf(stationName);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void searchStations() {
        List<String> addStationNames = List.of("강남역", "선릉역");

        // given
        addStationNames.forEach(stationName -> {
            지하철역을_생성한다(stationName);
        });

        // when
        List<String> stationNames = 지하철역_목록을_조회한다();

        // then
        assertThat(stationNames).hasSize(2);
    }


    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void removeStations() {
        // given
        String stationId = 지하철역을_생성한다("강남역")
                .jsonPath()
                .get("id")
                .toString();

        // when
        ExtractableResponse<Response> response = 지하철역을_삭제한다(stationId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        List<String> stationNames = 지하철역_목록을_조회한다();
        assertThat(stationNames).doesNotContain("강남역");
    }
}