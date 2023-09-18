package cz.cvut.fel.evaluator.output;

import lombok.Getter;

import java.util.List;

/**
 * Prints statistics about read and executed statements.
 */
@Getter
public class StatisticsOutput {

    /**
     * Statistic about read statements.
     */
    private int createTableQueriesCount = 0;
    private int insertQueriesCount = 0;
    private int selectQueriesCount = 0;

    /**
     * Statistics about executed statements.
     */
    private int executedCreateTableQueriesCount = 0;
    private int executedInsertQueriesCount = 0;
    private int executedSelectQueriesCount = 0;

    /**
     * Generates statistics about reading and executing queries
     * for each query type.
     *
     * @return List with statistics.
     */
    public List<String> generateList() {
        return List.of(
                getQueryStatistics("CREATE TABLE", createTableQueriesCount, executedCreateTableQueriesCount),
                getQueryStatistics("INSERT INTO", insertQueriesCount, executedInsertQueriesCount),
                getQueryStatistics("SELECT", selectQueriesCount, executedSelectQueriesCount));
    }

    /**
     * Connects the query processing data into a sentence.
     *
     * @param queryType Type of the processed query.
     * @param read Read queries count.
     * @param executed Executed queries count.
     * @return String with statistics about current query type.
     */
    private String getQueryStatistics(String queryType, int read, int executed) {
        return queryType + ": " + read + " read / " + executed + " executed<br>";
    }

    /**
     * Prints statistics about read and executed statements.
     * Types of statements: CREATE, INSERT INTO, SELECT.
     */
    public void printAll() {
        print("CREATE", createTableQueriesCount, executedCreateTableQueriesCount);
        print("INSERT", insertQueriesCount, executedInsertQueriesCount);
        print("SELECT", selectQueriesCount, executedSelectQueriesCount);
    }

    /**
     * Prints statistics for the received query type.
     *
     * @param queryType Query type for statistics.
     * @param read The number of queries of this type that were recognized.
     * @param executed The number of queries of this type that were executed.
     */
    public void print(String queryType, int read, int executed) {
        System.out.printf("%6s: %2s read / %2s executed\n", queryType, read, executed);
    }

    /**
     * Increases by one the number of fulfillment of the criterion.
     */
    public void incrementCreateTableQueriesCount() {
        createTableQueriesCount++;
    }

    /**
     * Increases by one the number of fulfillment of the criterion.
     */
    public void incrementInsertQueriesCount() {
        insertQueriesCount++;
    }

    /**
     * Increases by one the number of fulfillment of the criterion.
     */
    public void incrementSelectQueriesCount() {
        selectQueriesCount++;
    }

    /**
     * Increases by one the number of fulfillment of the criterion.
     */
    public void incrementExecutedCreateTableQueriesCount() {
        executedCreateTableQueriesCount++;
    }

    /**
     * Increases by one the number of fulfillment of the criterion.
     */
    public void incrementExecutedInsertQueriesCount() {
        executedInsertQueriesCount++;
    }

    /**
     * Increases by one the number of fulfillment of the criterion.
     */
    public void incrementExecutedSelectQueriesCount() {
        executedSelectQueriesCount++;
    }
}
