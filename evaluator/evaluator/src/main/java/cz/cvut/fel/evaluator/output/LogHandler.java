package cz.cvut.fel.evaluator.output;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores logs while executing SQL queries.
 */
@Component
public class LogHandler {

    @Getter
    private final List<String> errorLogs = new ArrayList<>();

    public void addErrorLog(String message) {
        errorLogs.add(message);
    }
}
