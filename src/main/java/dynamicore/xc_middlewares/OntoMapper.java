package dynamicore.xc_middlewares;

import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.Project;
import com.arafeh.jsf.model.ProjectDataSource;
import com.google.gson.Gson;
import dynamicore.Static_Settings;
import dynamicore.input.middlewares.AfterScanFilterMiddleware;
import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.input.middlewares.Event;
import dynamicore.input.middlewares.ScanFilterMiddleware;
import dynamicore.input.node.InputNode;
import dynamicore.tools.SVMModel;

import java.util.HashMap;

public class OntoMapper extends AfterScanFilterMiddleware {

    public OntoMapper() {

    }

    @Override
    public Boolean accepted(Project project, ProjectDataSource dataSource, Node node, InputNode iNode, int currentLevel) {
        if (currentLevel == 1) {
            iNode.setCustomType("Conference");
            iNode.setText("C");
        } else {
            node.get("Description").ifPresent((desc) -> {
                iNode.setCustomType(SVMModel.predictRole(desc.toString()));
                iNode.setText(desc.toString().substring(0, 1));
            });
        }
        return null;
    }


    @Override
    public boolean ignoreFirstLevel() {
        return false;
    }

    @Override
    public Class<? extends ScanFilterMiddleware> describe() {
        return OntoMapper.class;
    }

}
