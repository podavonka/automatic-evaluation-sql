package cz.cvut.fel.evaluator.evaluation.sql.query;

import lombok.Getter;

import java.util.regex.Pattern;

/**
 * Contains regular expressions to extract SQL queries from text.
 */
public class QueryRegexLibrary {

    /**
     * Regular expression for infrequently used queries:
     * CREATE SEQUENCE, ALTER TABLE and DELETE FROM.
     */
    @Getter
    private static final String universalQueryRegex =
            "\\b(CREATE\\s+SEQUENCE|ALTER\\s+TABLE|DELETE\\s+FROM)\\b(?:[^;']|(?:'[^']+')){1,1400};";

    /**
     * Pattern to extract queries that match a regular expression
     * for CREATE SEQUENCE, ALTER TABLE and DELETE FROM queries.
     */
    @Getter
    private static final Pattern universalQueryPattern = Pattern.compile(universalQueryRegex, Pattern.MULTILINE | Pattern.DOTALL);


    /**
     * Regular expression for DROP TABLE query.
     */
    @Getter
    private static final String dropTableQueryRegex =
            "\\bDROP\\s+TABLE\\s+(IF\\s+EXISTS\\s+)?([\\w-]+|['\"](\\s*[\\w-]+)+['\"]);";

    /**
     * Pattern to extract queries that match
     * a regular expression DROP TABLE query.
     */
    @Getter
    private static final Pattern dropTableQueryPattern = Pattern.compile(dropTableQueryRegex, Pattern.MULTILINE | Pattern.DOTALL);


    private static final String referentialActionRegex =
            "\\s+ON\\s+(DELETE|UPDATE)\\s+(CASCADE|SET\\s+NULL|SET\\s+DEFAULT|NO\\s+ACTION|RESTRICT)";

    private static final String checkExpressionRegex = "([^()]+|\\([^)]+\\))+";

    private static final String columnConstraintRegex =
            "\\s*(CONSTRAINT\\s+([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\s+)?" +
            "(NOT\\s+NULL|UNIQUE|PRIMARY\\s+KEY|" +
            "REFERENCES\\s+([\\w-]+|['\"](\\s*[\\w-]+)+['\"])(\\s*\\(\\s*([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\s*\\))?(" + referentialActionRegex + ")*|" +
            "CHECK\\s*\\(" + checkExpressionRegex + "\\))";

    private static final String columnNamesRegex =
            "\\(\\s*([[\\w-]-]+|['\"](\\s*[[\\w-]-]+)+['\"])\\s*(,\\s*([[\\w-]-]+|['\"](\\s*[\\w-]+)+['\"])\\s*)*\\)";

    private static final String tableConstraintRegex =
            "\\s*(CONSTRAINT\\s+([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\s+)?" +
            "(UNIQUE\\s*" + columnNamesRegex + "|PRIMARY\\s+KEY\\s*" + columnNamesRegex + "|" +
            "FOREIGN\\s+KEY\\s*" + columnNamesRegex + "\\s+REFERENCES\\s+([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\s*(" + columnNamesRegex + ")?(" + referentialActionRegex + ")*|" +
            "CHECK\\s*\\(" + checkExpressionRegex + "\\))";

    private static final String columnDefinitionRegex =
            "\\s*(?!CONSTRAINT|UNIQUE|PRIMARY|FOREIGN|CHECK)([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\s+([\\w-]+(\\s*\\(\\s*\\d+\\s*(,\\s*\\d+\\s*)*\\))*)" +
            "(\\s+(" + columnConstraintRegex + "|DEFAULT\\s+([\\w-()]+|nextval\\('?[^']+'?\\))))*";

    /**
     * Detailed regular expression for CREATE TABLE query.
     */
    @Getter
    private static final String createTableDetailedQueryRegex =
            "\\bCREATE\\s+TABLE\\s+(IF\\s+NOT\\s+EXISTS\\s+)?([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\s*\\(" +
            "(" + columnDefinitionRegex + ")" +
            "(\\s*," + columnDefinitionRegex + ")*" +
            "(\\s*," + tableConstraintRegex + ")*" +
            "\\s*\\)";

    /**
     * Pattern to extract queries that match
     * a detailed regular expression CREATE TABLE query.
     */
    @Getter
    private static final Pattern createTableDetailedQueryPattern = Pattern.compile(createTableDetailedQueryRegex, Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * Regular expression for CREATE TABLE query.
     */
    @Getter
    private static final String createTableQueryRegex =
            "\\bCREATE\\s+TABLE\\s+(IF\\s+NOT\\s+EXISTS\\s+)?" +
            "['\"]?([\\w-]+.)?([\\w-]+|['\"](\\s*[\\w-]+)+['\"])['\"]?\\s*\\(" +
            "(?:[^;']|(?:'[^']*')){1,1400};";

    /**
     * Pattern to extract queries that match
     * a regular expression CREATE TABLE query.
     */
    @Getter
    private static final Pattern createTableQueryPattern = Pattern.compile(createTableQueryRegex, Pattern.MULTILINE | Pattern.DOTALL);


    /**
     * Regular expression for INSERT INTO query.
     */
    @Getter
    private static final String insertIntoQueryRegex =
            "\\bINSERT\\s+INTO\\s+([\\w-]+|['\"](\\s*[\\w-]+)+['\"])(?:[^;']|'[^']+'){1,1400};";

    /**
     * Pattern to extract queries that match
     * a regular expression INSERT INTO query.
     */
    @Getter
    private static final Pattern insertIntoQueryPattern = Pattern.compile(insertIntoQueryRegex, Pattern.MULTILINE | Pattern.DOTALL);


    private static final String selectColumnRegex =
            "(" +
                "(([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\.)?(\\*|([\\w-]+|['\"](\\s*[\\w-]+)+['\"]))|" +
                "[\\w-]+\\s*\\(\\s*(([\\w-]+|['\"](\\s*[\\w-]+)+['\"])\\.)?(\\*|([\\w-]+|['\"](\\s*[\\w-]+)+['\"]))\\s*\\))" +
            "(\\s+(AS\\s+)?[\\w-]+)?";

    private static final String selectColumnUnionRegex =
            selectColumnRegex + "(\\s+\\|\\|\\s+(" + selectColumnRegex + "|[\"']?[^\"']*[\"']?))*";

    /**
     * Regular expression for SELECT query.
     */
    @Getter
    private static final String selectQueryRegex =
            "\\bSELECT\\s+(ALL\\s+|DISTINCT\\s+)?" +
            selectColumnUnionRegex + "(\\s*,\\s*" + selectColumnUnionRegex + ")*" +
            "\\s+FROM(?:[^;']|'[^']*'){1,1300};";

    /**
     * Pattern to extract queries that match
     * a regular expression SELECT query.
     */
    @Getter
    private static final Pattern selectQueryPattern = Pattern.compile(selectQueryRegex, Pattern.MULTILINE | Pattern.DOTALL);
}
