# LinkedIn Authenticator Plugin #

LinkedIn Oauth Authenticator plugin for the Curity Identity Server.

Create [LinkedIn app](https://developer.linkedin.com/docs/oauth2) 

Create LinkedIn Authenticator and configure following values.

### Config 
Name                    |   Default                                               | Description
------------------------|  -------------------------------------------------------|  ------------------------------------------------------------
`Client ID`             |                                                         | Linkedin app client id
`Client Secret`         |                                                         | Linkedin app secret key
`Authorization Endpoint`| https://www.linkedin.com/oauth/v2/authorization         | URL to the LinkedIn authorization endpoint
`Token Endpoint`        | https://www.linkedin.com/oauth/v2/accessToken           | URL to the LinkedIn authorization endpoint
`Scope`                 |                                                         | A space-separated list of scopes to request from LinkedIn
`User Info Endpoint`    | https://api.linkedin.com/v1/people/~?format=json        | URL to the LinkedIn userinfo(profile) endpoint

### Build plugin
First, collect credentials to the Curity Nexus, to be able to fetch the SDK. Add nexus credentials in maven settings.

Then, build the plugin by:
`mvn clean package`

### Install plugin
To install a plugin into the server, simply drop its jars and all of its required resources, including Server-Provided Dependencies, in the `<plugin_group>` directory.    
Please visit [curity.io/plugins](https://support.curity.io/docs/latest/developer-guide/plugins/index.html#plugin-installation) for more information about plugin installation.

####Required dependencies/jars
Following jars should be in plugin group classpath.  

*  [commons-codec-1.9.jar](http://central.maven.org/maven2/commons-codec/commons-codec/1.9/commons-codec-1.9.jar)
*  [commons-logging-1.2.jar](http://central.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar)
*  [google-collections-1.0-rc2.jar](http://central.maven.org/maven2/com/google/collections/google-collections/1.0-rc2/google-collections-1.0-rc2.jar)
*  [httpclient-4.5.jar](http://central.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5/httpclient-4.5.jar)
*  [httpcore-4.4.1.jar](http://central.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.1/httpcore-4.4.1.jar)
*  [identityserver.plugins.authenticators-1.0.0.jar](https://github.com/curityio/authenticator-plugin)



Please visit [curity.io](https://curity.io/) for more information about the Curity Identity Server.
