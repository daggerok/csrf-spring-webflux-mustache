package daggerok.web;

import daggerok.users.User;
import daggerok.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  final PasswordEncoder encoder;

  @GetMapping("/")
  public Rendering index() {

    return Rendering.view("index")
                    .modelAttribute("users", users.findTop19ByLastModifiedAtNotNullOrderByLastModifiedAtDesc())
                    .build();
  }

  @PostMapping("/")
  public Mono<String> post(final Mono<User> user, final Mono<Principal> principal) {

    return Flux.zip(principal, user)
               // ${owner.name}-${user.username}
               .map(p -> format("%s-%s", p.getT1().getName(), p.getT2().getUsername()))
               .map(r -> new User().setUsername(r))
               .map(u -> u.setPassword(encoder.encode(u.getUsername())))
               .map(u -> u.setLastModifiedAt(now()))
               .flatMap(users::save)
               .then(Mono.just("redirect:/"));
  }
}
