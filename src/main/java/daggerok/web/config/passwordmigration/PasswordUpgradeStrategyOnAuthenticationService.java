package daggerok.web.config.passwordmigration;

import daggerok.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class PasswordUpgradeStrategyOnAuthenticationService implements ReactiveAuthenticationManager {

  final UserRepository users;
  final PasswordEncoder encoder;
  final ReactiveUserDetailsService userDetailsService;

  ReactiveAuthenticationManager delegate;

  @PostConstruct
  public void init() {

    final UserDetailsRepositoryReactiveAuthenticationManager repositoryReactiveAuthenticationManager
        = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

    repositoryReactiveAuthenticationManager.setPasswordEncoder(encoder);
    delegate = repositoryReactiveAuthenticationManager;
  }

  @Override
  public Mono<Authentication> authenticate(final Authentication authentication) {

    return delegate.authenticate(authentication)
                   .delayUntil(this::updatePassword)
        ;
  }

  private Publisher<?> updatePassword(final Authentication authentication) {

    return users.findByUsername(authentication.getName())
                .log("old")
                .publishOn(Schedulers.parallel())
                .doOnSuccess(u -> u.setPassword(encoder.encode(authentication.getCredentials()
                                                                             .toString())))
                .log("new")
                .flatMap(users::save)
        ;
  }
}
