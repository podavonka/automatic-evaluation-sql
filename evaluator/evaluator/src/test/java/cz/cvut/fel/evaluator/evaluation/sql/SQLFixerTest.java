package cz.cvut.fel.evaluator.evaluation.sql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SQLFixerTest {

    private SQLFixer sqlFixer = new SQLFixer();

    @Test
    void fix_EmptyMessage_QueryHasNotChanged() {
        String expectedResult = "QUERY";
        String actualResult = sqlFixer.fix("QUERY", "");

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void fix_MessageColumnNotFound_QueryHasChanged() {
        String expectedResult = "SELECT COUNT(NAME.*)";
        String actualResult = sqlFixer.fix(
                "SELECT COUNT(NAME)",
                "Column \"NAME\" not found\n" +
                        "SELECT COUNT(NAME)");

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void fix_MessageCannotParseIntervalConstant_QueryHasChanged() {
        String expectedResult =
                "INTERVAL '1' YEAR";
        String actualResult = sqlFixer.fix(
                "INTERVAL '1 YEAR'",
                "Cannot parse \"INTERVAL\" constant" +
                        "INTERVAL '1 YEAR'");

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void fix_MessageCreateSequenceError_QueryHasChanged() {
        String expectedResult =
                "CREATE SEQUENCE 'SEQUENCE_NAME' START WITH 1";
        String actualResult = sqlFixer.fix(
                "CREATE SEQUENCE 'SEQUENCE_NAME' START 1",
                "Syntax error" +
                        "CREATE SEQUENCE 'SEQUENCE_NAME' START 1");

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
