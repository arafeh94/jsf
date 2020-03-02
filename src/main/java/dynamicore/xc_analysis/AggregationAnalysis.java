package dynamicore.xc_analysis;

import com.arafeh.jsf.core.utils.TextGenerator;
import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.model.GraphNode;
import dynamicore.analysing.*;
import dynamicore.input.node.GraphMapper;
import dynamicore.input.node.NodeUtils;
import org.primefaces.json.JSONObject;

import java.util.HashMap;

public class AggregationAnalysis extends DataAnalysisBase {
    @Override
    public Table analyse() {
        Table table = new Table("Aggregation Analysis");
        String graphId = project.getDataSource().getGraphId();
        HashMap<String, Integer> communities = new HashMap<>();
        Var<Integer> total = new Var<>(0);
        for (GraphNode gn : graphNodeBll.all()) {
            try {
                JSONObject value = new JSONObject(gn.getJson()).getJSONObject("value");
                JSONObject store = value.getJSONObject("store");
                String community = store.getString("Community");
                for (String cm : community.split(",")) {
                    cm = cm.replaceAll("\"", "").trim();
                    total.set(total.get() + 1);
                    if (!communities.containsKey(cm)) {
                        communities.put(cm, 0);
                    }
                    communities.put(cm, communities.get(cm) + 1);
                }
            } catch (Exception ignore) {
            }
        }

        for (String community : communities.keySet()) {
            int comCount = communities.get(community);
            Row row = new Row();
            row.addColumn(new Column("Community", community));
            row.addColumn(new Column("Count", String.valueOf(comCount)));
            table.addRow(row);

        }
        return table;
    }

    @Override
    public Class<? extends DataAnalysisInterface> describe() {
        return AggregationAnalysis.class;
    }
}
