package dynamicore;

import com.arafeh.jsf.core.utils.Level2ClassMapper;
import dynamicore.annotations.SettingsField;
import dynamicore.annotations.SettingsField.Category;
import dynamicore.xc_analysis.DegreeCentralityAnalysis;
import dynamicore.xc_analysis.NoneAnalysis;
import dynamicore.xc_analysis.PageRankAnalysis;


@SuppressWarnings("ALL")
public class Static_Analysis extends Level2ClassMapper {

    @Override
    public Class<? extends Level2ClassMapper> describe() {
        return Static_Analysis.class;
    }

    public static class PageRank {
        @SettingsField(name = "Page Rank", assignedClass = PageRankAnalysis.class, description = "Page Rank", category = Category.NONE)
        public static final String PAGE_RANK = "Page Rank";

    }

    public static class DegreeCentrality {
        @SettingsField(name = "Degree Centrality", assignedClass = DegreeCentralityAnalysis.class, description = "Degree Centrality", category = Category.NONE)
        public static final String PAGE_RANK = "Degree Centrality";

    }

    public static class Random {
        @SettingsField(name = "Random", assignedClass = NoneAnalysis.class, category = Category.NONE, description = "Random Names")
        public static final String RANDOM = "Ranom";
    }

    public static class Aggregation {
        @SettingsField(name = "Aggregation", assignedClass = NoneAnalysis.class, category = Category.NONE, description = "Aggregation Function")
        public static final String AGGREGATION = "Aggregation";
    }

}
