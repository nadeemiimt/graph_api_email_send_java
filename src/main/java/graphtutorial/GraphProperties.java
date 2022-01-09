package graphtutorial;

import java.util.List;

public class GraphProperties {
    private String appId;
    private String userId;
    private String secret;
    private String tenant;
    private String authority;
    private List<String> appScopes;

    public GraphProperties() {
    }

    public GraphProperties(String appId, String userId, String secret, String tenant, String authority, List<String> appScopes) {
        this.appId = appId;
        this.userId = userId;
        this.secret = secret;
        this.tenant = tenant;
        this.authority = authority;
        this.appScopes = appScopes;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public List<String> getAppScopes() {
        return appScopes;
    }

    public void setAppScopes(List<String> appScopes) {
        this.appScopes = appScopes;
    }
}
