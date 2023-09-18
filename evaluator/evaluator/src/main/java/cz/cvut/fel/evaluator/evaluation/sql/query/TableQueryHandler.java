package cz.cvut.fel.evaluator.evaluation.sql.query;

import cz.cvut.fel.evaluator.evaluation.model.ReferentialConstraint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes queries to get table parameters from the database.
 */
@Slf4j
@Component
public class TableQueryHandler {

    /**
     * Executes core JDBC workflow, leaving application code
     * to provide SQL and extract results.
     */
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TableQueryHandler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Executes query to get tables' names.
     *
     * @return List of tables' names.
     */
    public List<String> getTableNames() {
        String tableNamesQuery =
                "SELECT TABLE_NAME\n" +
                        "FROM INFORMATION_SCHEMA.TABLES\n" +
                        "WHERE TABLE_CLASS = 'org.h2.mvstore.db.MVTable'";

        List<String> tableNames = jdbcTemplate.queryForList(tableNamesQuery, String.class);
        return tableNames;
    }

    /**
     * Executes query to get table's columns.
     *
     * @param name Name of the table.
     * @return List of the table's columns names.
     */
    public List<String> getColumns(String name) {
        String columnsQuery =
                "SELECT COLUMN_NAME\n" +
                        "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                        "WHERE TABLE_NAME = '" + name + "'";

        List<String> columns = jdbcTemplate.queryForList(columnsQuery, String.class);
        return columns;
    }

    /**
     * Executes query to get table's primary key.
     *
     * @param name Name of the table.
     * @return Primary key which was initialized with a PRIMARY KEY constraint.
     */
    public List<String> getPrimaryKey(String name) {
        String primaryKeyQuery =
                "SELECT COLUMN_NAME\n" +
                        "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS  tc\n" +
                        "JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE  AS ccu USING (CONSTRAINT_SCHEMA, CONSTRAINT_NAME) \n" +
                        "WHERE CONSTRAINT_TYPE = 'PRIMARY KEY' AND tc.TABLE_NAME = '" + name + "'";

        List<String> primaryKey = jdbcTemplate.queryForList(primaryKeyQuery, String.class);
        return primaryKey;
    }

    /**
     * Executes query to get table's primary keys.
     *
     * @param name Name of the table.
     * @return Map with primary keys which were initialized with UNIQUE + NOT NULL constraints.
     */
    public Map<String, List<String>> getUniqueNotNull(String name) {
        String uniqueNotNullQuery =
                "SELECT c.COLUMN_NAME, tc.CONSTRAINT_NAME\n" +
                        "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS  tc\n" +
                        "    JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE  AS ccu USING (CONSTRAINT_SCHEMA, CONSTRAINT_NAME)\n" +
                        "    JOIN INFORMATION_SCHEMA.COLUMNS AS c\n" +
                        "        ON c.TABLE_SCHEMA = tc.CONSTRAINT_SCHEMA AND c.TABLE_NAME = tc.TABLE_NAME AND c.COLUMN_NAME = ccu.COLUMN_NAME\n" +
                        "WHERE CONSTRAINT_TYPE = 'UNIQUE' AND IS_NULLABLE = 'NO' AND tc.TABLE_NAME = '" + name + "'";

        List<Map<String, Object>> uniqueNotNullRows = jdbcTemplate.queryForList(uniqueNotNullQuery);
        Map<String, List<String>> primaryKeys = new HashMap<>();

        for (Map<String, Object> row : uniqueNotNullRows) {
            String column = (String) row.get("COLUMN_NAME");
            String constraintName = (String) row.get("CONSTRAINT_NAME");

            List<String> updatedPrimaryKey = primaryKeys.containsKey(constraintName) ?
                    primaryKeys.get(constraintName) : new ArrayList<>();

            updatedPrimaryKey.add(column);
            primaryKeys.put(constraintName, updatedPrimaryKey);
        }
        return primaryKeys;
    }

    /**
     * Executes query to get table's referential constraints.
     *
     * @param name Name of the table.
     * @return List of the table's referential constraints.
     */
    public List<ReferentialConstraint> getReferentialConstraints(String name) {
        String referentialConstraintsQuery =
                "SELECT rc.CONSTRAINT_NAME, rc.UNIQUE_CONSTRAINT_NAME, UPDATE_RULE, DELETE_RULE,\n" +
                        "         kcu.TABLE_NAME AS SOURCE_TABLE_NAME, kcu.COLUMN_NAME AS SOURCE_COLUMN_NAME," +
                        "         ccu.TABLE_NAME AS REF_TABLE_NAME, ccu.COLUMN_NAME AS REF_COLUMN_NAME\n" +
                        "FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS as rc\n" +
                        "         JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE as kcu\n" +
                        "              ON rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME\n" +
                        "         JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE as ccu\n" +
                        "              ON kcu.CONSTRAINT_NAME = ccu.CONSTRAINT_NAME\n" +
                        "WHERE kcu.TABLE_NAME = '" + name + "'";

        List<Map<String, Object>> referentialConstraintsRows = jdbcTemplate.queryForList(referentialConstraintsQuery);
        Map<String, ReferentialConstraint> referentialConstraints = new HashMap<>();

        for (Map<String, Object> row : referentialConstraintsRows) {
            String sourceColumns = (String) row.get("SOURCE_COLUMN_NAME");
            String refTable = (String) row.get("REF_TABLE_NAME");
            String refColumns = (String) row.get("REF_COLUMN_NAME");
            String constraintName = (String) row.get("CONSTRAINT_NAME");

            if (!refTable.equals(name)) {

                ReferentialConstraint referentialConstraint = referentialConstraints.containsKey(constraintName) ?
                        referentialConstraints.get(constraintName) : new ReferentialConstraint(refTable);

                referentialConstraint.addAttribute(sourceColumns);
                referentialConstraint.addRefAttribute(refColumns);
                referentialConstraints.put(constraintName, referentialConstraint);
            }
        }
        return new ArrayList<>(referentialConstraints.values());
    }
}
