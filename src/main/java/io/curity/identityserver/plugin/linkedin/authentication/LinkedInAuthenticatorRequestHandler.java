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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.authentication.AuthenticationResult;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.errors.ErrorCode;
import se.curity.identityserver.sdk.http.RedirectStatusCode;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static io.curity.identityserver.plugin.linkedin.authentication.RedirectUriUtil.createRedirectUri;
import static io.curity.identityserver.plugin.linkedin.descriptor.LinkedInAuthenticatorPluginDescriptor.CALLBACK;

public class LinkedInAuthenticatorRequestHandler implements AuthenticatorRequestHandler<Request>
{
    private static final Logger _logger = LoggerFactory.getLogger(LinkedInAuthenticatorRequestHandler.class);
    private static final String AUTHORIZATION_ENDPOINT = "https://www.linkedin.com/oauth/v2/authorization";

    private final LinkedInAuthenticatorPluginConfig _config;
    private final AuthenticatorInformationProvider _authenticatorInformationProvider;
    private final ExceptionFactory _exceptionFactory;

    public LinkedInAuthenticatorRequestHandler(LinkedInAuthenticatorPluginConfig config)
    {
        _config = config;
        _exceptionFactory = config.getExceptionFactory();
        _authenticatorInformationProvider = config.getAuthenticatorInformationProvider();
    }

    @Override
    public Optional<AuthenticationResult> get(Request request, Response response)
    {
        _logger.debug("GET request received for authentication");

        String redirectUri = createRedirectUri(_authenticatorInformationProvider, _exceptionFactory);
        String state = UUID.randomUUID().toString();
        Map<String, Collection<String>> queryStringArguments = new LinkedHashMap<>(5);
        Set<String> scopes = new LinkedHashSet<>(7);

        _config.getSessionManager().put(Attribute.of("state", state));

        queryStringArguments.put("client_id", Collections.singleton(_config.getClientId()));
        queryStringArguments.put("redirect_uri", Collections.singleton(redirectUri));
        queryStringArguments.put("state", Collections.singleton(state));
        queryStringArguments.put("response_type", Collections.singleton("code"));

        scopes.add("r_basicprofile");

        if (_config.isReadEmailAddress())
        {
            scopes.add("r_emailaddress");
        }
        if (_config.isManageCompanyPage())
        {
            scopes.add("rw_company_admin");
        }
        if (_config.isShareAccess())
        {
            scopes.add("w_share");
        }

        queryStringArguments.put("scope", Collections.singleton(String.join(" ", scopes)));

        _logger.debug("Redirecting to {} with query string arguments {}", AUTHORIZATION_ENDPOINT,
                queryStringArguments);

        throw _exceptionFactory.redirectException(AUTHORIZATION_ENDPOINT,
                RedirectStatusCode.MOVED_TEMPORARILY, queryStringArguments, false);
    }

    @Override
    public Optional<AuthenticationResult> post(Request request, Response response)
    {
        throw _exceptionFactory.methodNotAllowed();
    }

    @Override
    public Request preProcess(Request request, Response response)
    {
        return request;
    }
}
