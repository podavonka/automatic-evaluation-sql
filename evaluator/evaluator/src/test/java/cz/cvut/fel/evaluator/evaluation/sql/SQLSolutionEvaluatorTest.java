package cz.cvut.fel.evaluator.evaluation.sql;

import cz.cvut.fel.evaluator.evaluation.Criterion;
import cz.cvut.fel.evaluator.evaluation.sql.query.TableQueryHandler;
import cz.cvut.fel.evaluator.output.LogHandler;
import cz.cvut.fel.evaluator.output.StatisticsOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SQLSolutionEvaluatorTest {

    @InjectMocks
    SQLSolutionEvaluator sqlSolutionEvaluator;

    @Mock
    SQLParser sqlParser;

    @Mock
    LogHandler logHandler;

    @Mock
    StatisticsOutput statisticsOutput;

    @Mock
    TableQueryHandler tableQueryHandler;

    @Mock
    Map<String, Criterion> criteria;

    @Test
    void evaluate_ValidCriteriaAndSQLSolution_Success() {
        when(sqlParser.parse(Mockito.anyString())).thenReturn(new ArrayList<>());
        when(tableQueryHandler.getTableNames()).thenReturn(new ArrayList<>());

        sqlSolutionEvaluator.evaluate(criteria, "SQL_SOLUTION");

        verify(sqlParser, times(1)).parse(Mockito.anyString());
        verify(logHandler, times(1)).getErrorLogs();
        verify(statisticsOutput ,times(1)).printAll();
        verify(statisticsOutput ,times(1)).generateList();
    }
}
