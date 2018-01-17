package daggerok.web;

import daggerok.users.User;
import daggerok.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexPage {

  final UserRepository users;

  @GetMapping("/")
  public Rendering index() {

    return Rendering.view("index")
                    .modelAttribute("users", users.findTop19ByLastModifiedAtNotNullOrderByLastModifiedAtDesc())
                    .build();
  }

  @PostMapping("/")
  public Mono<String> post(final Mono<User> user, final Mono<Principal> principal) {

    return Flux.zip(principal, user)
               .map(p -> format("%s-by-%s", p.getT2().getUsername(), p.getT1().getName()))
               .map(r -> new User().setUsername(r))
               .map(u -> u.setPassword(u.getUsername()))
               .map(u -> u.setLastModifiedAt(now()))
               .flatMap(users::save)
               .then(Mono.just("redirect:/"));
  }
}
