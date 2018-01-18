package daggerok.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {

    http
        .authorizeExchange()
          .pathMatchers("/api/**")
            .hasRole("ADMIN") // only admin can use rest API
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

  /**
   * default encoder:        bcrypt
   * other possible options: ldap, MD4, MD5, noop, pbkdf2, scrypt, SHA-1, SHA-256, sha256
   *
   * old system has next users:
   *
   * 1. {"id":"5a60262e5d1c502e3aef4754","username":"user","password":"$2a$10$tf/t/evHJ4i1gKsVI5Ua.eE08vO3K1NAI4R2ulci0bA/e6yrQpF.e","lastModifiedAt":"2018-01-18T06:44:30.498"}
   * 2. {"id":"5a60262e5d1c502e3aef4755","username":"admin","password":"$2a$10$TTrvcHS07vIJZfHgy3bjr.EcceeDNjqgkJ0Bb4sDEDZ/ogxsBmYh.","lastModifiedAt":"2018-01-18T06:44:30.643"}
   * ...
   *
   * so we have to migrate then to support password migration:
   *
   * userRepository.findAll()
   *               .doOnNext(user -> user.setPassword(format("{bcrypt}%s", user.getPassword())))
   *               .flatMap(userRepository::save)
   *               .subscribe(s -> log.info("'password' migrated to '{bcrypt}password'))
   *
   * after migrations users looks like so:
   *
   * 1. {"id":"5a60262e5d1c502e3aef4754","username":"user","password":"{bcrypt}$2a$10$tf/t/evHJ4i1gKsVI5Ua.eE08vO3K1NAI4R2ulci0bA/e6yrQpF.e","lastModifiedAt":"2018-01-18T06:44:30.498"}
   * 2. {"id":"5a60262e5d1c502e3aef4755","username":"admin","password":"{bcrypt}$2a$10$TTrvcHS07vIJZfHgy3bjr.EcceeDNjqgkJ0Bb4sDEDZ/ogxsBmYh.","lastModifiedAt":"2018-01-18T06:44:30.643"}
   * ...
   *
   */
  @Bean PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
