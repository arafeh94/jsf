package com.arafeh.jsf.model;

import java.util.*;
import java.util.stream.Collectors;

import static com.arafeh.jsf.core.utils.Extensions.listOf;

public class ProjectDataSource {
    private String source;
    private String graphId;
    private ArrayList<ProjectDataSourceSettings> settings;
    private String startingIds;

    public ProjectDataSource() {
        settings = new ArrayList<>();
    }

    public String getSource() {
        return source;
    }

    public String getSourceName() {
        if (source.contains(".")) {
            return source.substring(source.lastIndexOf(".") + 1);
        } else {
            return source;
        }
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<ProjectDataSourceSettings> getSettings() {
        return settings;
    }

    public void setSettings(ArrayList<ProjectDataSourceSettings> settings) {
        this.settings = settings;
    }

    public ArrayList<String> startingIdsAsList() {
        return listOf(startingIds.split(";"));
    }

    public String getStartingIds() {
        return this.startingIds;
    }

    public void setStartingIds(String startingIds) {
        this.startingIds = startingIds;
    }

    public boolean isSeedsContains(String c) {
        return startingIdsAsList().stream().anyMatch(id -> id.replaceFirst("@", "").equals(c));
    }

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public List<ProjectDataSourceSettings> settingsOf(String key) {
        if (this.getSettings() == null) return Collections.emptyList();
        return this.getSettings().stream().filter(s -> s.getField().equals(key)).collect(Collectors.toList());
    }

    public Optional<ProjectDataSourceSettings> settingsOf(String key, int level) {
        return this.settingsOf(key).stream().filter(s -> {
            if (s.levelsAsInt().isEmpty()) return true;
            return s.levelsAsInt().contains(level);
        }).findFirst();
    }

    public void addSetting(String key, String value, String levels) {
        if (levels == null) levels = "";
        this.settings.add(new ProjectDataSourceSettings(key, value, levels));
    }


}
