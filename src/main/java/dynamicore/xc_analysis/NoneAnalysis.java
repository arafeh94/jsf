package dynamicore.xc_analysis;

import com.arafeh.jsf.core.utils.TextGenerator;
import dynamicore.analysing.*;

public class NoneAnalysis extends DataAnalysisBase {
    @Override
    public Table analyse() {
        Table table = new Table("Random Data");
        for (int i = 0; i < 10; i++) {
            Row row = new Row();
            row.addColumn(new Column("First Name", TextGenerator.getInstance().getName()));
            row.addColumn(new Column("Last Name", TextGenerator.getInstance().getName()));
            table.addRow(row);
        }
        return table;
    }

    @Override
    public Class<? extends DataAnalysisInterface> describe() {
        return NoneAnalysis.class;
    }
}
