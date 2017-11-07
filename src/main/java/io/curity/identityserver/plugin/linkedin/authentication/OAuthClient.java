/*
 * Copyright (C) 2017 Curity AB. All rights reserved.
 *
 * The contents of this file are the property of Curity AB.
 * You may not copy or use this file, in either source code
 * or executable form, except in compliance with terms
 * set by Curity AB.
 *
 * For further information, please contact Curity AB.
 */

package io.curity.identityserver.plugin.linkedin.authentication;


import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.authentication.AuthenticationResult;
import se.curity.identityserver.sdk.web.Response;

import java.util.Map;
import java.util.Optional;

public interface OAuthClient {

    /**
     * Redirect to the authorization endpoint with all parameters set. Generates a state and code.
     *
     * @param response      to update with redirect url
     * @param authzEndpoint endpoint to redirect to
     * @param clientId      oauth2 client id
     * @param scope         scope(s) to ask for, space separated
     * @param extraParams   The possibility to add extra parameters to the authorize query
     */
    void redirectToAuthorizationEndpoint(Response response, String authzEndpoint, String clientId, @Nullable String scope,
                                         Map<String, String> extraParams);

    /**
     * Get authentication result using access token
     *
     * @param accessToken access token retrieved from linked
     * @return Optional AuthenticationResult
     */

    Optional<AuthenticationResult> getAuthenticationResult(String accessToken);

    /**
     * Call the token endpoint and collect the tokens. Validates the state created in the authz call.
     *
     * @param tokenEndpoint endpoint to call
     * @param clientId      oauth2 client id
     * @param clientSecret  oauth2 client secret
     * @param code          collected from the authorization endpoint
     * @param state         obtained from the callback uri
     * @return A Map parsed from the json response
     */
    Map<String, Object> getTokens(String tokenEndpoint, String clientId, String clientSecret, String code, String state);

    /**
     * Get fully qualified callback url.
     * @return callback url
     */
    String getCallbackUrl();
}
