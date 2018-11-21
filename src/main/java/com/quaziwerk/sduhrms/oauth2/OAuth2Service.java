package com.quaziwerk.sduhrms.oauth2;

import java.util.Scanner;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class OAuth2Service {

    private static final String NETWORK_NAME = "LinkedIn";
    String tok = "AQSwy6cLLKEekkDwrjxxC6qCGhbED2ba5xh5a4LkiWgGTmiBWlhnMUaoj3-9j73ONafSNDPXR-AlEWWQRfkUdkvSfNaPvT__5dMoMeqrOJUGI0v9jFdg3_A78mKXtNsN8YuKhIE1YWv9jIVgNrC9CN5kUk9PzOxPgIN36J5kK7MOUn7wwQQ4TDcnTDUfxg";
    private static final String PROTECTED_RESOURCE_URL = "https://api.linkedin.com/v1/people/~:(%s)";

    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
        // Replace these with your client id and secret
        final String clientId = "77c2q1te5fm2mz";
        final String clientSecret = "szVBWjo6MUqXFWQ5";
        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .scope("r_basicprofile r_emailaddress r_fullprofile") // replace with desired scope
                .callback("https://localhost/callback")
                .state("some_params")
                .build(LinkedInApi20.instance());
        final Scanner in = new Scanner(System.in);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        while (true) {
            System.out.println("Paste profile query for fetch (firstName, lastName, etc) or 'exit' to stop example");
            System.out.print(">>");
            final String query = in.nextLine();
            System.out.println();

            if ("exit".equals(query)) {
                break;
            }

            final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(PROTECTED_RESOURCE_URL, query));
            request.addHeader("x-li-format", "json");
            request.addHeader("Accept-Language", "ru-RU");
            service.signRequest(accessToken, request);
            final Response response = service.execute(request);
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());

            System.out.println();
        }
    }
}
