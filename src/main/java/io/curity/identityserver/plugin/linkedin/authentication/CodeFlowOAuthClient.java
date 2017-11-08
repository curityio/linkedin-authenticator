/*
 *  Copyright 2017 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.curity.identityserver.plugin.linkedin.authentication;

import io.curity.identityserver.plugin.linkedin.config.LinkedInAuthenticatorPluginConfig;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.attribute.ContextAttributes;
import se.curity.identityserver.sdk.attribute.SubjectAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticationResult;
import se.curity.identityserver.sdk.errors.ErrorCode;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider;
import se.curity.identityserver.sdk.web.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static io.curity.identityserver.plugin.linkedin.config.Constants.Params.*;
import static org.apache.http.HttpHeaders.AUTHORIZATION;


public class CodeFlowOAuthClient implements OAuthClient {
    private static final String CALLBACK_URI_SUFFIX = "/callback";

    private static final Logger _logger = LoggerFactory.getLogger(CodeFlowOAuthClient.class);

    private final ExceptionFactory _exceptionFactory;
    private final HttpClient _client;
    private final LinkedInAuthenticatorPluginConfig _config;
    private final AuthenticatorInformationProvider _provider;
    private final Json _json;

    public CodeFlowOAuthClient(ExceptionFactory exceptionFactory,
                               LinkedInAuthenticatorPluginConfig config,
                               AuthenticatorInformationProvider provider,
                               Json json) {
        _exceptionFactory = exceptionFactory;
        _client = HttpClientBuilder.create().build();
        _config = config;
        _provider = provider;
        _json = json;
    }

    private String createState() {
        String nonce = Long.toUnsignedString(new Date().getTime());
        return nonce;
    }


    private HttpResponse callTokenEndpoint(String tokenEndpoint, UrlEncodedFormEntity data) {
        HttpPost post = new HttpPost(tokenEndpoint);
        post.setEntity(data);

        try {
            return _client.execute(post);
        } catch (IOException e) {
            _logger.warn("Could not communicate with token endpoint", e);

            throw _exceptionFactory.internalServerException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Authentication failed");
        }
    }

    private Map<String, Object> parseResponse(HttpResponse response) {
        try {
            String jsonString = EntityUtils.toString(response.getEntity());

            return _json.fromJson(jsonString);
        } catch (IOException e) {
            _logger.debug("Could not parse UserInfo", e);

            throw _exceptionFactory.internalServerException(ErrorCode.INVALID_SERVER_STATE, "Authentication failed");
        }
    }

    private UrlEncodedFormEntity createPostData(String clientId, String clientSecret, String code, String callbackUrl) {
        PostDataParamBuilder postData = new PostDataParamBuilder();

        postData.addPair(PARAM_CODE, code)
                .addPair(PARAM_CLIENT_ID, clientId)
                .addPair(PARAM_CLIENT_SECRET, clientSecret)
                .addPair(PARAM_GRANT_TYPE, PARAM_GRANT_TYPE_AUTHORIZATION_CODE)
                .addPair(PARAM_REDIRECT_URI, callbackUrl);

        try {
            return new UrlEncodedFormEntity(postData.getPairs());
        } catch (UnsupportedEncodingException e) {
            _logger.debug("Could not url encode post data", e);

            throw _exceptionFactory.configurationException("Bad encoding");
        }
    }

    private String buildUrl(String authzEndpoint, String clientId, String state, @Nullable String scope,
                            Map<String, String> extraParams) {
        URIBuilder builder;

        try {
            builder = new URIBuilder(authzEndpoint);
        } catch (URISyntaxException e) {
            _logger.warn("Bad syntax in redirect url", e);
            throw _exceptionFactory.configurationException("Bad syntax in redirect url");
        }

        if (scope != null && scope != "") {
            builder.addParameter(PARAM_SCOPE, scope);
        }

        builder.addParameter(PARAM_STATE, state)
                .addParameter(PARAM_CLIENT_ID, clientId)
                .addParameter(PARAM_RESPONSE_TYPE, PARAM_CODE)
                .addParameter(PARAM_GRANT_TYPE, PARAM_GRANT_TYPE_AUTHORIZATION_CODE);

        for (String key : extraParams.keySet()) {
            builder.addParameter(key, extraParams.get(key));
        }

        return builder.toString();
    }

    @Override
    public void redirectToAuthorizationEndpoint(Response response, String authzEndpoint, String clientId,
                                                @Nullable String scope, Map<String, String> extraParams) {
        String state = createState();

        String url = buildUrl(authzEndpoint, clientId, state, scope, extraParams);

        _logger.debug("URL of authorize endpoint: {}", url);

        throw _exceptionFactory.redirectException(url);
    }

    @Override
    public Map<String, Object> getTokens(String tokenEndpoint, String clientId, String clientSecret, String code, String state) {

        UrlEncodedFormEntity data = createPostData(clientId, clientSecret, code, getCallbackUrl());
        HttpResponse response = callTokenEndpoint(tokenEndpoint, data);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            _logger.debug("Got error response from token endpoint {}", response.getStatusLine());

            throw _exceptionFactory.internalServerException(ErrorCode.INVALID_SERVER_STATE, "INTERNAL SERVER ERROR");
        }

        return parseResponse(response);
    }

    @Override
    public Optional<AuthenticationResult> getAuthenticationResult(String accessToken) {
        HttpGet get = new HttpGet(_config.getUserInfoEndpoint());
        get.setHeader(AUTHORIZATION, BEARER + accessToken);
        try {
            Map<String, Object> profileData = parseResponse(_client.execute(get));

            ContextAttributes contextAttributes = ContextAttributes.of(Attributes.of(Attribute.of(PARAM_ACCESS_TOKEN, accessToken),
                    Attribute.of(PARAM_URL, ((Map) (profileData.get(PROFILE_REQUEST_URL))).get(PARAM_URL).toString())));
            AuthenticationAttributes attributes = AuthenticationAttributes.of(
                    SubjectAttributes.of(profileData.get(PARAM_ID).toString(), Attributes.fromMap(profileData)),
                    contextAttributes);
            AuthenticationResult authenticationResult = new AuthenticationResult(attributes);

            return Optional.of(authenticationResult);

        } catch (IOException e) {
            _logger.warn("Could not communicate with profile endpoint", e);

            throw _exceptionFactory.internalServerException(ErrorCode.EXTERNAL_SERVICE_ERROR, "Authentication failed");
        }
    }

    @Override
    public String getCallbackUrl() {
        return _provider.getFullyQualifiedAuthenticationUri().toString() + CALLBACK_URI_SUFFIX;
    }
}
