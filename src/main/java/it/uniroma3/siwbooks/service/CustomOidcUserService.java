package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.AuthenticatedUser;
import it.uniroma3.siwbooks.models.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    @Autowired
    private OAuthUserService oauthUserService; // la tua classe esistente

    private final OidcUserService delegate = new OidcUserService();

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oidcUser.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Credentials cred = oauthUserService.findOrCreateFromOAuth(attributes, provider);
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + cred.getRole());

        return new AuthenticatedUser(
                cred.getUser().getId(),
                cred.getUsername(),
                Collections.singleton(authority),
                attributes
        );
    }
}
