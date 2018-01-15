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

package io.curity.identityserver.plugin.linkedin.config;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultString;
import se.curity.identityserver.sdk.config.annotation.DefaultURI;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.service.WebServiceClientFactory;
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider;

import java.net.URI;
import java.util.Optional;

@SuppressWarnings("InterfaceNeverImplemented")
public interface LinkedInAuthenticatorPluginConfig extends Configuration {
    @Description("client id")
    String getClientId();

    @Description("Secret key used for communication with linkedin")
    String getClientSecret();

    @Description("URL to the LinkedIn authorization endpoint")
    @DefaultURI("https://www.linkedin.com/oauth/v2/authorization")
    URI getAuthorizationEndpoint();

    @Description("URL to the LinkedIn token endpoint")
    @DefaultURI("https://www.linkedin.com/oauth/v2/accessToken")
    URI getTokenEndpoint();

    @Description("A space-separated list of scopes to request from LinkedIn")
    @DefaultString("")
    String getScope();

    @Description("URL to the LinkedIn userinfo(profile) endpoint")
    @DefaultURI("https://api.linkedin.com/v1/people/~?format=json")
    URI getUserInfoEndpoint();

    @Description("The HTTP client with any proxy and TLS settings that will be used to connect to slack")
    Optional<HttpClient> getHttpClient();

    SessionManager getSessionManager();

    ExceptionFactory getExceptionFactory();

    AuthenticatorInformationProvider getAuthenticatorInformationProvider();

    WebServiceClientFactory getWebServiceClientFactory();

    Json getJson();

}
