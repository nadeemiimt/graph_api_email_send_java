package graphtutorial;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.GraphServiceClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Graph Tutorial
 *
 */
public class App {
    private static Logger logger = LogManager.getLogger();
    public static void main(String[] args) throws MalformedURLException {
        GraphWithToken graph = new GraphWithToken();

        IAuthenticationProvider authenticationProvider;

        if(graph.isUseTokenAuthProvider()) {
            String token = graph.getToken();
            authenticationProvider = graph.getAuthProviderFromToken(token);
        } else {
            authenticationProvider = graph.getAuthProviderFromSecret();
        }

        GraphServiceClient graphClient = graph.getGraphServiceClient(authenticationProvider);

        Message message = new Message();
        message.subject = "Graph API Test?";
        ItemBody body = new ItemBody();
        body.contentType = BodyType.TEXT;
        body.content = "The new cafeteria is open.";
        message.body = body;

        LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
        Recipient toRecipients = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = "change@yahoo.com";
        toRecipients.emailAddress = emailAddress;
        toRecipientsList.add(toRecipients);
        message.toRecipients = toRecipientsList;

        LinkedList<Recipient> ccRecipientsList = new LinkedList<Recipient>();
        Recipient ccRecipients = new Recipient();
        EmailAddress emailAddress1 = new EmailAddress();
        emailAddress1.address = "change@gmail.com";
        ccRecipients.emailAddress = emailAddress1;
        ccRecipientsList.add(ccRecipients);
        message.ccRecipients = ccRecipientsList;

        boolean saveToSentItems = false;

        try {
            graphClient.users(graph.getUserId())
                    .sendMail(UserSendMailParameterSet
                            .newBuilder()
                            .withMessage(message)
                            .withSaveToSentItems(saveToSentItems)
                            .build())
                    .buildRequest()
                    .post();
        } catch (Exception e) {
            logger.error(e);
            logger.info("Message Send failure");
            return;
        }

        logger.info("Message Send successfully");
    }
}