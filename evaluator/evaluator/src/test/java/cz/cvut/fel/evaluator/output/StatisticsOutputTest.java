package cz.cvut.fel.evaluator.output;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

@SpringBootTest
public class StatisticsOutputTest {

    StatisticsOutput statisticsOutput = new StatisticsOutput();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

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
    void generateStatistics_StatisticsAreNull_ReturnsListOfFormattedData() {
        List<String> expectedOutput = List.of(
                "CREATE TABLE: 0 read / 0 executed<br>",
                "INSERT INTO: 0 read / 0 executed<br>",
                "SELECT: 0 read / 0 executed<br>");
        List<String> actualOutput = statisticsOutput.generateList();;

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void printAll_StatisticsAreNull_PrintsFormattedData() {
        statisticsOutput.printAll();

        String expectedOutput =
                String.format("%6s: %2s read / %2s executed\n", "CREATE", 0, 0) +
                String.format("%6s: %2s read / %2s executed\n", "INSERT", 0, 0) +
                String.format("%6s: %2s read / %2s executed\n", "SELECT", 0, 0);
        String actualOutput = outContent.toString();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void print_GetsDataToPrint_PrintsFormattedData() {
        statisticsOutput.print("CREATE", 2, 1);

        String expectedOutput = String.format("%6s: %2s read / %2s executed\n", "CREATE", 2, 1);
        String actualOutput = outContent.toString();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getCreateTableQueriesCount_IncrementsCountBy1_CountIs1() {
        statisticsOutput.incrementCreateTableQueriesCount();

        int expectedOutput = 1;
        int actualOutput = statisticsOutput.getCreateTableQueriesCount();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getInsertQueriesCount_IncrementsCountBy1_CountIs1() {
        statisticsOutput.incrementInsertQueriesCount();

        int expectedOutput = 1;
        int actualOutput = statisticsOutput.getInsertQueriesCount();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getSelectQueriesCount_IncrementsCountBy1_CountIs1() {
        statisticsOutput.incrementSelectQueriesCount();

        int expectedOutput = 1;
        int actualOutput = statisticsOutput.getSelectQueriesCount();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getExecutedCreateTableQueriesCount_IncrementsCountBy1_CountIs1() {
        statisticsOutput.incrementExecutedCreateTableQueriesCount();

        int expectedOutput = 1;
        int actualOutput = statisticsOutput.getExecutedCreateTableQueriesCount();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getExecutedInsertQueriesCount_IncrementsCountBy1_CountIs1() {
        statisticsOutput.incrementExecutedInsertQueriesCount();

        int expectedOutput = 1;
        int actualOutput = statisticsOutput.getExecutedInsertQueriesCount();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getExecutedSelectQueriesCount_IncrementsCountBy1_CountIs1() {
        statisticsOutput.incrementExecutedSelectQueriesCount();

        int expectedOutput = 1;
        int actualOutput = statisticsOutput.getExecutedSelectQueriesCount();

        Assertions.assertEquals(expectedOutput, actualOutput);
    }
}
