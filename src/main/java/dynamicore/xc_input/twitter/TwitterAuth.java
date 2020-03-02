package dynamicore.xc_input.twitter;

public class TwitterAuth {
    public final String accessToken;
    public final String accessTokenSecret;
    public final String consumerKey;
    public final String consumerSecret;

    public TwitterAuth(String accessToken, String accessTokenSecret, String consumerKey, String consumerSecret) {
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    @Override
    public String toString() {
        return "TwitterAuth{" +
                "accessToken='" + accessToken + '\'' +
                ", accessTokenSecret='" + accessTokenSecret + '\'' +
                ", consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='" + consumerSecret + '\'' +
                '}';
    }
}
