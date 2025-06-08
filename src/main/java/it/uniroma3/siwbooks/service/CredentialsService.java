package it.uniroma3.siwbooks.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import it.uniroma3.siwbooks.models.Credentials;
import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.repository.CredentialsRepository;
import it.uniroma3.siwbooks.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing operations related to user credentials.
 * It provides methods to retrieve, save, and manage credentials,
 * including OAuth-based account creation and synchronization.
 */
@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UtenteRepository utenteRepository;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    public Credentials saveCredentials(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return credentialsRepository.save(credentials);
    }
}
