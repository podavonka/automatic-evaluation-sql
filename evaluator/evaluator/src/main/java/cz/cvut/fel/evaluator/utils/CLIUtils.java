package cz.cvut.fel.evaluator.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

/**
 * Contains methods necessary for working with the command line.
 */
@Slf4j
public class CLIUtils {

    /**
     * Reads options and arguments relating to them
     * from command line after the program's startup.
     * <p>
     * Types of the options:
     * -c   Contains path to the file with the evaluation criteria.
     * -rm  Contains path to the file with the relational model.
     * -sql Contains path to the file with the SQL queries.
     * -o   Contains name of the output HTML file.
     *
     * @param args Arguments sent to the program on its startup.
     * @return Command line containing parsed arguments.
     * @throws ParseException While reading the options and the arguments.
     */
    public static CommandLine readOptions(String[] args) throws ParseException {
        log.info("Method read options was called with the arguments " + args);
        Options options = new Options();

        Option criteriaJSON = new Option("c", true, "Criteria configuration");
        criteriaJSON.setRequired(true);
        options.addOption(criteriaJSON);

        Option rmPDF = new Option("rm", true, "Relational model");
        options.addOption(rmPDF);

        Option sqlPDF = new Option("sql", true, "SQL queries");
        sqlPDF.setRequired(true);
        options.addOption(sqlPDF);

        Option outputHTML = new Option("o", true, "Output HTML");
        options.addOption(outputHTML);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        cmd = parser.parse(options, args);
        return cmd;
    }
}
