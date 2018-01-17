package daggerok;

import daggerok.users.User;
import daggerok.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import reactor.core.publisher.Flux;

import static java.time.LocalDateTime.now;

@Slf4j
@SpringBootApplication
@EnableReactiveMongoRepositories(considerNestedRepositories = true, basePackageClasses = User.class)
public class WebappApplication {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
    return new HiddenHttpMethodFilter();
  }

  @Bean
  InitializingBean initializingBean(final UserRepository users,
                                    final PasswordEncoder encoder) { // 6

    return () -> users.deleteAll()
                      .thenMany(v -> Flux.just("user", "admin")
                                         .map(user -> new User().setUsername(user)
                                                                .setPassword(encoder.encode(user)) // 7
                                                                .setLastModifiedAt(now()))
                                         .flatMap(users::save)
                                         .subscribe(u -> log.info("created user: {}", u.getUsername())))
                      .subscribe();
  }

  public static void main(String[] args) {
    SpringApplication.run(WebappApplication.class, args);
  }
}
