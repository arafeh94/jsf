package dynamicore;

import com.arafeh.jsf.core.utils.Level2ClassMapper;
import dynamicore.annotations.SettingsField;
import dynamicore.annotations.SettingsField.Category;
import dynamicore.xc_middlewares.*;
import dynamicore.xc_middlewares.extentions.IncludeTweets;


import static com.arafeh.jsf.core.utils.Html.*;
import static com.arafeh.jsf.core.utils.Html.Color.BLUE;
import static com.arafeh.jsf.core.utils.Html.Color.RED;

@SuppressWarnings("ALL")
public class Static_Settings extends Level2ClassMapper {
    private static Static_Settings instance = new Static_Settings();

    public static Static_Settings instance() {
        if (instance == null) instance = new Static_Settings();
        return instance;
    }

    @Override
    public Class<? extends Level2ClassMapper> describe() {
        return Static_Settings.class;
    }

    public static class Filters {
        @SettingsField(name = "Language", assignedClass = LanguageFilter.class, category = Category.FILTER, description = "Filter the nodes with the specified languages, multiple languages must be separated by ';', to specify a language you must follow the global abbreviation naming. e.g. <b>fr;en</b>")
        public static final String LANGUAGE = "language";

        @SettingsField(name = "OntoMapper", assignedClass = OntoMapper.class, category = Category.FILTER, description = "ontology mapper")
        public static final String ONTO_MAPPER = "ontoMapper";


        @SettingsField(name = "Location", assignedClass = LocationFilter.class, category = Category.FILTER, description = "Filter the nodes by selecting only the ones in the specified location, multiple location must be separated by ';', e.g. <b>italy</b>")
        public static final String LOCATION = "location";


        @SettingsField(name = "Date Of Birth", category = Category.FILTER, description = "Filter the nodes by ages, if this field has value all that have smaller ages will be ignores. e.g. select only the nodes that the owner age is bigger than <b>22</b>, this field value would be <b>22</b>")
        public static final String AGE = "dateOfBirth";


        @SettingsField(name = "Gender", category = Category.FILTER, description = "Filter the nodes by gender, the value of this field can be one of these <b>m,f,male,female</b>. This filter may not work for all data source providers.")
        public static final String GENDER = "gender";

        @SettingsField(name = "Max Level", assignedClass = MaxLevel.class, category = Category.FILTER, description = "Control the depth of the scanned nodes in the graph. e.g. value of <b>3</b> will result in scanning only the first 3 levels of followers, a->(b->c->d)")
        public static final String MAX_LEVEL = "Max_Level";


        @SettingsField(name = "Minimum Followers Threshold", assignedClass = MinFollowers.class, category = Category.FILTER, description = "Filter the nodes and ignore the ones that have a smaller number of this field value.")
        public static final String MIN_FOLLOWERS_THRESHOLD = "minFollowersThreshold";

        @SettingsField(name = "Max Fetches", assignedClass = MaxFetches.class, category = Category.FILTER, description = "If this filter is set, when the maximum number of fetched specified is reached, all of others nodes will be dead")
        public static final String MAX_FETCHES = "maxFollowersThreshold";

        @SettingsField(name = "Follower Percentage", category = Category.FILTER, description = "From the followers of the current node, scan only the nodes until the maximum percentage specified in this field reached e.g. <b>50% of node with 10 follower will result in 5 node scanned only</b>")
        public static final String PERCENT_FOLLOWERS_NUMBER = "perCentFollowersNumber";

        @SettingsField(name = "Account Age", category = Category.FILTER, description = "Filter the nodes by account ages, if this field has value all that have smaller age will be ignores. e.g. if account is created before <b>2012</b> ignore it, field value would be <b>2012</b>")
        public static final String ACCOUNT_AGE = "accountAge";

        @SettingsField(name = "Minimum Friends", category = Category.FILTER, assignedClass = MinFriendsFilter.class, description = "Filter the nodes by account ages, if this field has value all that have smaller age will be ignores. e.g. if account is created before <b>2012</b> ignore it, field value would be <b>2012</b>")
        public static final String MIN_FRENDS = "attributeFilter";

    }

    public static class RandomInputSourceSettings {
        @SettingsField(name = "RandomSource Max Depth", category = Category.INPUT, description = "Maximum Depth of the generated random graph. be careful of the node count which is exponential to graph depth, for e.g. a random graph with depth 3 will have 3^3 + 3^2 + 3^1 total number of nodes")
        public static final String MAX_DEPTH = "RandomSource:Max_Depth";

        @SettingsField(name = "RandomSource Seed", category = Category.INPUT, description = "Work same as random seed, this field is used for testing purposes, with this seed specified generating graph multiple time will results in the same graph structure.")
        public static final String SEED = "RandomSource:Seed";
    }


    public static class InputSourceSettings {
        @SettingsField(name = "Sources MaxLoadedNodes", category = Category.INPUT, description = "This varilable work wonder, let suppose for example a person has 4000 follower, if this varilable is set to 5 for example we will load from his children nodes 5 only instead of 4000, this is good when you need to make fast tests or to have a better graph visualisation.")
        public static final String MAX_LOADED_NODES = "Sources:MaxLoadedNodes";

        @SettingsField(name = "Twitter Include Tweets", category = Category.INPUT, assignedClass = IncludeTweets.class, description = "1 to include Account tweets, 0 otherwise")
        public static final String INCLUDE_TWEETS = "Tweeter:IncludeTweets";


    }


    public static class RandomWalkSettings {
        @SettingsField(name = "RandomWalk ForwardWeight", category = Category.NAVIGATION, description = "To control random walk algorithm movement, the lower the number make the algorithm focus more on the low level, the higher the number make the algorithm visit deep area in the graph")
        public static final String FORWARD_WEIGHT = "RandomWalk:ForwardWeight";

        @SettingsField(name = "RandomWalk Iterations", category = Category.NAVIGATION, description = "how many time the next method will be called <b>by default it will be called maximum 10k times</b>")
        public static final String ITERATIONS = "RandomWalk:ITERATIONS";
    }

    public static class MetropolisHasting {
        @SettingsField(name = "MetropolisHasting Distribution", category = Category.NAVIGATION, description = "Distribution supplied to algorithm, 'Normal Distribution' by default")
        public static final String DISTRIBUTION = "MetropolisHasting:DISTRIBUTION";

        @SettingsField(name = "MetropolisHasting Mean", category = Category.NAVIGATION, description = "mean of the distribution")
        public static final String MEAN = "MetropolisHasting Mean";

        @SettingsField(name = "MetropolisHasting Standard Deviation", category = Category.NAVIGATION, description = "MetropolisHasting Standard Deviation")
        public static final String STANDARD_DEVIATION = "MetropolisHasting:STANDARD_DEVIATION";
    }

    public static class Aggregation {
        @SettingsField(name = "Sum", category = Category.ANALYSIS, description = "Sum function")
        public static final String SUM = "Aggregation:SUM";

        @SettingsField(name = "Percentage", category = Category.ANALYSIS, description = "Percentage")
        public static final String PERCENTAGE = "Aggregation:PERCENTAGE";

    }


}
