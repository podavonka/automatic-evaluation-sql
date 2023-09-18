package cz.cvut.fel.evaluator.output;

import cz.cvut.fel.evaluator.utils.FileUtils;
import cz.cvut.fel.evaluator.evaluation.Criterion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates evaluation output as an HTML file.
 */
@Slf4j
public class HTMLOutput {

    /**
     * Criteria to evaluate student's solution.
     */
    private Map<String, Criterion> criteria = new HashMap<>();

    /**
     * Captured exceptions while the program is running.
     */
    private List<String> logs = new ArrayList<>();

    /**
     * Statistics about reading and executing queries
     * for each query type.
     */
    private List<String> statistics = new ArrayList<>();

    /**
     * Reads a template for the output
     * and adds evaluation information to it.
     *
     * @param output  All the information which is necessary for the evaluation output.
     * @param outputHTML Name of the output HTML file.
     * @throws IOException While reading a file or writing to a file.
     */
    public void generateEvaluationResult(Output output, String outputHTML) throws IOException {
        this.criteria = output.getCriteria();
        this.logs = output.getLogs();
        this.statistics = output.getStatistics();

        String htmlString = FileUtils.readString("/output/template.html", this.getClass());

        htmlString = addStatistics(htmlString);
        htmlString = addScoreTable(htmlString);
        htmlString = addLogTable(htmlString);
        htmlString = addEvaluationInfo(htmlString);

        String outputPath = FileUtils.getProgramPath() + "/"
                + (outputHTML == null ? "output.html" : outputHTML);
        Files.writeString(Path.of(outputPath), htmlString);
    }

    /**
     * Adds statistics about reading and executing queries.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addStatistics(String htmlString) {
        String htmlStatistics = "";
        for (String queryStatistics : statistics) htmlStatistics += queryStatistics;
        if (!htmlStatistics.isEmpty())
            htmlStatistics = StringUtils.chop(StringUtils.chop(StringUtils.chop(StringUtils.chop(htmlStatistics))));
        htmlString = htmlString.replace("$statistics", htmlStatistics);
        return htmlString;
    }

    /**
     * Adds score table with the criteria.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addScoreTable(String htmlString) {
        htmlString = addCriteria(htmlString);
        htmlString = addTotalScore(htmlString);
        return htmlString;
    }

    /**
     * Adds criteria to the score table.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addCriteria(String htmlString) {
        String htmlCriteria = "";

        for (String key : criteria.keySet()) {
            if (key.equals("corresponding to the relational model")) continue;
            Criterion criterion = criteria.get(key);
            htmlCriteria += "\n<tr>\n<th>" + key
                    + "</th>\n<th>" + criterion.getMaxScore()
                    + "</th>\n<th>" + Math.round(criteria.get(key).getScore() * 10.0) / 10.0
                    + "</th>\n<th>" + Math.round(criteria.get(key).getPossibleScore() * 10.0) / 10.0
                    + "</th>\n<th>" + criterion.getQueryType()
                    + "</th>\n<th>" + criterion.getOccurrences() + "</th>\n</tr>";
        }

        htmlString = htmlString.replace("$criteria", htmlCriteria);
        return htmlString;
    }

    /**
     * Adds total result to the score table.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addTotalScore(String htmlString) {
        String htmlTotalMaxScore = String.valueOf(
                criteria.values().stream()
                .map(Criterion::getMaxScore)
                .reduce(0, Integer::sum)
                - criteria.get("corresponding to the relational model").getMaxScore());

        String htmlTotalScore = String.valueOf(
                Math.round(criteria.values().stream()
                .map(Criterion::getScore)
                .reduce(0.0, Double::sum) * 10.0) / 10.0);

        String htmlTotalPossibleScore = String.valueOf(
                Math.round(criteria.values().stream()
                .map(Criterion::getPossibleScore)
                .reduce(0.0, Double::sum) * 10.0) / 10.0);

        htmlString = htmlString.replace("$totalMaxScore", htmlTotalMaxScore);
        htmlString = htmlString.replace("$totalScore", htmlTotalScore);
        htmlString = htmlString.replace("$totalPossibleScore", htmlTotalPossibleScore);
        return htmlString;
    }

    /**
     * Adds exceptions captured while executing queries.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addLogTable(String htmlString) {
        String htmlLogs = "";

        for (String log : logs) {
            htmlLogs += "\n<tr>\n<th class=\"error\">ERROR</th>\n<th>" + log.replaceAll("\\r?\\n|\\r", "<br>") + "</th>\n</tr>";
        }

        htmlString = htmlString.replace("$logs", htmlLogs);
        return htmlString;
    }

    /**
     * Adds additional information about the evaluation process.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addEvaluationInfo(String htmlString) {
        htmlString = addRelationalModel(htmlString);
        htmlString = addPrimaryKeys(htmlString);
        htmlString = addIntegrityConstraints(htmlString);
        htmlString = addOnUpdateDelete(htmlString);
        htmlString = addRequirement(htmlString, "OUTER JOIN", "outer join of tables", "$outerJoin");
        htmlString = addRequirement(htmlString, "INNER JOIN", "inner join of tables", "$innerJoin");
        htmlString = addRequirement(htmlString, "Condition usage", "condition on the data", "$condition");
        htmlString = addRequirement(htmlString, "Aggregation usage", "aggregation", "$aggregation");
        htmlString = addRequirement(htmlString, "Sorting and pagination usage", "sorting and pagination", "$sorting");
        htmlString = addRequirement(htmlString, "Set operations usage", "set operations", "$setOperations");
        htmlString = addRequirement(htmlString, "Inner SELECT usage", "inner SELECT", "$innerSelect");
        return htmlString;
    }

    /**
     * Adds relational model.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addRelationalModel(String htmlString) {
        String htmlRelationalModel = criteria.get("corresponding to the relational model").getDescription();
        htmlRelationalModel = StringUtils.chop(StringUtils.chop(StringUtils.chop(StringUtils.chop(htmlRelationalModel))));
        htmlString = htmlString.replace("$relationalModel", htmlRelationalModel);
        return htmlString;
    }

    /**
     * Adds list of tables which do not contain primary key.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addPrimaryKeys(String htmlString) {
        String htmlPrimaryKeys = "Primary key is defined in every table";
        String noPrimaryKeyTableNames = criteria.get("defining primary keys of tables").getDescription();
        if (!noPrimaryKeyTableNames.isEmpty())
            htmlPrimaryKeys = "Absence of primary key in tables:<span>" + noPrimaryKeyTableNames + "</span>";
         htmlString = htmlString.replace("$primaryKeys", htmlPrimaryKeys);
        return htmlString;
    }

    /**
     * Adds integrity constraints usage statistics
     * and CREATE TABLE statements using these constraints.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addIntegrityConstraints(String htmlString) {
        String htmlIntegrityConstraints = "";
        String statements = criteria.get("using integrity constraints").getDescription();

        int foreignKeyCount = StringUtils.countMatches(statements, "REFERENCES");
        int checkCount = StringUtils.countMatches(statements, "CHECK");
        int uniqueCount = StringUtils.countMatches(statements, "UNIQUE");
        int notNullCount = StringUtils.countMatches(statements, "NOT NULL");

        htmlIntegrityConstraints += "FOREIGN KEY constraint is occurred " + foreignKeyCount + " times<br>";
        htmlIntegrityConstraints += "CHECK constraint is occurred " + checkCount + " times<br>";
        htmlIntegrityConstraints += "UNIQUE constraint is occurred " + uniqueCount + " times<br>";
        htmlIntegrityConstraints += "NOT NULL constraint is occurred " + notNullCount + " times<br><br>";

        if (!statements.isEmpty())
            htmlIntegrityConstraints += StringUtils.chop(StringUtils.chop(StringUtils.chop(StringUtils.chop(statements))));
        htmlString = htmlString.replace("$integrityConstraints", htmlIntegrityConstraints);
        return htmlString;
    }

    /**
     * Adds CREATE TABLE statements using ON UPDATE or/and ON DELETE.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addOnUpdateDelete(String htmlString) {
        String htmlOnUpdateDelete = "ON UPDATE / DELETE usage was not found";
        String onUpdateDeleteTableNames = criteria.get("using ON UPDATE/DELETE for foreign key").getDescription();
        if (!onUpdateDeleteTableNames.isEmpty())
            htmlOnUpdateDelete = "Usage of ON UPDATE / DELETE in tables:<span>" + onUpdateDeleteTableNames + "</span>";
        htmlString = htmlString.replace("$onUpdateDelete", htmlOnUpdateDelete);
        return htmlString;
    }

    /**
     * Adds statements which fulfilled criteria on SELECT queries.
     *
     * @param htmlString Output template.
     * @return Updated output.
     */
    private String addRequirement(String htmlString, String name, String criterionKey, String replacement) {
        String htmlRequirement = name + " was not found";
        String description = criteria.get(criterionKey).getDescription();
        if (!description.isEmpty())
            htmlRequirement = StringUtils.chop(StringUtils.chop(StringUtils.chop(StringUtils.chop(description))));
        htmlString = htmlString.replace(replacement, htmlRequirement);
        return htmlString;
    }
}
