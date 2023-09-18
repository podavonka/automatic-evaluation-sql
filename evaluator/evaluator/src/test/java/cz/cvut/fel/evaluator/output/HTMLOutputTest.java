package cz.cvut.fel.evaluator.output;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.evaluator.evaluation.Criterion;
import cz.cvut.fel.evaluator.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HTMLOutputTest {

    HTMLOutput htmlOutput = new HTMLOutput();

    Map<String, Criterion> criteria;

    MockedStatic<FileUtils> utilities;

    @BeforeEach
    public void setUpTerminalOutput() throws IOException {
        String json = Files.readString(Paths.get("src/test/resources/criteria/criteria-map.json"));
        ObjectMapper mapper = new ObjectMapper();
        this.criteria = mapper.readValue(json, new TypeReference<>() {});
    }

    @BeforeEach
    public void setUpMock() {
        utilities = Mockito.mockStatic(FileUtils.class);
    }

    @AfterEach
    public void close() {
        utilities.close();
    }

    @Test
    void generateEvaluationResult_ZeroCriteria_FormattedOutput() throws IOException {
        Output output = new Output(criteria, new ArrayList<>(), new ArrayList<>());

        utilities.when(FileUtils::getProgramPath).thenReturn("src/test/resources/assertions");
        utilities.when(() -> FileUtils.readString(Mockito.anyString(), Mockito.any())).thenReturn(Files.readString(Path.of("src/test/resources/output/template.html")));

        htmlOutput.generateEvaluationResult(output, "actual-html-output.html");

        String expectedOutput = Files
                .readString(Path.of("src/test/resources/assertions/expected-html-output.html"))
                .replaceAll("\\r?\\n|\\r", "\r\n")
                .replaceAll("\\s+","");

        String actualOutput = Files
                .readString(Path.of("src/test/resources/assertions/actual-html-output.html"))
                .replaceAll("\\r?\\n|\\r", "\r\n")
                .replaceAll("\\s+","");

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

}
