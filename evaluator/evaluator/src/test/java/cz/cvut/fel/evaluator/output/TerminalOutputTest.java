package cz.cvut.fel.evaluator.output;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.evaluator.evaluation.Criterion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@SpringBootTest
public class TerminalOutputTest {

    TerminalOutput terminalOutput;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpTerminalOutput() throws IOException {
        String json = Files.readString(Paths.get("src/test/resources/criteria/criteria-map.json"));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Criterion> criteria = mapper.readValue(json, new TypeReference<>() {});
        this.terminalOutput = new TerminalOutput(criteria);
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void printScoreTable_ZeroCriteria_FormattedOutput() throws IOException {
        terminalOutput.printScoreTable();

        String expectedOutput = Files
                .readString(Path.of("src/test/resources/assertions/expected-score-table.txt"))
                .replaceAll("\\r?\\n|\\r", "\r\n");

        String actualOutput = outContent.toString();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }
}
