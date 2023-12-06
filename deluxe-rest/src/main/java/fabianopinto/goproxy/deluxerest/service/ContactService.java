package fabianopinto.goproxy.deluxerest.service;

import fabianopinto.goproxy.deluxerest.model.Contact;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

public interface ContactService {

    Mono<Contact> create(Mono<Contact> contact);

    Mono<Contact> read(String group, Long id);

    Flux<Contact> read(String group);

    Flux<Contact> read(String group, String lastName);

    Flux<Contact> read(String group, Timestamp version);

    Mono<Contact> update(String group, Long id, Mono<Contact> contact);

    Mono<Void> delete(String group, Long id);

}
