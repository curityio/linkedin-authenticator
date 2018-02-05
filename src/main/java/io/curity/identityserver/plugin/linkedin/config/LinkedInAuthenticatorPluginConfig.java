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
import se.curity.identityserver.sdk.config.annotation.DefaultBoolean;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.service.WebServiceClientFactory;
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider;

import java.util.Optional;

@SuppressWarnings("InterfaceNeverImplemented")
public interface LinkedInAuthenticatorPluginConfig extends Configuration
{
    @Description("Client ID of the LinkedIn app")
    String getClientId();

    @Description("Client Secret")
    String getClientSecret();

    @Description("Request a scope (r_emailaddress) that grants access to manage primary email address you use for your LinkedIn account")
    @DefaultBoolean(false)
    boolean isReadEmailAddress();


    @Description("Request a scope (rw_company_admin) that grants access to manage your company page and post updates")
    @DefaultBoolean(false)
    boolean isManageCompanyPage();

    @Description("Request a scope (w_share) that grants access to post updates as you")
    @DefaultBoolean(false)
    boolean isShareAccess();

    @Description("The HTTP client with any proxy and TLS settings that will be used to connect to LinkedIn")
    Optional<HttpClient> getHttpClient();

    SessionManager getSessionManager();

    ExceptionFactory getExceptionFactory();

    AuthenticatorInformationProvider getAuthenticatorInformationProvider();

    WebServiceClientFactory getWebServiceClientFactory();

    Json getJson();

}
