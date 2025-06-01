package it.uniroma3.siwbooks.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
// ...

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("userDetails")
    public Object getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        // Caso 1: login “classico” con UserDetails
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }

        // Caso 2: login via OpenID Connect (OIDC)
        if (principal instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal;
            // Puoi restituire direttamente l’OidcUser, o estrarre un attributo a tua scelta:
            // – oidcUser.getEmail(), oidcUser.getName(), oidcUser.getClaims().get("preferred_username"), ecc.
            return oidcUser;
        }

        // Caso 3: login via OAuth2 non-OIDC (es. “OAuth2User” generico)
        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            return oauth2User;
        }

        // Altri casi (es. AnonymousAuthenticationToken)
        return null;
    }

    // …
}
