package cz.cvut.fel.evaluator.evaluation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CriterionTest {

    Criterion criterion = new Criterion();

    @ParameterizedTest(name = "addDescription_DescriptionPartIs{0}_Success")
    @ValueSource(strings = {"", "DESCRIPTION_PART"})
    void addDescription_Success(String descriptionPart) {
        criterion.addDescription(descriptionPart);

        String expectedResult = descriptionPart;
        String actualResult = criterion.getDescription();

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "addTableName_DescriptionPartIs{0}_Success")
    @ValueSource(strings = {"", "TABLE_NAME"})
    void addTableName_Success(String tableName) {
        criterion.addTableName(tableName);

        String expectedResult = " [" + tableName + "]";
        String actualResult = criterion.getDescription();

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void incrementOccurrences_Success() {
        criterion.incrementOccurrences();

        int expectedResult = 1;
        int actualResult = criterion.getOccurrences();

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
