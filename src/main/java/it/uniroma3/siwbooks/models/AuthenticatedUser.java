package it.uniroma3.siwbooks.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class AuthenticatedUser implements UserDetails, OAuth2User, OidcUser {
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes; // attributi OAuth2

    // ───────────────────────────────────────────────────────────────
    // Costruttore per login form (UserDetails)
    // ───────────────────────────────────────────────────────────────
    public AuthenticatedUser(Long id,
                               String username,
                               String password,
                               Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.attributes = null; // non usati nel login form
    }

    // ───────────────────────────────────────────────────────────────
    // Costruttore per OAuth2 (non richiede password, ma ID e attributi)
    // ───────────────────────────────────────────────────────────────
    public AuthenticatedUser(Long id,
                               String username,
                               Collection<? extends GrantedAuthority> authorities,
                               Map<String, Object> attributes) {
        this.id = id;
        this.username = username;
        this.password = null; // OAuth2 non usa la password locale
        this.authorities = authorities;
        this.attributes = attributes;
    }

    // Ordine consigliato: prima i get “custom”, poi i metodi di UserDetails e OAuth2User

    /** Restituisce l’ID (utile nell’app se vuoi es. generare un token JWT con l’ID) */
    public Long getId() {
        return id;
    }

    // ───────────────────────────────────────────────────────────────
    // Metodi di UserDetails (login form)
    // ───────────────────────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /** Se vuoi permettere blocco/sblocco account, modifica qui */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ───────────────────────────────────────────────────────────────
    // Metodi di OAuth2User (login via provider)
    // ───────────────────────────────────────────────────────────────

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * getName() viene chiamato da Spring Security come “ID univoco” dell’utente OAuth2.
     * Qui puoi restituire username (es. email) o un altro identificatore se preferisci.
     */
    @Override
    public String getName() {
        return username;
    }

    @Override
    public Map<String, Object> getClaims() {
        return Map.of();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }
}
