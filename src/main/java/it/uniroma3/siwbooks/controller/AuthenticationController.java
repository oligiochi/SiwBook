package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.controller.validator.PasswordValidator;
import it.uniroma3.siwbooks.models.Credentials;
import it.uniroma3.siwbooks.service.CredentialsService;
import it.uniroma3.siwbooks.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
class AuthenticationController {
    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private UtenteService userService;

    @Autowired
    private PasswordValidator passwordValidator;

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
    //confirmPassword
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("credentials") Credentials credentials, @ModelAttribute("confirmPassword") String Conferma, BindingResult bindingResult, Model model) {
        passwordValidator.validate(credentials, bindingResult);
        if(!Conferma.equals(credentials.getPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return register(model);
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return register(model);
        }
        credentialsService.saveCredentials(credentials);
        return "redirect:login";
    }
}
