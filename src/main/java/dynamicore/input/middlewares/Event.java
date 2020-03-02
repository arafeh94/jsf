package dynamicore.input.middlewares;

public enum Event {
    //Project::project, Class<?> describe(), InputNode::lastNode, Node::node
    NodeFetched,
    //Project::project
    ObservationStarted,
    //InputNode::mainGraph
    MainGraphFetched,
    //Project::project, Class<? extends DataInputBase>:: describe()
    CreateMainGraph,
    //Project::project, breadcrumb
    BreadCrumbUpdated,
    //Project::project, InputNode::graph
    UpdateSaved,
    //Project::project, InputNode::lastNode
    LastNodeUpdated,
    //Project::project, InputNode::lastNode
    NodeWillFetch;


    Event() {

    }

}
