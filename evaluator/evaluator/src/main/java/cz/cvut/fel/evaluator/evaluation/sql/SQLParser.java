package cz.cvut.fel.evaluator.evaluation.sql;

import cz.cvut.fel.evaluator.evaluation.sql.query.QueryRegexLibrary;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Retrieves from the received text SQL queries
 * similar to the pattern specified using regular expressions.
 */
@Slf4j
public class SQLParser {

    /**
     * Patterns to extract SQL queries from text.
     */
    private final Pattern universalQueryPattern = QueryRegexLibrary.getUniversalQueryPattern();
    private final Pattern dropTableQueryPattern = QueryRegexLibrary.getDropTableQueryPattern();
    private final Pattern createTableQueryPattern = QueryRegexLibrary.getCreateTableQueryPattern();
    private final Pattern insertIntoQueryPattern = QueryRegexLibrary.getInsertIntoQueryPattern();
    private final Pattern selectQueryPattern = QueryRegexLibrary.getSelectQueryPattern();

    /**
     * Extracts the query list from the received text.
     *
     * @param sqlSolution Text containing queries.
     * @return List of queries.
     */
    public List<String> parse(String sqlSolution) {
        log.info("Method parse was called");

        sqlSolution = preprocessSQLSolution(sqlSolution);

        Matcher matcher = universalQueryPattern.matcher(sqlSolution);
        List<String> matches = matcher.results().map(MatchResult::group)
                .collect(Collectors.toList());

        Matcher dropTableMatcher = dropTableQueryPattern.matcher(sqlSolution);
        matches.addAll(dropTableMatcher.results().map(MatchResult::group)
                .collect(Collectors.toList()));

        Matcher createTableMatcher = createTableQueryPattern.matcher(sqlSolution);
        matches.addAll(createTableMatcher.results().map(MatchResult::group)
                .collect(Collectors.toList()));

        Matcher insertIntoMatcher = insertIntoQueryPattern.matcher(sqlSolution);
        matches.addAll(insertIntoMatcher.results().map(MatchResult::group)
                .collect(Collectors.toList()));

        String insertIntoQueryRegex = QueryRegexLibrary.getInsertIntoQueryRegex();
        Matcher selectMatcher = selectQueryPattern.matcher(sqlSolution.replaceAll(insertIntoQueryRegex, ""));
        matches.addAll(selectMatcher.results().map(MatchResult::group)
                .collect(Collectors.toList()));

        return matches;
    }

    /**
     * Makes typographic character substitutions.
     *
     * @param sqlSolution Text containing queries.
     * @return Edited text containing queries.
     */
    private String preprocessSQLSolution(String sqlSolution) {
        return sqlSolution
                .replaceAll("[“”]", "\"")
                .replaceAll("’", "'")
                .replaceAll("\u2028", "\n")
                .replaceAll("\u001E", "FFI")
                .replaceAll("\u001B", "FF");
    }
}
