package cz.cvut.fel.evaluator.evaluation.sql;

import cz.cvut.fel.evaluator.output.LogHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Executes SQL queries in the database.
 */
@Slf4j
@Component
public class SQLExecutor {

    /**
     * Executes core JDBC workflow, leaving application code
     * to provide SQL and extract results.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Stores logs while executing SQL queries.
     */
    private final LogHandler logHandler;

    /**
     * Attempts to correct syntactic errors in SQL queries.
     */
    private final SQLFixer sqlFixer = new SQLFixer();

    @Autowired
    public SQLExecutor(JdbcTemplate jdbcTemplate, LogHandler logHandler) {
        this.jdbcTemplate = jdbcTemplate;
        this.logHandler = logHandler;
    }

    /**
     * Executes the received query in the H2 database.
     * In case of an error tries to fix it.
     *
     * @param query Query as a string.
     * @param lastQuery Query from the last query execution attempt.
     * @return true, if the attempt was successful,
     *         false, if the attempt was not successful.
     */
    public boolean tryExecute(String query, String lastQuery) {
        try {
            jdbcTemplate.execute(query);
        } catch (BadSqlGrammarException | DataIntegrityViolationException | UncategorizedSQLException e) {
            String message = e.getCause().getMessage();

            if (!query.equals(lastQuery)) return tryExecute(sqlFixer.fix(query, message), query);

            log.error(message + "\n");
            logHandler.addErrorLog(message);

            return false;
        }

        return true;
    }
}
