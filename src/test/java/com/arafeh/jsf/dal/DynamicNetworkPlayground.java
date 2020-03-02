package com.arafeh.jsf.dal;

import com.arafeh.jsf.model.Node;
import com.arafeh.jsf.model.RelationNode;
import org.bson.conversions.Bson;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public class DynamicNetworkPlayground extends TestDynamicNetwork {
    @Override
    @Test
    public void test() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        init();
        List<Node> nodes = nodeBll.all();
        StringBuilder all = new StringBuilder();
        all.append("Seed,Relation,Account Name,ID,Description,FriendsCount,FollowersCount").append("\n");
        for (RelationNode seed : relationNodeBll.getSeeds(1)) {
            Node first = nodes.stream().filter(node -> node.getId() == seed.getRight()).findFirst().get();
            for (RelationNode relationNode : relationNodeBll.getRelations(1, seed.getRight())) {
                try {
                    Node info = nodes.stream().filter(node -> node.getId() == relationNode.getRight()).findFirst().get();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(first.get("AccountName").get()).append(",");
                    stringBuilder.append("Followed By").append(",");
                    stringBuilder.append(info.get("AccountName").get()).append(",");
                    stringBuilder.append(info.getId()).append(",");
                    stringBuilder.append(info.get("Description").get().toString().replace("\n", " ").replace("\r", "  ").replace(",", " ")).append(",");
                    stringBuilder.append(info.get("FriendsCount").get()).append(",");
                    stringBuilder.append(info.get("FollowersCount").get()).append(",");
                    all.append(stringBuilder.toString()).append("\n");
                } catch (Exception ignore) {

                }
            }
            try {
                FileWriter fileWriter = new FileWriter("C:\\Users\\Arafeh\\Desktop\\data.csv");
                fileWriter.write(all.toString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test2() {
        init();
        List<Node> nodes = nodeBll.all();
        List<Long> seedIds = relationNodeBll.getSeeds(1).stream().map(RelationNode::getRight).collect(Collectors.toList());
        List<RelationNode> relationNodes = relationNodeBll.getSource().query(mongoCollection -> {
            ArrayList<Bson> bsons = new ArrayList<>();
            for (Long seedId : seedIds) {
                bsons.add(eq("right", seedId));
            }
            ArrayList<RelationNode> results = new ArrayList<>();
            for (Object o : mongoCollection.find(or(bsons))) {
                RelationNode relationNode = (RelationNode) o;
                if (relationNode.getLeft() != 1647469518175629784L) {
                    results.add(relationNode);
                }
            }
            return results;
        });
        HashMap<Long, Integer> map = new HashMap<>();
        for (RelationNode relationNode : relationNodes) {
            if (map.containsKey(relationNode.getLeft())) {
                int res = map.get(relationNode.getLeft());
                res += 1;
                map.put(relationNode.getLeft(), res);
            } else {
                map.put(relationNode.getLeft(), 1);
            }
        }
        System.out.println(map);
        StringBuilder all = new StringBuilder();
        all.append("AccountName,ID,FollowedBySpinDr").append("\n");

        for (Long id : map.keySet()) {
            try {
                Node info = nodes.stream().filter(node -> node.getId() == id).findFirst().get();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(info.get("AccountName").get()).append(",");
                stringBuilder.append(info.getId()).append(",");
                stringBuilder.append(map.get(id)).append(",");
                all.append(stringBuilder.toString()).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            FileWriter fileWriter = new FileWriter("C:\\Users\\Arafeh\\Desktop\\data2.csv");
            fileWriter.write(all.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
