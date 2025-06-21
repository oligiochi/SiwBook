package it.uniroma3.siwbooks.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static it.uniroma3.siwbooks.models.Credentials.ADMIN_ROLE;

public class SecurityUtil {
        public static boolean isIsAdmin() {
            GrantedAuthority adminAuth = new SimpleGrantedAuthority(ADMIN_ROLE);
            return SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getAuthorities()
                    .contains(adminAuth);
        }
}
