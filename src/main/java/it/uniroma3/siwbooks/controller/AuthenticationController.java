package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.models.Credentials;
import it.uniroma3.siwbooks.service.CredentialsService;
import it.uniroma3.siwbooks.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import java.util.Map;
@Controller
class AuthenticationController {
    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private UtenteService userService;

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
    @GetMapping(value = "/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index.html";
        }
        else {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
                return "admin/indexAdmin.html";
            }
        }
        return "index.html";
    }
    @GetMapping("/success")
    public String defaultAfterLogin(Authentication authentication,Model model) {
        Credentials credentials;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            // --- flusso OAuth2 (Google, GitHub, ecc.) ---
            String provider = oauthToken.getAuthorizedClientRegistrationId(); // "google" o "github"
            Map<String,Object> attributes = oauthToken.getPrincipal().getAttributes();

            // find or create user+credentials da OAuth
            credentials = credentialsService.findOrCreateFromOAuth(attributes, provider);

        } else {
            // --- flusso form-login tradizionale ---
            String username = authentication.getName();
            credentials = credentialsService.getCredentials(username);
            if (credentials == null) {
                // (opzionale) gestisci caso utente non presente
                throw new IllegalStateException("Credenziali non trovate per: " + username);
            }
        }

        // Redirect in base al ruolo
        if (Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
            return "admin/indexAdmin.html";
        }
        model.addAttribute("userDetails", credentials);
        return "index.html";
    }
}
