package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.models.Credentials;
import it.uniroma3.siwbooks.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IndexController {

    private final CredentialsService credentialsService;
    private static final String INDEX_VIEW = "index.html";
    private static final String ADMIN_INDEX_VIEW = "admin/indexAdmin.html";

    @Autowired
    public IndexController(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @GetMapping("/")
    public String showIndex(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return INDEX_VIEW;
        }

        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        if (Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
            // Optionally pass flash attributes
            redirectAttributes.addFlashAttribute("user", userDetails.getUsername());
            return "redirect:/admin";
        }

        // Default view for authenticated non-admin users
        return INDEX_VIEW;
    }

    @GetMapping("/admin")
    public String showAdminIndex() {
        return INDEX_VIEW;
    }
}
