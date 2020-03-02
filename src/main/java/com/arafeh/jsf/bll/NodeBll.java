package com.arafeh.jsf.bll;

import com.arafeh.jsf.core.utils.Var;
import com.arafeh.jsf.dal.NodeDAL;
import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.NodeType;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.arafeh.jsf.core.utils.Extensions.mapOf;
import static com.mongodb.client.model.Filters.*;

@Singleton
@LocalBean
public class NodeBll implements Serializable {
    private static NodeDAL nodes;
    private static NodeBll instance;

    public static NodeBll getInstance() {
        return instance;
    }

    public NodeBll() {
    }

    @PostConstruct
    public void init() {
        if (nodes == null) {
            nodes = new NodeDAL();
            instance = this;
        }
    }

    public boolean set(Node node) {
        if (get(node.getId(), node.getType(), node.getSource()).isPresent()) return true;
        return nodes.add(node);
    }

    public Optional<Node> get(long id, String source) {
        Var<String> src = new Var<>();
        String[] subs = source.split("\\.");
        src.set(subs[subs.length - 1]);
        List<Node> list = nodes.query(param -> {
                    Bson query = and(eq("_id", id), eq("source", src.get()));
                    return nodes.toList(param.find(query));
                }
        );
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

    public Optional<Node> get(long id, String type, String source) {
        Var<String> src = new Var<>();
        String[] subs = source.split("\\.");
        src.set(subs[subs.length - 1]);
        List<Node> list = nodes.query(param -> {
                    Bson query = and(eq("_id", id), eq("source", src.get()), eq("type", type));
                    return nodes.toList(param.find(query));
                }
        );
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

    public ArrayList<Long> dif(ArrayList<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<>();
        List<Node> nds = nodes.findAll(ids);
        List<Long> exists = new ArrayList<>();
        for (Node nd : nds) {
            if (ids.contains(nd.getId())) {
                exists.add(nd.getId());
            }
        }
        if (!exists.isEmpty()) ids.removeAll(exists);
        return ids;
    }


    public List<Node> all() {
        return nodes.all();
    }

    public List<Node> all(long projectId) {
        return nodes.find(mapOf("projectId", projectId));
    }

    public boolean exists(NodeType type, long id, Class source) {
        List<Node> list = nodes.query(param -> {
                    Bson query = and(eq("_id", id), eq("source", source.getSimpleName()), eq("type", type.name()));
                    return nodes.toList(param.find(query));
                }
        );
        return !list.isEmpty();
    }
}
