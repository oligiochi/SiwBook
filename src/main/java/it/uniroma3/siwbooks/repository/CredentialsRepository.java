package it.uniroma3.siwbooks.repository;

import it.uniroma3.siwbooks.models.Credentials;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
    Optional<Credentials> findByUsername(String username);
    Optional<Credentials> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}
