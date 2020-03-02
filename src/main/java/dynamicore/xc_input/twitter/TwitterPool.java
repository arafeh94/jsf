package dynamicore.xc_input.twitter;

import com.arafeh.jsf.config.AppProperties;
import com.arafeh.jsf.core.protocols.Action;
import com.arafeh.jsf.service.LoggingService;
import com.arafeh.jsf.service.TaskManagement;
import javafx.concurrent.Task;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;

public class TwitterPool {
    //sleep for 5s before retrying
    private final int SLEEP_TIME_BEFORE_RETRY = 5000;
    //number of allowed retry
    private final int MAX_RETRY = -1;

    private static TwitterPool instance;
    private int smallestResetTime = Integer.MAX_VALUE;
    private int retries = 0;

    public static TwitterPool getInstance() {
        if (instance == null) {
            synchronized (TwitterPool.class) {
                instance = new TwitterPool();
            }
        }
        return instance;
    }

    private int useAt = 0;
    private ArrayList<Twitter> twitters = new ArrayList<>();

    private TwitterPool() {
        ArrayList<TwitterAuth> auths = AppProperties.getInstance().twitterAuths();
        if (!auths.isEmpty()) {
            for (TwitterAuth auth : auths) {
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey(auth.consumerKey)
                        .setOAuthConsumerSecret(auth.consumerSecret)
                        .setOAuthAccessToken(auth.accessToken)
                        .setOAuthAccessTokenSecret(auth.accessTokenSecret);
                TwitterFactory tf = new TwitterFactory(cb.build());
                this.twitters.add(tf.getInstance());

            }
        } else {
            LoggingService.log("invalid twitters auth, twitters not running");
        }
    }

    @SuppressWarnings({"ConstantConditions", "LoopStatementThatDoesntLoop"})
    public ArrayList<Long> getFollowerIDs(long twitterId, Action<Exception> onError) {
        ArrayList<Long> ids = new ArrayList<>();
        request(twitter -> {
            IDs idsCursor = null;
            do {
                long cursor = idsCursor == null ? -1 : idsCursor.getNextCursor();
                idsCursor = twitter.getFollowersIDs(twitterId, cursor);
                long[] twitterIds = idsCursor.getIDs();
                for (long id : twitterIds) {
                    ids.add(id);
                }
                //I don't want to spent all twitter resource on a single user -_-
                break;
            } while (idsCursor.hasNext());
        }, onError);
        return ids;
    }

    public ArrayList<User> lookup(String[] screenNames, Action<Exception> onError) {
        ArrayList<User> users = new ArrayList<>();
        request(twt -> {
            for (int i = 0; i < screenNames.length; i += 99) {
                if (i > 99) break;
                int to = i + 99;
                if (to >= screenNames.length) to = screenNames.length;
                String[] batch = Arrays.copyOfRange(screenNames, i, to);
                ResponseList<User> result = twt.lookupUsers(batch);
                users.addAll(result);
            }
        }, onError);
        return users;
    }

    public Optional<User> lookup(String screenName, Action<Exception> onError) {
        String[] names = new String[]{screenName};
        return lookup(names, onError).stream().findFirst();
    }

    public ArrayList<User> lookupNames(List<String> screenNames, Action<Exception> onError) {
        String[] sc = screenNames.toArray(new String[0]);
        return lookup(sc, onError);
    }

    public ArrayList<Status> query(List<String> tags, Action<Exception> onError) {
        ArrayList<Status> list = new ArrayList<>();
        for (String tag : tags) {
            request(twitter -> {
                Query query = new Query(tag);
                query.count(100);
                QueryResult result = twitter.search(query);
                list.addAll(result.getTweets());
            }, onError);
        }
        return list;
    }

    public ArrayList<User> lookup(long[] ids, Action<Exception> onError) {
        ArrayList<User> list = new ArrayList<>();
        request(twitter -> {
            for (int i = 0; i < ids.length; i += 99) {
                if (i >= 99) break;
                int to = i + 99;
                if (to >= ids.length) to = ids.length;
                long[] batch = Arrays.copyOfRange(ids, i, to);
                ResponseList<User> result = twitter.lookupUsers(batch);
                list.addAll(result);
            }
        }, onError);
        return list;
    }

    public Optional<User> lookup(long id, Action<Exception> onError) {
        long[] ids = {id};
        return lookup(ids, onError).stream().findFirst();
    }


    public ArrayList<User> lookupIds(List<Long> ids, Action<Exception> onError) {
        Long[] sc = ids.toArray(new Long[0]);
        return lookup(ArrayUtils.toPrimitive(sc), onError);
    }

    public List<Status> getRetweets(long id, Action<Exception> onError) {
        List<Status> list = new ArrayList<>();
        request(twitter -> {
            List<Status> statuses = twitter.getRetweets(id);
            list.addAll(statuses);
        }, onError);
        return list;
    }

    public List<Status> getTweets(long id, TwitterInputSource.TwitterExceptionHandler onError) {
        List<Status> list = new ArrayList<>();
        request(twitter -> {
            List<Status> statuses = twitter.getUserTimeline(id);
            list.addAll(statuses);
        }, onError);
        return list;
    }


    public interface TwitterConsumer {
        void consume(Twitter twitter) throws TwitterException;
    }

    public void request(TwitterConsumer consumer, Action<Exception> onError) {
        try {
            consumer.consume(acquire());
        } catch (TwitterException e) {
            LoggingService.log(e.getLocalizedMessage());
            if (e.exceededRateLimitation()) {
                LoggingService.log("limit reached, using other account if possible");
                LoggingService.log(e.getRateLimitStatus().toString());
                if (e.getRateLimitStatus().getSecondsUntilReset() < smallestResetTime) {
                    smallestResetTime = e.getRateLimitStatus().getSecondsUntilReset();
                }
                useAt++;
                LoggingService.log("new api use position", String.valueOf(useAt));
                request(consumer, onError);
            } else if (e.isCausedByNetworkIssue()) {
                LoggingService.log("network related issue", e.getMessage());
                try {
                    LoggingService.log("retry after", SLEEP_TIME_BEFORE_RETRY, "millis");
                    Thread.sleep(SLEEP_TIME_BEFORE_RETRY);
                    LoggingService.log("sleeping finished, starting again");
                    request(consumer, onError);
                } catch (Exception eee) {
                    LoggingService.log("sleeping interrupted", eee.getLocalizedMessage());
                }
            } else {
                LoggingService.log("unknown issue with twitter", e.getMessage(), e.getErrorCode(), e.getErrorMessage());
                if (onError != null) {
                    onError.run(e);
                }
            }
        } catch (NullPointerException e) {
            LoggingService.log("All twitters instance exhausted");
            try {
                LoggingService.log("Sleeping for ", String.valueOf(smallestResetTime / 60), " minutes...");
                Thread.sleep(smallestResetTime * 1000);
                smallestResetTime = Integer.MAX_VALUE;
                LoggingService.log("sleeping finished, starting again");
                useAt = 0;
                request(consumer, onError);
            } catch (Exception ee) {
                LoggingService.log("sleeping interrupted", ee.getLocalizedMessage());
            }
        }
    }


    private Twitter acquire() {
        if (useAt < twitters.size()) {
            return twitters.get(useAt);
        }
        useAt = 0;
        return null;
    }


}
