package cz.cvut.fel.evaluator.evaluation.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a table in a relational model.
 */
@Getter
public class Table {

    private final String name;
    private final List<String> attributes;
    private final List<PrimaryKey> primaryKeys = new ArrayList<>();
    private final List<ReferentialConstraint> referentialConstraints = new ArrayList<>();

    public Table(String name, List<String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    /**
     * Adds a new primary key to the list of table's primary keys.
     *
     * @param primaryKey Primary key to add.
     * @return Updated list of table's primary keys.
     */
    public List<PrimaryKey> addPrimaryKey(List<String> primaryKey) {
        primaryKeys.add(new PrimaryKey(primaryKey));
        return primaryKeys;
    }

    /**
     * Adds several new referential constraints to the list of table's referential constraints.
     *
     * @param referentialConstraint List of referential constraints to add.
     * @return Updated list of table's referential constraints.
     */
    public List<ReferentialConstraint> addReferentialConstraints(List<ReferentialConstraint> referentialConstraint) {
        referentialConstraints.addAll(referentialConstraint);
        return referentialConstraints;
    }

    /**
     * Converts table description got from toString() method to HTML format.
     *
     * @return String with table information as HTML.
     */
    public String toHTML() {
        return this.toString().replace("\u001B[33m", "<span>")
                .replace("\u001B[0m", "</span>")
                .replaceAll("\n", "<br>");
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder("--> TABLE \u001B[33m" + name + "\u001B[0m " + attributes + "\n");

        primaryKeys.forEach(primaryKey -> info.append("--- primary key ").append(primaryKey.getAttributes()).append("\n"));
        referentialConstraints.forEach(rc -> info.append("--- reference ").append(name).append(" ")
                                                 .append(rc.getAttributes()).append(" => ").append(rc.getRefTable())
                                                 .append(" ").append(rc.getRefAttributes()).append("\n"));

        return info.toString();
    }
}
