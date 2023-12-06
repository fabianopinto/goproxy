package fabianopinto.goproxy.simplerest.service;

import fabianopinto.goproxy.simplerest.data.ContactRepository;
import fabianopinto.goproxy.simplerest.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository repository;

    public ContactServiceImpl(ContactRepository repository) {
        this.repository = repository;
    }

    @Override
    public Contact create(Contact contact) {
        return repository.save(contact);
    }

    @Override
    public Optional<Contact> read(String group, Long id) {
        return repository.findByGroupAndId(group, id);
    }

    @Override
    public List<Contact> read(String group) {
        return makeList(repository.findByGroup(group));
    }

    @Override
    public List<Contact> read(String group, String lastName) {
        return makeList(repository.findByGroupAndLastName(group, lastName));
    }

    @Override
    public List<Contact> read(String group, Timestamp version) {
        return makeList(repository.findByGroupAndVersionAfterOrderByVersionAsc(group, version, Pageable.ofSize(10)));
    }

    private List<Contact> makeList(Iterable<Contact> contacts) {
        var list = new ArrayList<Contact>();
        contacts.forEach(list::add);
        return list;
    }

    @Override
    public void update(String group, Long id, Contact contact) {
        var current = repository.findByGroupAndId(group, id).orElseThrow();
        if (contact.getFirstName() != null) {
            current.setFirstName(contact.getFirstName());
        }
        if (contact.getLastName() != null) {
            current.setLastName(contact.getLastName());
        }
        if (contact.getEmailAddress() != null) {
            current.setEmailAddress(contact.getEmailAddress());
        }
        if (contact.getPhoneNumber() != null) {
            current.setPhoneNumber(contact.getPhoneNumber());
        }
        if (contact.getPersonalNotes() != null) {
            current.setPersonalNotes(contact.getPersonalNotes());
        }
        LOGGER.debug("update -- id {}, body {}, result {}", id, contact, current);
        repository.save(current);
    }

    @Override
    public void delete(String group, Long id) {
        if (repository.existsByGroupAndId(group, id)) {
            repository.deleteById(id);
        }
    }

}
