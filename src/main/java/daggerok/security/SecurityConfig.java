package daggerok.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Configuration
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {

    http
        .authorizeExchange()
          .pathMatchers("/profiles/admin/**", "/api/**")
            .access(this::isAdmin) // custom decision to REST API security
          .pathMatchers("/favicon.ico", "/css/**", "/webjars/**")
            .permitAll()
          .anyExchange()
            .authenticated()
            .and()
        .httpBasic()
          .and()
        .formLogin()
          .and()
        .logout()
    ;

    return http.build();
  }

  private Mono<AuthorizationDecision> isAdmin(final Mono<Authentication> authentication, final AuthorizationContext authorizationContext) {

    return authentication.map(Authentication::getName)
                         .map(login -> login.toLowerCase().contains("admin"))
                         .map(AuthorizationDecision::new);
  }

  @Bean PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
