package cz.cvut.fel.evaluator.evaluation.sql;

import cz.cvut.fel.evaluator.evaluation.Criterion;
import cz.cvut.fel.evaluator.evaluation.ScoreCalculator;
import cz.cvut.fel.evaluator.evaluation.model.RelationalModel;

import cz.cvut.fel.evaluator.evaluation.model.ReferentialConstraint;
import cz.cvut.fel.evaluator.evaluation.model.Table;
import cz.cvut.fel.evaluator.evaluation.sql.query.TableQueryHandler;
import cz.cvut.fel.evaluator.output.LogHandler;
import cz.cvut.fel.evaluator.output.Output;
import cz.cvut.fel.evaluator.output.StatisticsOutput;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Manages the processing of SQL queries.
 */
@Slf4j
@Component
public class SQLSolutionEvaluator {

    /**
     * Executes SQL queries in the database.
     */
    private final SQLExecutor sqlExecutor;

    /**
     * Calculator for the criteria.
     */
    private ScoreCalculator scoreCalculator;

    /**
     * Captured exceptions while the program is running.
     */
    private LogHandler logHandler;

    /**
     * Relational model created on the basis of CREATE TABLE queries.
     */
    private final RelationalModel relationalModel = new RelationalModel();

    /**
     * Processes queries to get table parameters from the database.
     */
    private TableQueryHandler tableQueryHandler;

    /**
     * Retrieves from the received text SQL queries
     * similar to the pattern specified using regular expressions.
     */
    private SQLParser sqlParser = new SQLParser();

    /**
     * Generates statistics on read and executed SQL queries.
     */
    private StatisticsOutput statisticsOutput = new StatisticsOutput();

    @Autowired
    public SQLSolutionEvaluator(SQLExecutor sqlExecutor, TableQueryHandler tableQueryHandler, LogHandler logHandler) {
        this.sqlExecutor = sqlExecutor;
        this.tableQueryHandler = tableQueryHandler;
        this.logHandler = logHandler;
    }

    /**
     * Manages the evaluation process of SQL queries.
     * Generates a list of queries from the received text
     * and starts processing and evaluation.
     *
     * @param sqlSolution The text of the solution to the task with SQL queries.
     * @return Output containing SQL queries evaluation result.
     */
    public Output evaluate(Map<String, Criterion> setCriteria, String sqlSolution) {
        log.info("Method evaluate was called");

        scoreCalculator = new ScoreCalculator(setCriteria);

        List<String> queries = sqlParser.parse(sqlSolution);
        executeAll(queries);

        statisticsOutput.printAll();
        return new Output(setCriteria, logHandler.getErrorLogs(), statisticsOutput.generateList());
    }

    /**
     * Executes SQL queries in the defined order:
     * 1. DROP TABLE, CREATE SEQUENCE and CREATE TABLE queries to create database,
     * 2. Other queries that need the existence of tables in the database.
     * Then processes parameters of the tables in the database.
     *
     * @param queries List of queries to execute.
     */
    private void executeAll(List<String> queries) {
        queries.stream()
                .filter(query -> query.startsWith("CREATE TABLE") || query.startsWith("DROP TABLE") || query.startsWith("CREATE SEQUENCE"))
                .forEach(this::execute);
        queries.stream()
                .filter(query -> !query.startsWith("CREATE TABLE") && !query.startsWith("DROP TABLE") && !query.startsWith("CREATE SEQUENCE"))
                .forEach(this::execute);
        processTables();
        if (queries.stream().anyMatch(query -> query.startsWith("CREATE TABLE")))
            scoreCalculator.calculateRelationalModel(relationalModel);
    }

    /**
     * Prepares and executes the received query.
     *
     * @param query Query as a string.
     */
    private void execute(String query) {
        log.info("Method execute was called with query:\n" + "\u001B[32m" + query + "\u001B[0m");

        query = preprocessQuery(query);
        if (sqlExecutor.tryExecute(query, "")) postprocessQuery(query);
        else if (query.startsWith("SELECT")) processSelect("<span class=\"uncertain\">" + query + "</span>");
    }

    /**
     * Increments read queries count.
     * Fixes an SQL query syntax to be compatible with the H2 database.
     *
     * @param query Query as a string.
     * @return Edited query.
     */
    private String preprocessQuery(String query) {
        if (query.startsWith("CREATE TABLE")) statisticsOutput.incrementCreateTableQueriesCount();
        else if (query.startsWith("INSERT")) statisticsOutput.incrementInsertQueriesCount();
        else if (query.startsWith("SELECT")) statisticsOutput.incrementSelectQueriesCount();

        return query
                .replaceAll("\\b(\\d{4})\\.(\\d{2})\\.(\\d{2})\\b", "$1-$2-$3")
                .replaceAll("\\bWITH\\b\\s*\\(\\s*\\bOIDS\\b\\s*=\\s*\\bFALSE\\b\\s*\\)", "")
                .replaceAll("(PRIMARY KEY)(\\s+)(\\w+\\s+)*(DEFAULT NEXTVAL\\('[^']+'\\))", "$4$2$3$1");
    }

    /**
     * Determines the type of the received query
     * and starts the processing based on this.
     *
     * @param query Query as a string.
     */
    private void postprocessQuery(String query) {
        log.info("Query was successfully executed");
        if (query.startsWith("CREATE TABLE")) processCreateTable(query);
        else if (query.startsWith("INSERT")) processInsert();
        else if (query.startsWith("SELECT")) processSelect(query);
    }

    /**
     * Processes CREATE TABLE statement.
     * Looks for the fulfillment of the task criteria in the SQL queries.
     *
     * @param query CREATE TABLE statement to create a table in the database.
     */
    private void processCreateTable(String query) {
        log.info("Method processCreate was called");
        statisticsOutput.incrementExecutedCreateTableQueriesCount();

        if (query.contains("ON UPDATE") || query.contains("ON DELETE"))
            scoreCalculator.calculateOnUpdateDelete(query);

        if (query.contains("REFERENCES") || query.contains("CHECK") ||
                query.contains("UNIQUE") || query.contains("NOT NULL"))
            scoreCalculator.calculateIntegrityConstraints(query);
    }

    /**
     * Processes INSERT statement.
     */
    private void processInsert() {
        log.info("Method processInsert was called");
        statisticsOutput.incrementExecutedInsertQueriesCount();

        scoreCalculator.calculateInsertInto();
    }

    /**
     * Processes SELECT statement.
     * Looks for the fulfillment of the task criteria in the SQL queries.
     *
     * @param query SELECT statement to query.
     */
    private void processSelect(String query) {
        log.info("Method processSelect was called");

        boolean isCertain = !query.contains("<span class=\"uncertain\">");
        if (isCertain) statisticsOutput.incrementExecutedSelectQueriesCount();

        if (query.contains("JOIN"))
            scoreCalculator.calculateJoin(query, isCertain);

        if (query.contains("WHERE"))
            scoreCalculator.calculateCondition(query, isCertain);

        if (query.contains("COUNT(") || query.contains("AVG(") || query.contains("SUM(") ||
                query.contains("MIN(") || query.contains("MAX(") ||
                (query.contains("GROUP BY") && query.contains("HAVING")))
            scoreCalculator.calculateAggregation(query, isCertain);

        if (query.contains("ORDER BY") || query.contains("LIMIT") || query.contains("OFFSET"))
            scoreCalculator.calculateSorting(query, isCertain);

        if (query.contains("UNION") || query.contains("INTERSECT") || query.contains("EXCEPT"))
            scoreCalculator.calculateSetOperations(query, isCertain);

        scoreCalculator.calculateInnerSelect(query, isCertain);
    }

    /**
     * Gets the names of the tables created
     * in the database to analyze them in detail.
     */
    private void processTables() {
        log.info("Method processTables was called");
        List<String> tableNames = tableQueryHandler.getTableNames();
        tableNames.forEach(this::processTable);
    }

    /**
     * Queries details about a table and adds it
     * to the relational model based on the parameters obtained.
     *
     * @param name Table name.
     */
    private void processTable(String name) {
        log.info("Method processTable was called with the table name " + name);
        List<String> columns = tableQueryHandler.getColumns(name);
        Table table = new Table(name, columns);

        List<String> primaryKey = tableQueryHandler.getPrimaryKey(name);
        if (!primaryKey.isEmpty()) {
            table.addPrimaryKey(primaryKey);
            scoreCalculator.calculatePrimaryKey(statisticsOutput.getCreateTableQueriesCount());
        } else scoreCalculator.addTableWithoutPrimaryKey(table);
        log.info("Primary keys processing is done");

        Map<String, List<String>> primaryKeys = tableQueryHandler.getUniqueNotNull(name);
        primaryKeys.keySet().forEach(key -> table.addPrimaryKey(primaryKeys.get(key)));
        log.info("Unique and not null combinations processing is done");

        List<ReferentialConstraint> referentialConstraints = tableQueryHandler.getReferentialConstraints(name);
        table.addReferentialConstraints(referentialConstraints);
        log.info("Referential constraints processing is done");

        relationalModel.addTable(table);
    }
}
