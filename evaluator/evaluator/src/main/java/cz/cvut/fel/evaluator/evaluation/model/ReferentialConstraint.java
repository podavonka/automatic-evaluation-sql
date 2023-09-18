package cz.cvut.fel.evaluator.evaluation.model;

import lombok.Getter;

import java.util.*;

/**
 * Represents a referential constraint in a database table.
 */
@Getter
public class ReferentialConstraint {

    private SortedSet<String> attributes;
    private SortedSet<String> refAttributes;
    private String refTable;

    public ReferentialConstraint(String refTable) {
        this.refTable = refTable;
        attributes = new TreeSet<>();
        refAttributes = new TreeSet<>();
    }

    public SortedSet<String> addAttribute(String attribute) {
        attributes.add(attribute);
        return attributes;
    }

    public SortedSet<String> addRefAttribute(String attribute) {
        refAttributes.add(attribute);
        return refAttributes;
    }
}
