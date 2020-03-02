package dynamicore.input;

import com.arafeh.jsf.model.ProjectDataSource;

public class DataSourceNotRegisteredException extends Exception {
    public DataSourceNotRegisteredException(String source) {
        super(source + " not registered in the current dynamic network");
    }
}
