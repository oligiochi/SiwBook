package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.AuthenticatedUser;
import it.uniroma3.siwbooks.models.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private OAuthUserService oauthUserService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        try {
            OAuth2User oauth2User = super.loadUser(request);
            Map<String, Object> attr = oauth2User.getAttributes();
            String provider = request.getClientRegistration().getRegistrationId();

            Credentials cred = oauthUserService.findOrCreateFromOAuth(attr, provider);
            if (cred == null || cred.getUser() == null || cred.getUser().getId() == null) {
                throw new OAuth2AuthenticationException("Failed to create or retrieve user credentials");
            }

            SimpleGrantedAuthority auth = new SimpleGrantedAuthority("ROLE_" + cred.getRole());

            return new AuthenticatedUser(
                    cred.getUser().getId(),
                    cred.getUsername(),
                    Collections.singleton(auth),
                    attr
            );
        } catch (Exception e) {
            throw new OAuth2AuthenticationException("Authentication failed: "+String.valueOf(e));
        }
    }
}