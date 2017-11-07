# LinkedIn Authenticator Plugin #

LinkedIn Oauth Authenticator plugin for the Curity Identity Server.



## Config 
Name                    |   Default                                               | Description
------------------------|  -------------------------------------------------------|  ------------------------------------------------------------
`Client ID`             |                                                         | Linkedin app client id
`Client Secret`         |                                                         | Linkedin app secret key
`Authorization Endpoint`| https://www.linkedin.com/oauth/v2/authorization         | URL to the LinkedIn authorization endpoint
`Token Endpoint`        | https://www.linkedin.com/oauth/v2/accessToken           | URL to the LinkedIn authorization endpoint
`Scope`                 |                                                         | A space-separated list of scopes to request from LinkedIn
`User Info Endpoint`    | https://api.linkedin.com/v1/people/~?format=json        | URL to the LinkedIn userinfo(profile) endpoint

## Build plugin
First, collect credentials to the Curity Nexus, to be able to fetch the SDK. Add nexus credentials in maven settings.

Then, build the plugin by:
`mvn clean package`

Please visit [curity.io](https://curity.io/) for more information about the Curity Identity Server.
