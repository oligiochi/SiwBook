package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    protected UtenteRepository userRepository;

    @Transactional
    public Utente getById(Long id) {
        Optional<Utente> result = this.userRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Utente saveUser(Utente user) {
        return this.userRepository.save(user);
    }

    @Transactional
    public List<Utente> getAllUsers() {
        List<Utente> result = new ArrayList<>();
        Iterable<Utente> iterable = this.userRepository.findAll();
        for(Utente user : iterable)
            result.add(user);
        return result;
    }
    public Utente getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return credentialsService.getCredentials(userDetails.getUsername()).getUser();
    }

}
