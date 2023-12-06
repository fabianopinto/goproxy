package fabianopinto.goproxy.deluxerest.data;

import fabianopinto.goproxy.deluxerest.model.Contact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

public interface ContactRepository extends ReactiveCrudRepository<Contact, Long> {

    Mono<Contact> findByGroupAndId(String group, Long id);

    Flux<Contact> findByGroup(String group);

    Flux<Contact> findByGroupAndLastName(String group, String lastName);

    Flux<Contact> findByGroupAndLastModifiedAfterOrderByVersionAsc(String group, Timestamp lastModTimestamp, Pageable pageable);

    Mono<Boolean> existsByGroupAndId(String group, Long id);

}
