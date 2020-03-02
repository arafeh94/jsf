package dynamicore;

import com.arafeh.jsf.core.utils.Level2ClassMapper;
import dynamicore.annotations.SettingsField;
import dynamicore.annotations.SettingsField.Category;
import dynamicore.xc_navigator.BFSNavigator;
import dynamicore.xc_navigator.MetropolisHastingNavigator;
import dynamicore.xc_navigator.RandomWalkNavigator;


@SuppressWarnings("ALL")
public class Static_Navigators extends Level2ClassMapper {


    @Override
    public Class<? extends Level2ClassMapper> describe() {
        return Static_Navigators.class;
    }

    public static class RW {
        @SettingsField(name = "Random Walk", assignedClass = RandomWalkNavigator.class, category = Category.NONE, description = "Random Walk Navigator")
        public static final String RANDOM_WALK = "Random Walk";

    }

    public static class MH {
        @SettingsField(name = "Metropolis Hasting", assignedClass = MetropolisHastingNavigator.class, category = Category.NONE, description = "Metropolis Hasting Navigator")
        public static final String RANDOM_WALK = "Metropolis Hasting";

    }

    public static class BFS {
        @SettingsField(name = "BFS", assignedClass = BFSNavigator.class, category = Category.NONE, description = "Breadth First Navigator")
        public static final String BFS = "BFS";
    }

}
