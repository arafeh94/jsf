package com.arafeh.jsf.dal;

import com.arafeh.jsf.model.Project;
import dynamicore.DynamicNetwork;
import dynamicore.navigator.Navigator;
import dynamicore.xc_input.random.RandomInputSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DALServicesTest {
    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "ProjectServiceTest.war")
                .addPackages(true, "com.arafeh")
                .addAsResource("conf/app.properties")
                .addAsLibraries(Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .importRuntimeAndTestDependencies()
                        .resolve().withTransitivity().asFile()
                ).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }


    @Test
    public void testMongo() {

    }


    @Test
    public void testDynamicNetwork() {
        DynamicNetwork dynamicNetwork = DynamicNetwork.builder()
                .setNavigator(Navigator.class)
                .setProject(getProject())
                .addDataSource(RandomInputSource.class)
                .build();
        dynamicNetwork.execute();
    }

    @SuppressWarnings("Duplicates")
    private Project getProject() {
        return null;
    }

}
