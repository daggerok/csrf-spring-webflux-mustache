package daggerok.web;

import daggerok.user.User;
import daggerok.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexPage {

  final UserRepository users;
  final PasswordEncoder encoder;

  @GetMapping("/")
  public Rendering index(@AuthenticationPrincipal final User owner) {

    final Flux<User> sharedUsers = users.getAny().share();

    return Rendering.view("index")
                    .modelAttribute("users", sharedUsers)
                    .modelAttribute("createdByMe",
                                    sharedUsers.filter(u -> u.getUsername()
                                                             .startsWith(format("%s-", owner.getUsername()))))
                    .build();
  }

  @PostMapping("/")
  public Mono<String> post(final Mono<User> user,
                           @AuthenticationPrincipal final Mono<User> owner) {

    return Flux.zip(owner, user)
               .map(p -> format("%s-%s", p.getT1().getUsername(), p.getT2().getUsername()))
               .map(r -> new User().setUsername(r))
               .map(u -> u.setLastModifiedAt(now()))
               .subscribeOn(Schedulers.parallel())
               .doOnNext(u -> u.setPassword(encoder.encode(u.getUsername())))
               .flatMap(users::save)
               .then(Mono.just("redirect:/"))
        ;
  }
}
