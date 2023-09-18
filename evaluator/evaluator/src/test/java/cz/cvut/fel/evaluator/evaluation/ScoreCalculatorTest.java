package cz.cvut.fel.evaluator.evaluation;

import cz.cvut.fel.evaluator.evaluation.model.ReferentialConstraint;
import cz.cvut.fel.evaluator.evaluation.model.RelationalModel;
import cz.cvut.fel.evaluator.evaluation.model.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ScoreCalculatorTest {

    @Spy
    @InjectMocks
    ScoreCalculator scoreCalculator;

    @Spy
    Map<String, Criterion> criteria = new HashMap<>();

    @Mock
    Criterion criterion;

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
    void calculateRelationalModel_EmptyRelationalModel_Success() {
        RelationalModel relationalModel = new RelationalModel();

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateRelationalModel(relationalModel);

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculateRelationalModel_ValidRelationalModel_Success() {
        Table table = getTable();
        RelationalModel relationalModel = new RelationalModel();
        relationalModel.addTable(table);

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateRelationalModel(relationalModel);

        verify(criterion, times(1)).addDescription(table.toHTML() + "<br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculatePrimaryKey_CreateQueriesCountIs{0}_InnerMethodsCallsCountIs{2}")
    @CsvSource({"1, 1, 1", "0, 0, 0", "-1, 0, 0"})
    void calculatePrimaryKey_Success(int createQueriesCount, int expectedScore, int methodsCallsCount) {
        if (createQueriesCount > 0) when(criterion.getScore()).thenReturn(0.0);
        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculatePrimaryKey(createQueriesCount);

        verify(criterion, times(methodsCallsCount)).setScore(expectedScore);
        verify(criterion, times(methodsCallsCount)).setPossibleScore(expectedScore);
        verify(criterion, times(methodsCallsCount)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult;
        if (createQueriesCount > 0) expectedResult = Arrays.asList(null, criterion);
        else {
            expectedResult = new ArrayList<>();
            expectedResult.add(null);
        }

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void addTableWithoutPrimaryKey_NotNullTable_Success() {
        Table table = getTable();

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.addTableWithoutPrimaryKey(table);

        verify(criterion, times(1)).addTableName(table.getName());

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculateIntegrityConstraints_ValidStatement_Success() {
        String statement = "STATEMENT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getScore()).thenReturn(0.0);
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateIntegrityConstraints(statement);

        verify(criterion, times(1)).setScore(0.5);
        verify(criterion, times(1)).setPossibleScore(0.5);
        verify(criterion, times(1)).addDescription(statement + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculateOnUpdateDelete_ValidStatement_Success() {
        String statement = "STATEMENT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getScore()).thenReturn(0.0);
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateOnUpdateDelete(statement);

        verify(criterion, times(1)).setScore(1);
        verify(criterion, times(1)).setPossibleScore(1);
        verify(criterion, times(1)).addTableName("STATEMENT");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculateOnUpdateDelete_ValidCriteria_UpdatedCriteria() {
        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateInsertInto();

        verify(criterion, times(1)).setScore(2);
        verify(criterion, times(1)).setPossibleScore(2);
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculateJoin_StatementWithoutJoinAndIsCertainIsTrue_Success() {
        String statement = "STATEMENT";
        boolean isCertain = true;

        scoreCalculator.calculateJoin(statement, isCertain);

        verify(scoreCalculator, times(0)).calculateOuterJoin(statement, isCertain);
        verify(scoreCalculator, times(0)).calculateInnerJoin(statement, isCertain);
    }

    @ParameterizedTest(name = "calculateOuterJoin_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateOuterJoin_ValidStatement_Success(boolean isCertain) {
        String statement = "STATEMENT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateOuterJoin(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(2);
        else verify(criterion, times(0)).setScore(2);
        verify(criterion, times(1)).setPossibleScore(2);
        verify(criterion, times(1)).addDescription(statement + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateInnerJoin_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateInnerJoin_ValidStatement_Success(boolean isCertain) {
        String statement = "STATEMENT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateInnerJoin(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(2);
        else verify(criterion, times(0)).setScore(2);
        verify(criterion, times(1)).setPossibleScore(2);
        verify(criterion, times(1)).addDescription(statement + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateCondition_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateCondition_ValidStatement_Success(boolean isCertain) {
        String statement = "STATEMENT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(1);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateCondition(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(1);
        else verify(criterion, times(0)).setScore(1);
        verify(criterion, times(1)).setPossibleScore(1);
        verify(criterion, times(1)).addDescription(statement + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateAggregation_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateAggregation_ValidStatementWithGroupByHaving_Success(boolean isCertain) {
        String statement = "GROUP BY HAVING";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getDescription()).thenReturn("");
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateAggregation(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(1);
        else verify(criterion, times(0)).setScore(1);
        verify(criterion, times(1)).setPossibleScore(1);
        verify(criterion, times(1)).addDescription(statement
                .replaceAll("GROUP BY", "<span>GROUP BY</span>")
                .replaceAll("\\bHAVING", "<span>HAVING</span>") + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateAggregation_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateAggregation_ValidStatementWithCount_Success(boolean isCertain) {
        String statement = "COUNT(*)";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getDescription()).thenReturn("");
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateAggregation(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(1);
        else verify(criterion, times(0)).setScore(1);
        verify(criterion, times(1)).setPossibleScore(1);
        verify(criterion, times(1)).addDescription(statement
                .replaceAll("\\bCOUNT", "<span>COUNT</span>") + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateAggregation_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateAggregation_ValidStatementWithGroupByHavingCount_Success(boolean isCertain) {
        String statement = "GROUP BY HAVING COUNT(*)";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateAggregation(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(2);
        else verify(criterion, times(0)).setScore(2);
        verify(criterion, times(1)).setPossibleScore(2);
        verify(criterion, times(1)).addDescription(statement
                .replaceAll("GROUP BY", "<span>GROUP BY</span>")
                .replaceAll("\\bHAVING", "<span>HAVING</span>")
                .replaceAll("\\bCOUNT", "<span>COUNT</span>") + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateSorting_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateSorting_ValidStatement_Success(boolean isCertain) {
        String statement = "STATEMENT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(1);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateSorting(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(1);
        else verify(criterion, times(0)).setScore(1);
        verify(criterion, times(1)).setPossibleScore(1);
        verify(criterion, times(1)).addDescription(statement + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateSetOperations_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateSetOperations_ValidStatement_Success(boolean isCertain) {
        String statement = "STATEMENT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(1);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateSetOperations(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(1);
        else verify(criterion, times(0)).setScore(1);
        verify(criterion, times(1)).setPossibleScore(1);
        verify(criterion, times(1)).addDescription(statement + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest(name = "calculateInnerSelect_ValidStatementAndIsCertainIs{0}_Success")
    @ValueSource(booleans =  {true, false})
    void calculateInnerSelect_ValidStatement_Success(boolean isCertain) {
        String statement = "SELECT SELECT";

        when(criteria.get(Mockito.anyString())).thenReturn(criterion);
        when(criterion.getMaxScore()).thenReturn(2);
        when(criteria.put(Mockito.anyString(), eq(criterion))).thenCallRealMethod();

        scoreCalculator.calculateInnerSelect(statement, isCertain);

        if (isCertain) verify(criterion, times(1)).setScore(2);
        else verify(criterion, times(0)).setScore(2);
        verify(criterion, times(1)).setPossibleScore(2);
        verify(criterion, times(1)).addDescription(statement.replaceAll("SELECT", "<span>SELECT</span>") + "<br><br>");
        verify(criterion, times(1)).incrementOccurrences();

        List<Criterion> actualResult = new ArrayList<>(criteria.values());
        List<Criterion> expectedResult = Arrays.asList(null, criterion);

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
