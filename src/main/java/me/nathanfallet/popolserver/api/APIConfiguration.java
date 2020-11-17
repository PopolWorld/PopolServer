package me.nathanfallet.popolserver.api;

public class APIConfiguration {
    
    // Properties
    public final String scheme;
    public final String host;
    public final int port;

    // Constructor
    public APIConfiguration(String scheme, String host) {
        this.scheme = scheme;
        this.host = host;
        this.port = scheme.equals("https") ? 443 : 3000;
    }

}
