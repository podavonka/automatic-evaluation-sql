package cz.cvut.fel.evaluator.output;

import cz.cvut.fel.evaluator.evaluation.Criterion;

import java.util.Map;

/**
 * Prints information about evaluation.
 */
public class TerminalOutput {

    /**
     * Criteria to evaluate student's solution.
     */
    private final Map<String, Criterion> criteria;

    public TerminalOutput(Map<String, Criterion> criteria) {
        this.criteria = criteria;
    }

    /**
     * Prints the criteria and score as a table.
     */
    public void printScoreTable() {
        System.out.println();
        printTableHeader();

        for (String key : criteria.keySet()) {
            if (key.equals("corresponding to the relational model")) continue;
            System.out.format("%40s %7s %12s %15s %14s %11s", key, criteria.get(key).getMaxScore(),
                    Math.round(criteria.get(key).getScore() * 10.0) / 10.0,
                    Math.round(criteria.get(key).getPossibleScore() * 10.0) / 10.0,
                    criteria.get(key).getQueryType(), criteria.get(key).getOccurrences());
            System.out.println();
        }

        printTableFooter();
        System.out.println();
    }

    /**
     * Prints score table's header.
     */
    private void printTableHeader() {
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%40s %10s %14s %15s %11s %13s", "description", "maxScore", "certainScore", "possibleScore", "queryType", "occurrences");
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Prints score table's total statistics.
     */
    private void printTableFooter() {
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%40s", "TOTAL SCORE");
        System.out.format("%8s %12s %15s", criteria.values().stream()
                        .map(Criterion::getMaxScore)
                        .reduce(0, Integer::sum)
                        - criteria.get("corresponding to the relational model").getMaxScore(),
                Math.round(criteria.values().stream()
                        .map(Criterion::getScore)
                        .reduce(0.0, Double::sum) * 10.0) / 10.0,
                Math.round(criteria.values().stream()
                        .map(Criterion::getPossibleScore)
                        .reduce(0.0, Double::sum) * 10.0) / 10.0);
    }
}
