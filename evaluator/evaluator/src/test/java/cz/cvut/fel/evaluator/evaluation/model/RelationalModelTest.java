package cz.cvut.fel.evaluator.evaluation.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class RelationalModelTest {

    RelationalModel relationalModel = new RelationalModel();

    private Table getTable() {
        String TABLE_NAME = "TABLE_NAME";
        String REF_TABLE_NAME = "REF_TABLE_NAME";

        String TABLE_ATTRIBUTE = "TABLE_ATTRIBUTE";
        String REF_TABLE_ATTRIBUTE = "REF_TABLE_ATTRIBUTE";

        List<String> TABLE_ATTRIBUTES = List.of(TABLE_ATTRIBUTE);

        ReferentialConstraint referentialConstraint = new ReferentialConstraint(REF_TABLE_NAME);
        referentialConstraint.addAttribute(TABLE_ATTRIBUTE);
        referentialConstraint.addRefAttribute(REF_TABLE_ATTRIBUTE);

        Table table = new Table(TABLE_NAME, TABLE_ATTRIBUTES);
        table.addPrimaryKey(TABLE_ATTRIBUTES);
        table.addReferentialConstraints(List.of(referentialConstraint));

        return table;
    }

    @Test
    void toString_EmptyRelationalModel_Success() {
        String expectedResult = "";
        String actualResult = relationalModel.toString();

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void toString_ValidRelationalModel_Success() {
        Table table = getTable();
        relationalModel.addTable(table);

        String expectedResult =
                "--> TABLE \u001B[33mTABLE_NAME\u001B[0m [TABLE_ATTRIBUTE]\n" +
                "--- primary key [TABLE_ATTRIBUTE]\n" +
                "--- reference TABLE_NAME [TABLE_ATTRIBUTE] => REF_TABLE_NAME [REF_TABLE_ATTRIBUTE]\n";
        String actualResult = relationalModel.toString();

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
