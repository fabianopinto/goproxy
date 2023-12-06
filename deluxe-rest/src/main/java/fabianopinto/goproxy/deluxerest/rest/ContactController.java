package fabianopinto.goproxy.deluxerest.rest;

import fabianopinto.goproxy.deluxerest.model.Contact;
import fabianopinto.goproxy.deluxerest.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/contact")
public class ContactController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@RequestBody Contact contact, UriComponentsBuilder uri) {
        LOGGER.debug("create -- body {}", contact);
        return service.create(Mono.just(contact))
                .flatMap(created -> Mono.just(ResponseEntity.created(getLocation(uri, created)).build()))
                .onErrorMap(e -> {
                    LOGGER.warn("create -- exception {}", e.getMessage());
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                });
    }

    private static URI getLocation(UriComponentsBuilder uri, Contact created) {
        return uri.path("/contact/{group}/{id}").build(created.getGroup(), created.getId());
    }

    @GetMapping("/{group}")
    public Flux<Contact> read(
            @PathVariable String group,
            @RequestParam Optional<String> lastName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<Instant> version) {
        LOGGER.debug("read -- group {}, lastName {}, version {}", group, lastName, version);
        if (lastName.isPresent()) {
            return service.read(group, lastName.get());
        } else if (version.isPresent()) {
            return service.read(group, new Timestamp(version.get().toEpochMilli() + 1));
        }
        return service.read(group);
    }

    @GetMapping("/{group}/{id}")
    public Mono<Contact> read(@PathVariable String group, @PathVariable Long id) {
        LOGGER.debug("read -- group {}, id {}", group, id);
        return service.read(group, id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "group %s, id %d".formatted(group, id))));
    }

    @PutMapping("/{group}/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> update(@PathVariable String group, @PathVariable Long id, @RequestBody Contact contact) {
        LOGGER.debug("update -- group {}, id {}, body {}", group, id, contact);
        if (id.equals(contact.getId()) || contact.getId() == null) {
            return service.update(group, id, Mono.just(contact))
                    .onErrorMap(NoSuchElementException.class, e -> {
                        LOGGER.warn("update -- NoSuchElementException", e);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "group %s, id %d".formatted(group, id));
                    })
                    .onErrorMap(DataAccessException.class, e -> {
                        LOGGER.warn("update -- DataAccessException", e);
                        return new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                    })
                    .then();
        }
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{group}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String group, @PathVariable Long id) {
        LOGGER.debug("delete -- group {}, id {}", group, id);
        return service.delete(group, id);
    }

}
