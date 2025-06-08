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

    @Transactional
    public Credentials findOrCreateFromOAuth(Map<String, Object> attributes, String provider) {
        String providerId = extractProviderId(attributes, provider);
        if (providerId == null) {
            throw new IllegalArgumentException("OAuth2 non ha fornito un'ID valido per provider " + provider);
        }

        Optional<Credentials> credById = credentialsRepository
                .findByOauthProviderAndOauthId(provider, providerId); // Cerco prima le credenziali via (provider + providerId)
        if (credById.isPresent()) {
            Credentials existing = credById.get();

            // Propagazione eventuale aggiornamento anagrafica
            Utente u = existing.getUser();
            updateUserFromAttributes(u, attributes, provider);
            return existing;
        }

        String email = extractEmail(attributes, provider);  // Se non ho trovato nulla, posso usare l’email per: evitare collisioni e popolare 'username'
        if (email == null || !email.contains("@")) {
            email = providerId + "@" + provider + ".local"; //Incaso di non email si usa providerId+"@"+provider+".local"
        } else {
            // Controllo collisione con utenti interni già registrati con quella email
            Optional<Credentials> credByEmail = credentialsRepository.findByUsername(email);
            if (credByEmail.isPresent() && !credByEmail.get().isOauthUser()) {
                // se esiste un utente tradizionale con la stessa email, gestisco il conflitto
                throw new IllegalStateException(
                        "Esiste già un account interno registrato con questa email: " + email
                );
            }
        }

        //Creo nuovo Utente + Credentials
        String firstName = extractName(attributes, provider);
        String lastName  = extractSurname(attributes, provider);

        Utente newUtente = new Utente();
        newUtente.setName(firstName);
        newUtente.setSurname(lastName);
        newUtente.setEmail(email);
        utenteRepository.save(newUtente);
        Credentials newCred = new Credentials();
        newCred.setUsername(email);
        // Genero password fittizia, la hash-o comunque
        newCred.setPassword(UUID.randomUUID().toString());
        newCred.setRole(Credentials.DEFAULT_ROLE);
        newCred.setOauthUser(true);
        newCred.setOauthProvider(provider);
        newCred.setOauthId(providerId);

        newCred.setUser(newUtente);
        credentialsRepository.save(newCred);

        return newCred;
    }
    //METODI TRADUZIONE DA OUTH2 a DATI LOCALI
    //extractProviderId
    private String extractProviderId(Map<String, Object> attributes, String provider) {
        if ("google".equalsIgnoreCase(provider)) {
            // Google: attributo "sub"
            return (String) attributes.get("sub");

        }
        if ("github".equalsIgnoreCase(provider)) {
            // GitHub: attributo "id" numerico → converto in String
            Object idObj = attributes.get("id");
            return (idObj != null) ? String.valueOf(idObj) : null;
        }
        if ("facebook".equalsIgnoreCase(provider)) {
            // Facebook: attributo "id"
            return (String) attributes.get("id");
        }
        // altri provider...
        return null;
    }

    //extractEmail
    private String extractEmail(Map<String, Object> attributes, String provider) {
        String email = (String) attributes.get("email");
        if (email != null && email.contains("@")) {
            return email;
        }
        if ("github".equalsIgnoreCase(provider)) {
            Object rawEmails = attributes.get("emails");
            if (rawEmails instanceof Iterable) {
                @SuppressWarnings("unchecked")
                Iterable<Map<String, Object>> emailList = (Iterable<Map<String, Object>>) rawEmails;
                for (Map<String, Object> e : emailList) {
                    Boolean primary = (Boolean) e.get("primary");
                    Boolean verified = (Boolean) e.get("verified");
                    String candidate = (String) e.get("email");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified) && candidate != null) {
                        return candidate;
                    }
                }
                for (Map<String, Object> e : emailList) {
                    Boolean verified = (Boolean) e.get("verified");
                    String candidate = (String) e.get("email");
                    if (Boolean.TRUE.equals(verified) && candidate != null) {
                        return candidate;
                    }
                }
            }
            return null;
        }
        // Facebook e altri
        return (String) attributes.get("email");
    }

    //extractSurname
    private String extractSurname(Map<String, Object> attributes, String provider) {
        if ("google".equalsIgnoreCase(provider)) {
            return (String) attributes.getOrDefault("family_name", null);
        }
        if ("github".equalsIgnoreCase(provider)) {
            String fullName = (String) attributes.get("name");
            if (fullName != null && fullName.contains(" ")) {
                String[] parts = fullName.split(" ");
                return parts[parts.length - 1];
            }
            return null;
        }
        if ("facebook".equalsIgnoreCase(provider)) {
            return (String) attributes.get("last_name");
        }
        return null;
    }

    //extractName
    private String extractName(Map<String, Object> attributes, String provider) {
        if ("google".equalsIgnoreCase(provider)) {
            return (String) attributes.getOrDefault("given_name", attributes.get("name"));
        }
        if ("github".equalsIgnoreCase(provider)) {
            // GitHub non obbliga ad avere “given_name” e “family_name”
            String fullName = (String) attributes.get("name"); // es. "Mario Rossi"
            if (fullName != null && fullName.contains(" ")) {
                return fullName.split(" ")[0];
            }
            return fullName;
        }
        if ("facebook".equalsIgnoreCase(provider)) {
            return (String) attributes.get("first_name");
        }
        // default: se c’è “given_name” o “name”, altrimenti null
        return (String) attributes.getOrDefault("given_name", attributes.get("name"));
    }

    private void updateUserFromAttributes(Utente user, Map<String, Object> attributes, String provider) {
        String firstName = extractName(attributes, provider);
        String lastName = extractSurname(attributes, provider);
        String email = extractEmail(attributes, provider);

        if (firstName != null) {
            user.setName(firstName);
        }
        if (lastName != null) {
            user.setSurname(lastName);
        }
        if (email != null && email.contains("@")) {
            user.setEmail(email);
        }
    }
}
