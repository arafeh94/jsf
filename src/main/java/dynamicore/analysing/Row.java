package dynamicore.analysing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Row implements Serializable {
    private ArrayList<Column> columns;

    public Row() {
        this.columns = new ArrayList<>();
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public void addColumnAt(int index, Column column) {
        this.columns.add(index, column);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        for (Column column : this.columns) {
            builder.append(column.toString()).append(",");
        }
        builder.append("}");
        return builder.toString();
    }
}
