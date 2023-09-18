package cz.cvut.fel.evaluator.evaluation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.evaluator.utils.FileUtils;
import cz.cvut.fel.evaluator.output.TerminalOutput;
import cz.cvut.fel.evaluator.evaluation.sql.SQLSolutionEvaluator;
import cz.cvut.fel.evaluator.output.HTMLOutput;
import cz.cvut.fel.evaluator.output.Output;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Manages the evaluation process.
 */
@Slf4j
@NoArgsConstructor
@Component
public class Evaluation {

    /**
     * Manages the processing of SQL queries.
     */
    private SQLSolutionEvaluator sqlSolutionEvaluator;

    /**
     * The evaluation criteria.
     */
    private Map<String, Criterion> criteria;

    /**
     * Generates evaluation output as HTML file.
     */
    private HTMLOutput htmlOutput = new HTMLOutput();

    @Autowired
    public Evaluation(SQLSolutionEvaluator sqlSolutionEvaluator) {
        this.sqlSolutionEvaluator = sqlSolutionEvaluator;
    }

    /**
     * Manages the processing of the student's solution and evaluation.
     *
     * @param criteriaJSON Path to JSON file containing evaluation criteria.
     * @param sqlPDF Path to PDF file containing student's solution with SQL queries.
     * @param rmPDF Path to PDF file containing relational model.
     * @param outputHTML Output HTML file name.
     *
     * @throws IOException When reading a file.
     */
    public void run(String criteriaJSON, String sqlPDF, String rmPDF, String outputHTML) throws IOException {
        log.info("Evaluation process has been started");
        initCriteria(criteriaJSON);

        String sqlSolution = FileUtils.readPDF(sqlPDF);
        Output output = sqlSolutionEvaluator.evaluate(criteria, sqlSolution);

        printResult(output, outputHTML);
    }

    /**
     * Initializes the evaluation criteria from a file.
     * Each criterion is assigned a name and the maximum score for its fulfillment.
     *
     * @param criteriaJSON Path to JSON file containing evaluation criteria.
     * @throws IOException When reading a JSON file.
     */
    private void initCriteria(String criteriaJSON) throws IOException {
        String json = Files.readString(Paths.get(criteriaJSON));
        ObjectMapper mapper = new ObjectMapper();
        criteria = mapper.readValue(json, new TypeReference<>() {});

        log.info("The evaluation criteria have been initialized");
    }

    /**
     * Prints the result of the evaluation.
     *
     * @param output Object containing information about
     *               how the evaluation process ended
     * @param outputHTML Output HTML file name.
     */
    private void printResult(Output output, String outputHTML) throws IOException {
        log.info("Method printResult was called");

        TerminalOutput terminalOutput = new TerminalOutput(criteria);
        terminalOutput.printScoreTable();

        htmlOutput.generateEvaluationResult(output, outputHTML);
    }
}
