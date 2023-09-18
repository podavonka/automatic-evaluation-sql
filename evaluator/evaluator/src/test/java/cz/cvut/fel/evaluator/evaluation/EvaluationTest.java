package cz.cvut.fel.evaluator.evaluation;


import cz.cvut.fel.evaluator.evaluation.sql.SQLSolutionEvaluator;
import cz.cvut.fel.evaluator.output.HTMLOutput;
import cz.cvut.fel.evaluator.output.Output;
import cz.cvut.fel.evaluator.utils.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EvaluationTest {

    @InjectMocks
    Evaluation evaluation;

    @Mock
    SQLSolutionEvaluator sqlSolutionEvaluator;

    @Mock
    Output output;

    @Mock
    HTMLOutput htmlOutput;

    @Value("${CRITERIA_JSON}")
    private String CRITERIA_JSON;

    MockedStatic<FileUtils> utilities;

    @BeforeEach
    public void setUpMock() {
        utilities = Mockito.mockStatic(FileUtils.class);
    }

    @AfterEach
    public void close() {
        utilities.close();
    }

    @Test
    void run_ValidPathToCriteriaJSON_Success() throws Exception {
        utilities.when(() -> FileUtils.readPDF(Mockito.anyString())).thenReturn("SQL_SOLUTION");

        when(sqlSolutionEvaluator.evaluate(Mockito.any(), Mockito.anyString())).thenReturn(output);
        doNothing().when(htmlOutput).generateEvaluationResult(Mockito.any(), Mockito.any());

        evaluation.run(CRITERIA_JSON, "SQL_PDF", null, null);

        verify(sqlSolutionEvaluator, times(1)).evaluate(any(), eq("SQL_SOLUTION"));
    }

}
