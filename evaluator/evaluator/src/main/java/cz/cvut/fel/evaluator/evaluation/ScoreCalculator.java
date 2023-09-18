package cz.cvut.fel.evaluator.evaluation;

import cz.cvut.fel.evaluator.evaluation.model.RelationalModel;
import cz.cvut.fel.evaluator.evaluation.model.Table;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Counts score for fulfilling the criteria.
 */
public class ScoreCalculator {

    /**
     * The evaluation criteria.
     */
    private final Map<String, Criterion> criteria;

    public ScoreCalculator(Map<String, Criterion> criteria) {
        this.criteria = criteria;
    }

    /**
     * Generates the relational model from tables
     * created by SQL CREATE TABLE statements.
     *
     * @param relationalModel Relational model to edit.
     */
    public void calculateRelationalModel(RelationalModel relationalModel) {
        String key = "corresponding to the relational model";
        Criterion criterion = criteria.get(key);

        relationalModel.getTables().forEach(table -> criterion.addDescription(table.toHTML() + "<br>"));
        for (int i = 0; i < relationalModel.getTables().size(); i++)
            criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using primary key in every table.
     *
     * @param createQueriesCount Number of the executed CREATE TABLE queries.
     */
    public void calculatePrimaryKey(int createQueriesCount) {
        if (createQueriesCount <= 0) return;

        String key = "defining primary keys of tables";
        Criterion criterion = criteria.get(key);

        double score = criterion.getScore() + 1 / (double) createQueriesCount;
        criterion.setScore(score < 0 ? 0 : score);
        criterion.setPossibleScore(score < 0 ? 0 : score);
        criterion.incrementOccurrences();
        criteria.put(key, criterion);
    }

    /**
     * Generates a list of the tables without primary key.
     *
     * @param table Table without primary key.
     */
    public void addTableWithoutPrimaryKey(Table table) {
        if (table == null) return;

        String key = "defining primary keys of tables";
        Criterion criterion = criteria.get(key);

        criterion.addTableName(table.getName());
        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using integrity constraints.
     *
     * @param statement CREATE TABLE statement with integrity constraints.
     */
    public void calculateIntegrityConstraints(String statement) {
        String key = "using integrity constraints";
        Criterion criterion = criteria.get(key);

        double score = criterion.getScore() + 0.5;
        double maxScore = criterion.getMaxScore();
        criterion.setScore(Math.min(score, maxScore));
        criterion.setPossibleScore(Math.min(score, maxScore));

        String query = statement.replaceAll("REFERENCES", "<span>REFERENCES</span>")
                .replaceAll("CHECK", "<span>CHECK</span>")
                .replaceAll("\n", "<br>");
        criterion.addDescription(query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using ON UPDATE/DELETE.
     *
     * @param statement CREATE TABLE statement using ON UPDATE/DELETE.
     */
    public void calculateOnUpdateDelete(String statement) {
        String key = "using ON UPDATE/DELETE for foreign key";
        Criterion criterion = criteria.get(key);

        double score = criterion.getScore() + 1;
        double maxScore = criterion.getMaxScore();
        criterion.setScore(Math.min(score, maxScore));
        criterion.setPossibleScore(Math.min(score, maxScore));

        String description = getTableNameFromStatement(statement);
        criterion.addTableName(description);

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Extracts table name from CREATE TABLE statement.
     *
     * @param statement CREATE TABLE statement.
     * @return Name of the table.
     */
    private String getTableNameFromStatement(String statement) {
        String firstLine = statement.split("\\r?\\n|\\r")[0];

        String lastChar = firstLine.substring(firstLine.length() - 1);
        while (lastChar.equals(" ") || lastChar.equals("(")) {
            firstLine = StringUtils.chop(firstLine);
            lastChar = firstLine.substring(firstLine.length() - 1);
        }

        String[] firstLineSplit = firstLine.split(" ");
        String tableName = firstLineSplit[firstLineSplit.length - 1];

        return tableName;
    }

    /**
     * Calculates score for writing INSERT INTO statements.
     */
    public void calculateInsertInto() {
        String key = "statements to fill tables by data";
        Criterion criterion = criteria.get(key);
        criterion.setScore(criterion.getMaxScore());
        criterion.setPossibleScore(criterion.getMaxScore());
        criterion.incrementOccurrences();
        criteria.put(key, criterion);
    }

    /**
     * Counts OUTER and INNER JOINs' occurrences
     * and calculates score for using them.
     *
     * @param statement SELECT statement with JOIN.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     */
    public void calculateJoin(String statement, boolean isCertain) {
        String outerJoinRegex = "(LEFT|RIGHT|FULL)\\s+(OUTER\\s+)?JOIN";
        Pattern outerJoinPattern = Pattern.compile(outerJoinRegex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher outerJoinMatcher = outerJoinPattern.matcher(statement);

        long joinCount = StringUtils.countMatches(statement, "JOIN");
        long outerJoinCount = outerJoinMatcher.results().count();
        long innerJoinCount = joinCount - outerJoinCount;

        if (outerJoinCount > 0) calculateOuterJoin(statement, isCertain);
        if (innerJoinCount > 0) calculateInnerJoin(statement, isCertain);
    }

    /**
     * Calculates score for using OUTER JOIN in a SELECT statement.
     *
     * @param statement SELECT statement with OUTER JOIN.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     */
    public void calculateOuterJoin(String statement, boolean isCertain) {
        String key = "outer join of tables";
        Criterion criterion = criteria.get(key);

        if (isCertain) criterion.setScore(criterion.getMaxScore());
        criterion.setPossibleScore(criterion.getMaxScore());

        String query = statement
                .replaceAll("((LEFT|RIGHT|FULL)\\s+(OUTER\\s+)?JOIN)", "<span>$1</span>")
                .replaceAll("\n", "<br>");
        criterion.addDescription( query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using INNER JOIN in a SELECT statement.
     *
     * @param statement SELECT statement with INNER JOIN.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     */
    public void calculateInnerJoin(String statement, boolean isCertain) {
        String key = "inner join of tables";
        Criterion criterion = criteria.get(key);

        if (isCertain) criterion.setScore(criterion.getMaxScore());
        criterion.setPossibleScore(criterion.getMaxScore());

        String query = statement
                .replaceAll("INNER\\s+JOIN", "<span>INNER JOIN</span>")
                .replaceAll("JOIN", "<span>JOIN</span>")
                .replaceAll("((LEFT|RIGHT|FULL)\\s+(OUTER\\s+)?|OUTER\\s+)<span>JOIN</span>", "$1JOIN")
                .replaceAll("\n", "<br>");
        criterion.addDescription(query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using condition in a SELECT statement.
     *
     * @param statement SELECT statement with condition.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     */
    public void calculateCondition(String statement, boolean isCertain) {
        String key = "condition on the data";
        Criterion criterion = criteria.get(key);

        if (isCertain) criterion.setScore(criterion.getMaxScore());
        criterion.setPossibleScore(criterion.getMaxScore());

        String query = statement.replaceAll("WHERE", "<span>WHERE</span>")
                .replaceAll("\n", "<br>");
        criterion.addDescription(query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using aggregation in a SELECT statement.
     *
     *  @param statement SELECT statement with aggregation.
     *  @param isCertain true, if the query does not have to be checked manually,
     *                   false, if the query should be checked manually.
     */
    public void calculateAggregation(String statement, boolean isCertain) {
        String key = "aggregation";
        Criterion criterion = setAggregationScore(statement, isCertain, criteria.get(key));

        String query = statement.replaceAll("GROUP BY", "<span>GROUP BY</span>")
                        .replaceAll("\\bHAVING", "<span>HAVING</span>")
                        .replaceAll("\\bCOUNT\\(", "<span>COUNT</span>(")
                        .replaceAll("\\bAVG\\(", "<span>AVG</span>(")
                        .replaceAll("\\bSUM\\(", "<span>SUM</span>(")
                        .replaceAll("\\bMIN\\(", "<span>MIN</span>(")
                        .replaceAll("\\bMAX\\(", "<span>AVG</span>(")
                        .replaceAll("\n", "<br>");
        criterion.addDescription(query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates the score for using aggregation,
     * depending on the way in which it was accomplished.
     *
     * @param statement SELECT statement with aggregation.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     * @param criterion Criterion suspected of fulfilling.
     * @return Criterion with the updated score.
     */
    private Criterion setAggregationScore(String statement, boolean isCertain, Criterion criterion) {
        if (statement.contains("COUNT(") || statement.contains("AVG(") || statement.contains("SUM(") ||
                statement.contains("MIN(") || statement.contains("MAX(")) {

            if (statement.contains("GROUP BY") && statement.contains("HAVING")) {

                if (isCertain) criterion.setScore(criterion.getMaxScore());
                criterion.setPossibleScore(criterion.getMaxScore());
            }

            else if (!(criterion.getDescription().contains("COUNT") || criterion.getDescription().contains("AVG") ||
                    criterion.getDescription().contains("SUM") || criterion.getDescription().contains("MIN")
                    || criterion.getDescription().contains("MAX"))) {

                double score = criterion.getScore() + 1;
                double maxScore = criterion.getMaxScore();
                if (isCertain) criterion.setScore(Math.min(score, maxScore));
                criterion.setPossibleScore(Math.min(score, maxScore));
            }
        }

        else if (statement.contains("GROUP BY") && statement.contains("HAVING") &&
                !(criterion.getDescription().contains("GROUP BY") && criterion.getDescription().contains("HAVING"))) {

            double score = criterion.getScore() + 1;
            double maxScore = criterion.getMaxScore();
            if (isCertain) criterion.setScore(Math.min(score, maxScore));
            criterion.setPossibleScore(Math.min(score, maxScore));
        }

        return criterion;
    }

    /**
     * Calculates score for using sorting and pagination in a SELECT statement.
     *
     * @param statement SELECT statement with sorting and pagination.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     */
    public void calculateSorting(String statement, boolean isCertain) {
        String key = "sorting and pagination";
        Criterion criterion = criteria.get(key);

        if (isCertain) criterion.setScore(criterion.getMaxScore());
        criterion.setPossibleScore(criterion.getMaxScore());

        String query = statement.replaceAll("ORDER BY", "<span>ORDER BY</span>")
                .replaceAll("LIMIT", "<span>LIMIT</span>")
                .replaceAll("OFFSET", "<span>OFFSET</span>")
                .replaceAll("\n", "<br>");
        criterion.addDescription(query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using set operations in a SELECT statement.
     *
     * @param statement SELECT statement with set operations.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     */
    public void calculateSetOperations(String statement, boolean isCertain) {
        String key = "set operations";
        Criterion criterion = criteria.get(key);

        if (isCertain) criterion.setScore(criterion.getMaxScore());
        criterion.setPossibleScore(criterion.getMaxScore());

        String query = statement
                .replaceAll("UNION", "<span>UNION</span>")
                .replaceAll("UNION ALL", "<span>UNION ALL</span>")
                .replaceAll("INTERSECT", "<span>INTERSECT</span>")
                .replaceAll("EXCEPT", "<span>EXCEPT</span>")
                .replaceAll("\n", "<br>");
        criterion.addDescription(query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Calculates score for using inner SELECT in a SELECT statement.
     *
     * @param statement SELECT statement with inner SELECT.
     * @param isCertain true, if the query does not have to be checked manually,
     *                  false, if the query should be checked manually.
     */
    public void calculateInnerSelect(String statement, boolean isCertain) {
        if (!isInnerSelect(statement)) return;

        String key = "inner SELECT";
        Criterion criterion = criteria.get(key);

        if (isCertain) criterion.setScore(criterion.getMaxScore());
        criterion.setPossibleScore(criterion.getMaxScore());

        String query = statement.replaceAll("SELECT", "<span>SELECT</span>")
                .replaceAll("\n", "<br>");
        criterion.addDescription(query + "<br><br>");

        criterion.incrementOccurrences();

        criteria.put(key, criterion);
    }

    /**
     * Checks whether there is inner SELECT usage in the statement.
     *
     * @param statement Statement to check.
     * @return true, if there is at least one inner SELECT,
     *         false, if there is no inner SELECT.
     */
    private boolean isInnerSelect(String statement) {
        int outerSelectsCount = 1;
        outerSelectsCount += StringUtils.countMatches(statement, "UNION");
        outerSelectsCount += StringUtils.countMatches(statement, "INTERSECT");
        outerSelectsCount += StringUtils.countMatches(statement, "EXCEPT");

        int selectsCount = StringUtils.countMatches(statement, "SELECT");
        int innerSelectsCount = selectsCount - outerSelectsCount;

        return innerSelectsCount > 0;
    }
}
