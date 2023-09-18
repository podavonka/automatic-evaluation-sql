package cz.cvut.fel.evaluator.evaluation.sql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class SQLParserTest {

    SQLParser sqlParser = new SQLParser();

    static Stream<Arguments> generateSQLSolution() {
        String sqlSolution1 =
                "CREATE SEQUENCE sequence_1\n" +
                "start with 1\n" +
                "increment by 1\n" +
                "minvalue 0\n" +
                "maxvalue 100\n" +
                "cycle;";

        String sqlSolution2 =
                "DROP TABLE IF EXISTS TABLE_NAME_1;";

        String sqlSolution3 =
                "CREATE TABLE IF NOT EXISTS TABLE_NAME_1 (\n" +
                "    TABLE_ATTRIBUTE_1 VARCHAR(1) PRIMARY KEY,\n" +
                "    TABLE_ATTRIBUTE_2 VARCHAR(1) NOT NULL UNIQUE,\n" +
                "    TABLE_ATTRIBUTE_3 VARCHAR(1) NOT NULL\n" +
                "        CHECK (TABLE_ATTRIBUTE_3 NOT LIKE '%[^0-9]%')\n" +
                ");";

        String sqlSolution4 =
                "INSERT INTO TABLE_NAME_1 (TABLE_ATTRIBUTE_1, TABLE_ATTRIBUTE_2, TABLE_ATTRIBUTE_3)\n" +
                "VALUES\n" +
                "     ('1', '2', '3');";

        String sqlSolution5 =
                "SELECT * FROM TABLE_NAME_1;";

        return Stream.of(
                Arguments.of(sqlSolution1),
                Arguments.of(sqlSolution2),
                Arguments.of(sqlSolution3),
                Arguments.of(sqlSolution4),
                Arguments.of(sqlSolution5)
        );
    }

    @ParameterizedTest(name = "sqlSolutionIs_{0}")
    @MethodSource("generateSQLSolution")
    void parse_SQLSolutionContainsValidQuery_PlainQueryIsGot(String sqlSolution) {
        String expectedResult = sqlSolution;
        String actualResult = sqlParser.parse(sqlSolution).get(0);

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
