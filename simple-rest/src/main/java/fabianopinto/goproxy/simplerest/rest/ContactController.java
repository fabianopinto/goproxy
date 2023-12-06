package fabianopinto.goproxy.simplerest.rest;

import fabianopinto.goproxy.simplerest.model.Contact;
import fabianopinto.goproxy.simplerest.service.ContactService;
import jakarta.persistence.PersistenceException;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
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
    public ResponseEntity<Void> create(@RequestBody Contact contact, UriComponentsBuilder uri) {
        LOGGER.debug("create -- body {}", contact);
        try {
            var created = service.create(contact);
            var location = uri.path("/contact/{group}/{id}").build(created.getGroup(), created.getId());
            return ResponseEntity.created(location).build();
        } catch (DataAccessException e) {
            LOGGER.warn("create -- exception {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{group}")
    public ResponseEntity<List<Contact>> read(
            @PathVariable String group,
            @RequestParam Optional<String> lastName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<Instant> version) {
        LOGGER.debug("read -- group {}, lastName {}, version {}", group, lastName, version);
        if (lastName.isPresent()) {
            return ResponseEntity.ok(service.read(group, lastName.get()));
        } else if (version.isPresent()) {
            return ResponseEntity.ok(service.read(group, new Timestamp(version.get().toEpochMilli() + 1)));
        }
        return ResponseEntity.ok(service.read(group));
    }

    @GetMapping("/{group}/{id}")
    public ResponseEntity<Contact> read(@PathVariable String group, @PathVariable Long id) {
        LOGGER.debug("read -- group {}, id {}", group, id);
        var contact = service.read(group, id);
        return contact.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{group}/{id}")
    public ResponseEntity<Void> update(@PathVariable String group, @PathVariable Long id, @RequestBody Contact contact) {
        LOGGER.debug("update -- group {}, id {}, body {}", group, id, contact);
        if (id.equals(contact.getId()) || contact.getId() == null) {
            try {
                service.update(group, id, contact);
                return ResponseEntity.accepted().build();
            } catch (NoSuchElementException e) {
                LOGGER.warn("update -- NoSuchElementException {}", e.getMessage());
                return ResponseEntity.notFound().build();
            } catch (PersistenceException e) {
                LOGGER.warn("update -- PersistenceException {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } catch (Exception e) {
                LOGGER.warn("update -- Exception {}", e.getMessage());
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{group}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String group, @PathVariable Long id) {
        LOGGER.debug("delete -- group {}, id {}", group, id);
        service.delete(group, id);
        return ResponseEntity.noContent().build();
    }

}
