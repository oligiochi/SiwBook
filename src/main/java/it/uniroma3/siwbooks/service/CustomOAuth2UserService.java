package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.AuthenticatedUser;
import it.uniroma3.siwbooks.models.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private CredentialsService credentialsService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oauth2User = super.loadUser(request);
        Map<String,Object> attributes = oauth2User.getAttributes();
        String provider = request.getClientRegistration().getRegistrationId();

        Credentials credentials = credentialsService.findOrCreateFromOAuth(attributes, provider);

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + credentials.getRole());

        return new AuthenticatedUser(
                credentials.getUser().getId(),
                credentials.getUsername(),
                Collections.singleton(authority),
                attributes
        );
    }
}

