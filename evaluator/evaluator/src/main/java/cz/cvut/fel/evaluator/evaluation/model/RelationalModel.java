package cz.cvut.fel.evaluator.evaluation.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains a list of tables in a relational model.
 */
@Getter
public class RelationalModel {

    private List<Table> tables = new ArrayList<>();

    /**
     * Adds a new table to the relational model.
     *
     * @param table Table to add.
     * @return Updated list of tables.
     */
    public List<Table> addTable(Table table) {
        tables.add(table);
        return tables;
    }

    @Override
    public String toString() {
        String info = tables.stream().map(Table::toString).collect(Collectors.joining());
        return info;
    }
}
