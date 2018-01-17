package daggerok.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {

    http
        .authorizeExchange()
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

  // 1
  @Bean PasswordEncoder passwordEncoder() {
    return new StandardPasswordEncoder();
  }
}
