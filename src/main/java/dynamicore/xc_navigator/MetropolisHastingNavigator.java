package dynamicore.xc_navigator;

import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSourceSettings;
import dynamicore.Static_Settings;
import dynamicore.input.DataInputBase;
import dynamicore.input.node.InputNode;
import dynamicore.navigator.Navigator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import static com.arafeh.jsf.core.utils.Extensions.random;
import static java.lang.Math.*;

public class MetropolisHastingNavigator extends RandomWalkNavigator {
    private double mean;
    private double lg;
    private double stand;
    private String distribution;

    @Override
    public void init(Project project, Class<? extends DataInputBase> dataSourceInterface) {
        super.init(project, dataSourceInterface);
        this.distribution = dataSource.settingsOf(Static_Settings.MetropolisHasting.DISTRIBUTION).stream().findFirst().orElse(new ProjectDataSourceSettings("", "gaussian", "")).asString();
        this.mean = dataSource.settingsOf(Static_Settings.MetropolisHasting.MEAN).stream().findFirst().orElse(new ProjectDataSourceSettings("", "0", "")).asDouble();
        this.stand = dataSource.settingsOf(Static_Settings.MetropolisHasting.STANDARD_DEVIATION).stream().findFirst().orElse(new ProjectDataSourceSettings("", "1", "")).asDouble();
        lg = lg(mean, 1, 1);
    }

    @Override
    public InputNode next(InputNode last) {
        InputNode node = super.next(last);
        while (!accepted()) {
            super.currentCalls -= 1;
            node = super.next(last);
        }
        return node;
    }


    @SuppressWarnings("SameParameterValue")
    private double lg(double mu, double n, double ybar) {
        double mu2 = pow(mu, 2);
        return n * (ybar * mu - mu2 / 2.0d) - log(1.0 + mu2);
    }

    private boolean accepted() {
        double mu_node = mean();
        double lg_node = lg(mu_node, 1, 1);
        double lalpha = lg_node - lg;
        double alpha = exp(lalpha);
        double u = new UniformRealDistribution().sample();
        if (u < alpha) {
            mean = mu_node;
            lg = lg_node;
            return true;
        }
        return false;
    }

    public double mean() {
        switch (distribution) {
            case "uniform":
                return new UniformRealDistribution().sample();
            case "poisson":
                return new PoissonDistribution(mean, stand).sample();
            case "gaussian":
            default:
                return new NormalDistribution(mean, stand).sample();
        }
    }


    @Override
    public Class<? extends Navigator> describe() {
        return MetropolisHastingNavigator.class;
    }

}
