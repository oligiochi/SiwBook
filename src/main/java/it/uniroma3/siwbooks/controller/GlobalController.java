package it.uniroma3.siwbooks.controller;

import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalController {
    @Autowired
    private UtenteService userService;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @ModelAttribute("userDetails")
    public UserDetails getUser() {
        UserDetails user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return user;
    }
    @ModelAttribute("currentUser")
    public Utente getCurrentUser(){
        return userService.getCurrentUser();
    }

    @ExceptionHandler(Exception.class)
    public String handleAllErrors(Exception ex, WebRequest request, Model model) {
        // Timestamp
        String timestamp = LocalDateTime.now().format(FORMATTER);

        // HTTP request path
        String path = request.getDescription(false)
                .replace("uri=", "");

        // Exception type
        String exceptionType = ex.getClass().getSimpleName();

        // Stack trace snippet (first 5 lines)
        String stackTrace = Arrays.stream(ex.getStackTrace())
                .limit(5)
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        // Add attributes to the model
        model.addAttribute("timestamp", timestamp);
        model.addAttribute("path", path);
        model.addAttribute("exceptionType", exceptionType);
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("stackTrace", stackTrace);

        // Return your Thymeleaf template customError.html
        return "customError";
    }

}