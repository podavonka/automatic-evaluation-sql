package cz.cvut.fel.evaluator.output;

import cz.cvut.fel.evaluator.evaluation.Criterion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Contains all the information
 * which is necessary for the evaluation output.
 */
@Slf4j
@Getter
public class Output {

    /**
     * Contains criteria for the task.
     */
    private Map<String, Criterion> criteria;

    /**
     * A list of exceptions
     * that occurred during the execution of queries.
     */
    private List<String> logs;

    /**
     * Statistics on read and executed SQL queries.
     */
    private List<String> statistics;

    public Output(Map<String, Criterion> criteria, List<String> logs, List<String> statistics) {
        this.criteria = criteria;
        this.logs = logs;
        this.statistics = statistics;
    }
}
