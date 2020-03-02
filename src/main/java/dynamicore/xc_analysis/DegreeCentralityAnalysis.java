package dynamicore.xc_analysis;

import com.arafeh.jsf.core.utils.Pair;
import com.arafeh.jsf.model.Node;
import dynamicore.analysing.Column;
import dynamicore.analysing.DataAnalysisBase;
import dynamicore.analysing.Row;
import dynamicore.analysing.Table;
import dynamicore.xc_input.sql.SQLInputSource;
import dynamicore.xc_input.twitter.TwitterInputSource;

import java.util.Optional;

public class DegreeCentralityAnalysis extends DataAnalysisBase {

    @Override
    public Table analyse() {
        Table table = new Table("Page Rank Results");
        try {
            for (Pair<String, Double> pair : graphNodeBll.degreeCentrality(this.project.getId())) {
                Long id = Long.valueOf(pair.getK().split(":")[0]);
                Row row = new Row();
                row.addColumn(new Column("Id", id.toString()));
                row.addColumn(new Column("Score", pair.getV().toString()));
                Optional<Node> node = nodeBll.get(id, this.projectDataSource.getSource());
                if (projectDataSource.getSource().equals(TwitterInputSource.class.getName())) {
                    //for twitter only add if the name exists in starting ids
                    if (node.isPresent() && (projectDataSource.isSeedsContains(node.get().get("AccountName", "")))) {
                        row.addColumnAt(1, new Column("Name", node.get().get("Name", "")));
                        table.addRow(row);
                    }
                } else if (projectDataSource.getSource().equals(SQLInputSource.class.getName())) {
                    if (node.isPresent() && projectDataSource.startingIdsAsList().contains(String.valueOf(node.get().getId()))) {
                        row.addColumnAt(1, new Column("Name", node.get().get("Name", "")));
                        table.addRow(row);
                    }
                } else {
                    table.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            table.empty("make sure neo4j has algo package loaded");
        }
        return table;
    }

    @Override
    public Class<? extends DataAnalysisBase> describe() {
        return DegreeCentralityAnalysis.class;
    }
}
