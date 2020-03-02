package com.arafeh.jsf.dal;

import dynamicore.xc_input.twitter.TwitterPool;
import dynamicore.xc_middlewares.MinFollowers;
import org.junit.Test;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Optional;

public class TestTwitter {

    @Test
    public void test() {
        TwitterPool twitterPool = TwitterPool.getInstance();
        twitterPool.lookup(963073442, e -> {
            System.out.println("booom");
        }).ifPresent(user -> {
            System.out.println(user.toString());
        });
        ArrayList<Long> res = twitterPool.getFollowerIDs(963073442, e -> {
            System.out.println(e.toString());
        });
        System.out.println(res.size());
    }


}
