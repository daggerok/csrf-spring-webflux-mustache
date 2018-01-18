package daggerok.security;

import daggerok.users.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME;

@Slf4j
@ControllerAdvice
public class SecurityAdvice {

  /**
   * @return CSRF:see src/main/resources/templates/index.mustache
   */
  @ModelAttribute("_csrf")
  Mono<CsrfToken> csrfToken(final ServerWebExchange exchange) {

    final Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());

    return csrfToken.doOnSuccess(token -> exchange.getAttributes()
                                                  .put(DEFAULT_CSRF_ATTR_NAME, token));
  }

  @ModelAttribute("currentUser") // 1: replace `Principle` with project `User`
  Mono<User> currentUser(@AuthenticationPrincipal final Mono<User> currentUser) {
    return currentUser;
  }
}
