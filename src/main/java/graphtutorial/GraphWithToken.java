package graphtutorial;

import com.azure.identity.AuthenticationRecord;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.implementation.MsalAuthenticationAccount;
import com.microsoft.aad.msal4j.*;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class GraphWithToken {
    private static Logger logger = LogManager.getLogger();
    GraphProperties graphProperties;
    boolean useTokenAuthProvider;

    public GraphWithToken() {
        this.LoadProperties();
    }

    public String getUserId() {
        return this.graphProperties.getUserId();
    }

    public boolean isUseTokenAuthProvider() {
        return this.useTokenAuthProvider;
    }

    private void LoadProperties(){
        // Load OAuth settings
        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
        } catch (
                IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            throw new RuntimeException(e.getMessage());
        }
        this.useTokenAuthProvider = Boolean.parseBoolean(oAuthProperties.getProperty("use.token.provider"));
        final String appId = oAuthProperties.getProperty("app.id");
        final String userId = oAuthProperties.getProperty("user.id");
        final String secret = oAuthProperties.getProperty("secret");
        final String tenant = oAuthProperties.getProperty("tenant.id");
        final String authority = oAuthProperties.getProperty("authority");
        final List<String> appScopes = Arrays
                .asList(oAuthProperties.getProperty("scopes").split(","));

        this.graphProperties = new GraphProperties(appId, userId, secret, tenant, authority, appScopes);
    }

    public String getToken() throws MalformedURLException {
        IClientCredential credential = ClientCredentialFactory.createFromSecret(this.graphProperties.getSecret());

        ConfidentialClientApplication cca =
                ConfidentialClientApplication
                        .builder(this.graphProperties.getAppId(), credential)
                        .authority(this.graphProperties.getAuthority())
                        .build();

        IAuthenticationResult result;
        try {
         //   AuthenticationRecord authenticationRecord = AuthenticationRecord.
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(new HashSet<>(this.graphProperties.getAppScopes()))
                            .build();

            // try to acquire token silently. This call will fail since the token cache does not
            // have a token for the application you are requesting an access token for
            result = cca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {

                ClientCredentialParameters parameters =
                        ClientCredentialParameters
                                .builder(new HashSet<>(this.graphProperties.getAppScopes()))
                                .build();

                // Try to acquire a token. If successful, you should see
                // the token information printed out to console
                result = cca.acquireToken(parameters).join();
            } else {
                // Handle other exceptions accordingly
                throw ex;
            }
        }

        var token = result.accessToken();

        return token;
    }

    public IAuthenticationProvider getAuthProviderFromToken(String token){
        IAuthenticationProvider authProvider = new IAuthenticationProvider() {
            @Override
            public CompletableFuture<String> getAuthorizationTokenAsync(@NotNull URL requestUrl) {
                CompletableFuture<String> alreadyThere = new CompletableFuture<>();
                alreadyThere.complete(token);
                return alreadyThere;
            }
        };

        return authProvider;
    }

    public IAuthenticationProvider getAuthProviderFromSecret(){
        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(this.graphProperties.getAppId())
                .clientSecret(this.graphProperties.getSecret())
                .tenantId(this.graphProperties.getTenant())
                .build();

        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(this.graphProperties.getAppScopes(), clientSecretCredential);

        return tokenCredentialAuthProvider;
    }

    public GraphServiceClient getGraphServiceClient(IAuthenticationProvider authenticationProvider){
        GraphServiceClient graphClient =
                GraphServiceClient
                        .builder()
                        .authenticationProvider(authenticationProvider)
                        .buildClient();

        return graphClient;
    }


}
