package cz.cvut.fel.evaluator.evaluation.sql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SQLExecutorTest {

    SQLExecutor sqlExecutor;

    @Autowired
    public SQLExecutorTest(SQLExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    @Test
    void execute_ValidQuery_Success() {
        boolean expectedResult = true;
        boolean actualResult = sqlExecutor.tryExecute("SELECT * FROM TABLE_NAME_1", "");

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void execute_InvalidQuery_Failure() {
        boolean expectedResult = false;
        boolean actualResult = sqlExecutor.tryExecute("SELECT TABLE_NAME_1", "");

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
