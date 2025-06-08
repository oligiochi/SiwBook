// Service to handle all OAuth user provisioning logic
package it.uniroma3.siwbooks.service;

import it.uniroma3.siwbooks.models.Credentials;
import it.uniroma3.siwbooks.models.Utente;
import it.uniroma3.siwbooks.repository.CredentialsRepository;
import it.uniroma3.siwbooks.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OAuthUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Transactional
    public Credentials findOrCreateFromOAuth(Map<String, Object> attributes, String provider) {
        String providerId = extractProviderId(attributes, provider);
        if (providerId == null) {
            throw new IllegalArgumentException("ID provider non valido per: " + provider);
        }

        // Cerca credenziali esistenti
        Optional<Credentials> existing = credentialsRepository.findByOauthProviderAndOauthId(provider, providerId);
        if (existing.isPresent()) {
            Credentials cred = existing.get();
            updateUserFromAttributes(cred.getUser(), attributes, provider);
            return cred;
        }

        // Validazione e preparazione email
        String email = extractEmail(attributes, provider);
        email = validateEmail(email, providerId, provider);
        Optional<Credentials> conflict = credentialsRepository.findByUsername(email);
        if (conflict.isPresent() && !conflict.get().isOauthUser()) {
            throw new IllegalStateException("Email già registrata: " + email);
        }

        // Crea utente e credenziali
        Utente user = createUser(attributes, provider, email);
        Credentials cred = createCredentials(attributes, provider, providerId, email, user);

        // Salva solo le credenziali: cascade persiste anche Utente
        return credentialsRepository.save(cred);
    }

    private String validateEmail(String email, String providerId, String provider) {
        if (email == null || !email.contains("@")) {
            return providerId + "@" + provider + ".local";
        }
        return email;
    }

    private Utente createUser(Map<String, Object> attrs, String provider, String email) {
        Utente u = new Utente();
        u.setName(extractName(attrs, provider));
        u.setSurname(extractSurname(attrs, provider));
        u.setEmail(email);
        return u;
    }

    private Credentials createCredentials(Map<String, Object> attrs, String provider,
                                          String providerId, String email, Utente user) {
        Credentials c = new Credentials();
        c.setUsername(email);
        c.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        c.setRole(Credentials.DEFAULT_ROLE);
        c.setOauthUser(true);
        c.setOauthProvider(provider);
        c.setOauthId(providerId);
        c.setUser(user);
        return c;
    }

    private void updateUserFromAttributes(Utente u, Map<String, Object> attrs, String provider) {
        String fn = extractName(attrs, provider);
        String ln = extractSurname(attrs, provider);
        String em = extractEmail(attrs, provider);
        if (fn != null) u.setName(fn);
        if (ln != null) u.setSurname(ln);
        if (em != null && em.contains("@")) u.setEmail(em);
    }

    private String extractProviderId(Map<String, Object> attributes, String provider) {
        return switch (provider.toLowerCase()) {
            case "google"  -> (String) attributes.get("sub");
            case "github"  -> String.valueOf(attributes.get("id"));
            case "facebook"-> (String) attributes.get("id");
            default          -> null;
        };
    }

    private String extractEmail(Map<String, Object> attributes, String provider) {
        if ("github".equalsIgnoreCase(provider)) {
            return extractGitHubEmail(attributes);
        }
        return (String) attributes.get("email");
    }

    private String extractGitHubEmail(Map<String, Object> attributes) {
        Object raw = attributes.get("emails");
        if (raw instanceof Iterable) {
            for (Object o : (Iterable<?>) raw) {
                if (o instanceof Map<?, ?> m && Boolean.TRUE.equals(m.get("primary"))) {
                    return (String) m.get("email");
                }
            }
        }
        return null;
    }

    private String extractSurname(Map<String, Object> attributes, String provider) {
        return switch (provider.toLowerCase()) {
            case "google"   -> (String) attributes.get("family_name");
            case "github"   -> extractGitHubName(attributes).split(" ")[1];
            case "facebook" -> (String) attributes.get("last_name");
            default           -> null;
        };
    }

    private String extractName(Map<String, Object> attributes, String provider) {
        return switch (provider.toLowerCase()) {
            case "google"   -> (String) attributes.getOrDefault("given_name", attributes.get("name"));
            case "github"   -> extractGitHubName(attributes).split(" ")[0];
            case "facebook" -> (String) attributes.get("first_name");
            default           -> (String) attributes.getOrDefault("given_name", attributes.get("name"));
        };
    }

    private String extractGitHubName(Map<String, Object> attributes) {
        return (String) attributes.get("name");
    }
}