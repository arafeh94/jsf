package dynamicore;

import com.arafeh.jsf.core.utils.Level2ClassMapper;
import dynamicore.annotations.SettingsField;
import dynamicore.annotations.SettingsField.Category;
import dynamicore.xc_input.random.RandomInputSource;
import dynamicore.xc_input.sql.SQLInputSource;
import dynamicore.xc_input.twitter.TwitterInputSource;


@SuppressWarnings("ALL")
public class Static_DataSources extends Level2ClassMapper {

    @Override
    public Class<? extends Level2ClassMapper> describe() {
        return Static_DataSources.class;
    }

    public static class Twitter {
        @SettingsField(name = "Twitter", assignedClass = TwitterInputSource.class, category = Category.NONE, description = "Twitter API")
        public static final String TWITTER = "Twitter";
    }

    public static class Random {
        @SettingsField(name = "Random Graph", assignedClass = RandomInputSource.class, category = Category.NONE, description = "Random Graph Generator")
        public static final String RANDOM = "Random";

    }

    public static class SQL {
        @SettingsField(name = "SQL", assignedClass = SQLInputSource.class, category = Category.NONE, description = "Load Data From SQL Database")
        public static final String SQL = "SQL";

    }

}
