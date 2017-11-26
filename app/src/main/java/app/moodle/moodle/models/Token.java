package app.moodle.moodle.models;

/**
 * Token model
 */

public class Token {
    private String token;
    private String privatetoken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPrivatetoken() {
        return privatetoken;
    }

    public void setPrivatetoken(String privatetoken) {
        this.privatetoken = privatetoken;
    }
}
