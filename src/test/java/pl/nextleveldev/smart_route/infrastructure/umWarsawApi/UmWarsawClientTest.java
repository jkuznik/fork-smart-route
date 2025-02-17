package pl.nextleveldev.smart_route.infrastructure.umWarsawApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.nextleveldev.smart_route.infrastructure.umWarsawApi.dto.StopDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {UmWarsawConfig.class, MethodValidationPostProcessor.class})
@TestPropertySource(properties = {
        "um.warsaw.api-key=${UM_WARSAW_API_KEY}",
        "um.warsaw.base-url=https://api.um.warszawa.pl",
        "um.warsaw.timetable-resource-url=api/action/dbtimetable_get",
        "um.warsaw.timetable-resource-id=e923fa0e-d96c-43f9-ae6e-60518c9f3238",
        "um.warsaw.bus-line-on-stop-resource-id=88cd555f-6f31-43ca-9de4-66c479ad5942"
})
class UmWarsawClientTest {

    @Autowired
    UmWarsawAPI umWarsawAPI;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldThrowException_whenStopDtoIsNotValid() {
        //given
        var stopDto = new StopDto(null, null);

        //when
        Exception result = catchException(() -> umWarsawAPI.getSupportedBusLinesAtStop(stopDto));

        //then
        assertInstanceOf(ConstraintViolationException.class, result);
    }

    @Test
    void shouldNotThrowException_whenParamsAreValid() {
        //given
        var stopDto = new StopDto("7009", "01");

        //when
        Exception result = catchException(() -> umWarsawAPI.getSupportedBusLinesAtStop(stopDto));

        //then
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnJsonValue_whenParamsAreValid() {
        //given
        var stopDto = new StopDto("7009", "01");

        //when
        String result = umWarsawAPI.getSupportedBusLinesAtStop(stopDto);

        //then
        assertTrue(isValidJson(result));
    }

    private boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}