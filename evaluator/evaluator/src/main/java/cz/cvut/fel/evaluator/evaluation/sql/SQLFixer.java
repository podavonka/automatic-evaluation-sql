package cz.cvut.fel.evaluator.evaluation.sql;

import org.apache.commons.lang3.StringUtils;

/**
 * Attempts to correct syntactic errors in SQL queries.
 */
public class SQLFixer {

    /**
     * Analyzes if the query contains known errors.
     *
     * @param query Query to fix as a string.
     * @param message Exception on the current query execution attempt.
     * @return Edited query.
     */
    public String fix(String query, String message) {
        System.out.println(message + "\n");
        if (message.contains("Column") && message.contains("not found"))
            return fixColumnNotFound(query, message);
        if (message.contains("Cannot parse \"INTERVAL\" constant"))
            return fixCannotParseIntervalConstant(query);
        if (message.contains("CREATE SEQUENCE") && (message.contains("START") || message.contains("INCREMENT")))
            return fixSequenceCreation(query);
        return query;
    }

    /**
     * Tries to fix column not found error.
     *
     * @param query Query to fix as a string.
     * @param message Exception on the current query execution attempt.
     * @return true, if the attempt was successful,
     *         false, if the attempt was not successful.
     */
    private String fixColumnNotFound(String query, String message) {
        String columnName = StringUtils.substringBetween(message, "\"");
        return query
                .replaceFirst("\\bCOUNT\\s*\\(\\s*" + columnName + "\\s*\\)", "COUNT(" + columnName + ".*)")
                .replaceAll("JOIN\\s+(\\w+)\\s+(AS\\s+\\w+\\s+)?USING\\s*\\(\\s*[^()]+\\s*\\)", "NATURAL JOIN $1 $2");
    }

    /**
     * Tries to fix error with INTERVAL constant.
     *
     * @param query Query to fix as a string.
     * @return true, if the attempt was successful,
     *         false, if the attempt was not successful.
     */
    private String fixCannotParseIntervalConstant(String query) {
        return query
                .replaceAll("INTERVAL\\s+['\"](\\d+)\\s+(YEAR|MONTH|DAY|HOUR|MINUTE|SECOND)S?[\"']", "INTERVAL '$1' $2");
    }

    /**
     * Tries to fix sequence creation error.
     *
     * @param query Query to fix as a string.
     * @return true, if the attempt was successful,
     *         false, if the attempt was not successful.
     */
    private String fixSequenceCreation(String query) {
        return query
                .replaceAll("START(?!\\s+WITH)", "START WITH")
                .replaceAll("INCREMENT(?!\\s+BY)" ,"INCREMENT BY");
    }
}
