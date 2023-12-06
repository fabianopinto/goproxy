package fabianopinto.goproxy.deluxerest.service;

import fabianopinto.goproxy.deluxerest.data.ContactRepository;
import fabianopinto.goproxy.deluxerest.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository repository;

    public ContactServiceImpl(ContactRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Contact> create(Mono<Contact> contact) {
        return contact.flatMap(repository::save);
    }

    @Override
    public Mono<Contact> read(String group, Long id) {
        return repository.findByGroupAndId(group, id);
    }

    @Override
    public Flux<Contact> read(String group) {
        return repository.findByGroup(group);
    }

    @Override
    public Flux<Contact> read(String group, String lastName) {
        return repository.findByGroupAndLastName(group, lastName);
    }

    @Override
    public Flux<Contact> read(String group, Timestamp version) {
        return repository.findByGroupAndLastModifiedAfterOrderByVersionAsc(group, version, Pageable.ofSize(10));
    }

    @Override
    public Mono<Contact> update(String group, Long id, Mono<Contact> update) {
        return Mono.zip(repository.findByGroupAndId(group, id)
                        .switchIfEmpty(Mono.error(new NoSuchElementException())), update)
                .flatMap(t -> repository.save(updateContact(t.getT1(), t.getT2())));
    }

    private Contact updateContact(Contact current, Contact update) {
        if (update.getFirstName() != null) {
            current.setFirstName(update.getFirstName());
        }
        if (update.getLastName() != null) {
            current.setLastName(update.getLastName());
        }
        if (update.getEmailAddress() != null) {
            current.setEmailAddress(update.getEmailAddress());
        }
        if (update.getPhoneNumber() != null) {
            current.setPhoneNumber(update.getPhoneNumber());
        }
        if (update.getPersonalNotes() != null) {
            current.setPersonalNotes(update.getPersonalNotes());
        }
        LOGGER.debug("update -- body {}, result {}", update, current);
        return current;
    }

    @Override
    public Mono<Void> delete(String group, Long id) {
        return repository.existsByGroupAndId(group, id)
                .flatMap(exists -> exists ? repository.deleteById(id) : Mono.empty());
    }

}
