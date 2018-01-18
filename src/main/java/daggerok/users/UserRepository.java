package daggerok.users;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

  // use getCreatedBy(username) instead
  Flux<User> findTop10ByUsernameContainingIgnoreCaseAndLastModifiedAtNotNullOrderByLastModifiedAtDesc(
      @Param("username") final String username
  );

  default Flux<User> getCreatedBy(final String username) {
    return findTop10ByUsernameContainingIgnoreCaseAndLastModifiedAtNotNullOrderByLastModifiedAtDesc(username);
  }

  // use getAny() instead
  Flux<User> findTop10ByLastModifiedAtNotNullOrderByLastModifiedAtDesc();

  default Flux<User> getAny() {
    return findTop10ByLastModifiedAtNotNullOrderByLastModifiedAtDesc();
  }

  Mono<User> findByUsername(final String username);
}
