package daggerok.users;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

  Flux<User> findTop19ByLastModifiedAtNotNullOrderByLastModifiedAtDesc();

  Mono<User> findByUsername(final String username);
}
