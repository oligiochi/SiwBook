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
        return "login2";
    }

    @GetMapping("/success")
    public String defaultAfterLogin(Authentication authentication,Model model) {
        return "redirect:/";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("credentials",new Credentials());
        return "registrazione";
    }
}
