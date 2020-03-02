package dynamicore.analysing;

import java.util.ArrayList;

public class Table {
    private String title;
    private ArrayList<Row> rows;

    public Table(String title) {
        this.title = title;
        this.rows = new ArrayList<>();
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public ArrayList<String> getColumnsName() {
        ArrayList<String> headers = new ArrayList<>();
        if (!rows.isEmpty()) {
            for (Column column : rows.get(0).getColumns()) {
                headers.add(column.getName());
            }
        }
        return headers;
    }

    public int getColumnCount() {
        return getColumnsName().size();
    }

    public void setRows(ArrayList<Row> rows) {
        this.rows = rows;
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void empty(String error) {
        Row row = new Row();
        row.addColumn(new Column("Error", error));
        addRow(row);
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        String line = "----------------------------------------------------";
        builder.append(title).append("\n").append(line).append("\n");
        if (rows.size() > 0) {
            for (Column column : rows.get(0).getColumns()) {
                builder.append(column.getName()).append("\t");
            }
            builder.append("\n");
        }
        for (Row row : rows) {
            for (Column column : row.getColumns()) {
                builder.append(column.getValue()).append("\t");
            }
            builder.append("\n");
        }
        builder.append(line);
        return builder.toString();
    }
}
