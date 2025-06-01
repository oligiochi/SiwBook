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
    public String defaultAfterLogin(Model model) {
        String username = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
            // logica per login tradizionale
        } else if (principal instanceof DefaultOidcUser oidcUser) {
            String email = oidcUser.getEmail(); // o oidcUser.getAttribute("email")
            username = oidcUser.getFullName(); // o .getAttribute("name")
            // logica per login via Google
        }
        Credentials credentials = credentialsService.getCredentials(username);
        if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            return "admin/indexAdmin.html";
        }
        return "index.html";
    }
}
