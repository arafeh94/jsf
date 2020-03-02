package com.arafeh.jsf.model;

public enum NodeType {
    ACCOUNT, FOLLOWER, POST, COMMENT, HASHTAG, ROOT;

    public static NodeType of(String type) {
        if (type == null) return null;
        switch (type) {
            case "FOLLOWER":
                return FOLLOWER;
            case "ACCOUNT":
                return ACCOUNT;
            case "POST":
                return POST;
            case "COMMENT":
                return COMMENT;
            case "HASHTAG":
                return HASHTAG;
            case "ROOT":
                return ROOT;
            default:
                return FOLLOWER;
        }
    }
}
