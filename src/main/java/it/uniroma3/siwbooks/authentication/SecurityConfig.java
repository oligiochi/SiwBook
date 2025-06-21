package it.uniroma3.siwbooks.authentication;

import it.uniroma3.siwbooks.service.CustomOAuth2UserService;
import it.uniroma3.siwbooks.service.CustomOidcUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import static it.uniroma3.siwbooks.models.Credentials.ADMIN_ROLE;
import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    @Lazy
    private CustomOidcUserService customOidcUserService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception {
        https
                .csrf(csrf -> csrf.disable()).cors(cors->cors.disable())
                .authorizeHttpRequests(requests -> requests
                        // pagine e risorse su cui tutti possono fare GET
                        .requestMatchers(HttpMethod.GET,"/","/login","/register","/index","/author/**","/authors","/search","/book/**","/books","/css/**", "/images/**", "favicon.ico","/js/**").permitAll()
                        // pagine e risorse su cui tutti possono fare POST
                        .requestMatchers(HttpMethod.POST,"/search","/register","/login").permitAll()

                        // pagine e risorse su cui solo gli ADMIN possono fare GET
                        .requestMatchers(HttpMethod.GET,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
                        // pagine e risorse su cui solo gli ADMIN possono fare POST
                        .requestMatchers(HttpMethod.POST,"/admin/**").hasAnyAuthority(ADMIN_ROLE)

                        // pagine non elencate sopra richiedono auth (autenticato come ADMIN o come DEFAULT)
                        .anyRequest().authenticated()
                ).formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/success", true)
                        .failureHandler((request, response, exception) -> {
                            System.out.println("Login fallito: " + exception.getMessage());
                            exception.printStackTrace();
                            response.sendRedirect("/login?error=true");
                        }))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/success", true)
                        .userInfoEndpoint(userInfo->userInfo
                                .userService(new OAuth2UserService<OAuth2UserRequest, OAuth2User>() {
                                    @Override
                                    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
                                        if (userRequest instanceof OidcUserRequest) {
                                            return customOidcUserService.loadUser((OidcUserRequest) userRequest);
                                        }
                                        return customOAuth2UserService.loadUser(userRequest);
                                    }
                                })
                        )
                )
                .logout(logout->logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .clearAuthentication(true).permitAll());
        return https.build();
    }

}
