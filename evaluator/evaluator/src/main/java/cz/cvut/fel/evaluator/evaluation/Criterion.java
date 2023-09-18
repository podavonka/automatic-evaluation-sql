package cz.cvut.fel.evaluator.evaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Represents one evaluation criterion.
 */
@Slf4j
@Getter
@NoArgsConstructor
@Component
public class Criterion {

    /**
     * The maximum score that can be obtained
     * by fulfilling this criterion.
     */
    private int maxScore;

    /**
     * The score the student will obtain for completing
     * the current requirement in his or her solution.
     * Score value cannot be greater than maxScore.
     */
    @Setter
    private double score = 0;

    /**
     * Score that the student will theoretically receive
     * if, after checking manually,the suspicious queries
     * turn out to be correct.
     */
    @Setter
    private double possibleScore = 0;

    /**
     * The type of query to which the criterion belongs.
     */
    private String queryType;

    /**
     * Evaluation result explanation.
     */
    private String description = "";

    /**
     * The number of times the criterion was fulfilled.
     */
    @Setter
    private int occurrences = 0;

    /**
     * Adds formatted text to the description.
     *
     * @param descriptionPart A formatted text to add.
     */
    public void addDescription(String descriptionPart) {
        description += descriptionPart;
    }

    /**
     * Adds table name to the description.
     *
     * @param tableName Name of the table which creation fulfilled the criterion.
     */
    public void addTableName(String tableName) {
        description += " [" + tableName + "]";
    }

    /**
     * Increases by one the number of fulfillment of the criterion.
     */
    public void incrementOccurrences() {
        occurrences++;
    }
}
