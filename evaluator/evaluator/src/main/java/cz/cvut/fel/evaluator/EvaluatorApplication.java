package cz.cvut.fel.evaluator;

import cz.cvut.fel.evaluator.utils.CLIUtils;
import cz.cvut.fel.evaluator.evaluation.Evaluation;
import org.apache.commons.cli.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * Launches the evaluator application.
 */
@SpringBootApplication
public class EvaluatorApplication {

    private static Evaluation evaluation;

    public EvaluatorApplication(Evaluation evaluation) {
        EvaluatorApplication.evaluation = evaluation;
    }

    public static void main(String[] args) throws ParseException, IOException {
        SpringApplication.run(EvaluatorApplication.class, args);

        CommandLine cmd = CLIUtils.readOptions(args);
        evaluation.run(cmd.getOptionValue("c"), cmd.getOptionValue("sql"), cmd.getOptionValue("rm"), cmd.getOptionValue("o"));
    }
}
