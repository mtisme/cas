package org.apereo.cas.oidc.authn;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.oidc.OidcConstants;
import org.apereo.cas.support.oauth.authenticator.OAuth20AccessTokenAuthenticator;
import org.apereo.cas.ticket.accesstoken.OAuth20AccessToken;
import org.apereo.cas.token.JwtBuilder;
import org.apereo.cas.util.LoggingUtils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;

/**
 * This is {@link OidcClientConfigurationAccessTokenAuthenticator}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Slf4j
public class OidcClientConfigurationAccessTokenAuthenticator extends OAuth20AccessTokenAuthenticator {
    public OidcClientConfigurationAccessTokenAuthenticator(
        final CentralAuthenticationService centralAuthenticationService,
        final JwtBuilder accessTokenJwtBuilder) {
        super(centralAuthenticationService, accessTokenJwtBuilder);
    }

    @Override
    protected CommonProfile buildUserProfile(final TokenCredentials tokenCredentials,
                                             final WebContext webContext, final OAuth20AccessToken accessToken) {
        try {
            val profile = super.buildUserProfile(tokenCredentials, webContext, accessToken);
            LOGGER.trace("Examining access token [{}] for required scope [{}]", accessToken, OidcConstants.CLIENT_CONFIGURATION_SCOPE);
            if (accessToken.getScopes().contains(OidcConstants.CLIENT_CONFIGURATION_SCOPE)) {
                return profile;
            }
        } catch (final Exception e) {
            LoggingUtils.error(LOGGER, e);
        }
        return null;
    }
}
