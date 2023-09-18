package cz.cvut.fel.evaluator.evaluation.model;

import lombok.Getter;

import java.util.List;

/**
 * Represents a primary key constraint in a database table.
 */
@Getter
public class PrimaryKey {

    private final List<String> attributes;

    public PrimaryKey(List<String> attributes) {
        this.attributes = attributes;
    }
}
