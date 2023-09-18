package cz.cvut.fel.evaluator.evaluation.sql;

import cz.cvut.fel.evaluator.evaluation.model.ReferentialConstraint;
import cz.cvut.fel.evaluator.evaluation.sql.query.TableQueryHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TableQueryHandlerTest {

    private final TableQueryHandler tableQueryHandler;

    @Autowired
    public TableQueryHandlerTest(TableQueryHandler tableQueryHandler) {
        this.tableQueryHandler = tableQueryHandler;
    }

    @Test
    void getTableNames_TwoTablesAreInTheDatabase_TwoTableNamesAreGot() {
        List<String> expectedResult = List.of("TABLE_NAME_1", "TABLE_NAME_2");
        List<String> actualResult = tableQueryHandler.getTableNames();

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void getColumns_ValidTableName_ListOfColumnsIsGot() {
        List<String> expectedResult = List.of("TABLE_ATTRIBUTE_1", "TABLE_ATTRIBUTE_2", "TABLE_ATTRIBUTE_3");
        List<String> actualResult = tableQueryHandler.getColumns("TABLE_NAME_1");

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void getColumns_InvalidTableName_EmptyListOfColumnsIsGot() {
        List<String> expectedResult = new ArrayList<>();
        List<String> actualResult = tableQueryHandler.getColumns("TABLE_NAME_3");

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void getPrimaryKey_ValidTableName_PrimaryKeyIsGot() {
        List<String> expectedResult = List.of("TABLE_ATTRIBUTE_1", "TABLE_ATTRIBUTE_2");
        List<String> actualResult = tableQueryHandler.getPrimaryKey("TABLE_NAME_2");

        Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedResult, actualResult));
    }

    @Test
    void getUniqueNotNull_ValidTableName_AttributesWithUniqueNotNullConstraintsAreGot() {
        List<List<String>> expectedResult = List.of(List.of("TABLE_ATTRIBUTE_2"));

        List<List<String>> actualResult = new ArrayList<>();
        Map<String, List<String>> actualResultMap = tableQueryHandler.getUniqueNotNull("TABLE_NAME_1");
        actualResultMap.keySet().forEach(key -> actualResult.add(actualResultMap.get(key)));

        Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedResult, actualResult));
    }

    @Test
    void getUniqueNotNull_ValidTableNameButTableDoesNotContainRequiredConstraintsCombination_EmptyListIsGot() {
        List<List<String>> expectedResult = new ArrayList<>(new ArrayList<>());

        List<List<String>> actualResult = new ArrayList<>();
        Map<String, List<String>> actualResultMap = tableQueryHandler.getUniqueNotNull("TABLE_NAME_2");
        actualResultMap.keySet().forEach(key -> actualResult.add(actualResultMap.get(key)));

        Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedResult, actualResult));
    }

    @Test
    void getUniqueNotNull_InvalidTableName_EmptyListIsGot() {
        List<List<String>> expectedResult = new ArrayList<>(new ArrayList<>());

        List<List<String>> actualResult = new ArrayList<>();
        Map<String, List<String>> actualResultMap = tableQueryHandler.getUniqueNotNull("TABLE_NAME_3");
        actualResultMap.keySet().forEach(key -> actualResult.add(actualResultMap.get(key)));

        Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedResult, actualResult));
    }

    @Test
    void getReferentialConstraints_ValidTableName_FirstConstraintRefTableIsValid() {
        List<ReferentialConstraint> expectedResult = new ArrayList<>();
        ReferentialConstraint referentialConstraint = new ReferentialConstraint("TABLE_NAME_1");
        referentialConstraint.addAttribute("TABLE_ATTRIBUTE_1");
        referentialConstraint.addRefAttribute("TABLE_ATTRIBUTE_1");
        expectedResult.add(referentialConstraint);

        List<ReferentialConstraint> actualResult = tableQueryHandler.getReferentialConstraints("TABLE_NAME_2");

        Assertions.assertEquals(expectedResult.get(0).getRefTable(), actualResult.get(0).getRefTable());
    }

    @Test
    void getReferentialConstraints_ValidTableName_FirstConstraintAttributesAreValid() {
        List<ReferentialConstraint> expectedResult = new ArrayList<>();
        ReferentialConstraint referentialConstraint = new ReferentialConstraint("TABLE_NAME_1");
        referentialConstraint.addAttribute("TABLE_ATTRIBUTE_1");
        referentialConstraint.addRefAttribute("TABLE_ATTRIBUTE_1");
        expectedResult.add(referentialConstraint);

        List<ReferentialConstraint> actualResult = tableQueryHandler.getReferentialConstraints("TABLE_NAME_2");

        Assertions.assertEquals(expectedResult.get(0).getAttributes(), actualResult.get(0).getAttributes());
    }

    @Test
    void getReferentialConstraints_ValidTableName_FirstConstraintRefAttributesAreValid() {
        List<ReferentialConstraint> expectedResult = new ArrayList<>();
        ReferentialConstraint referentialConstraint = new ReferentialConstraint("TABLE_NAME_1");
        referentialConstraint.addAttribute("TABLE_ATTRIBUTE_1");
        referentialConstraint.addRefAttribute("TABLE_ATTRIBUTE_1");
        expectedResult.add(referentialConstraint);

        List<ReferentialConstraint> actualResult = tableQueryHandler.getReferentialConstraints("TABLE_NAME_2");

        Assertions.assertEquals(expectedResult.get(0).getRefAttributes(), actualResult.get(0).getRefAttributes());
    }

    @Test
    void getReferentialConstraints_InvalidTableName_ListOfConstraintsIsEmpty() {
        List<ReferentialConstraint> expectedResult = new ArrayList<>();;
        List<ReferentialConstraint> actualResult = tableQueryHandler.getReferentialConstraints("TABLE_NAME_3");

        Assertions.assertTrue(CollectionUtils.isEqualCollection(expectedResult, actualResult));
    }
}
