package fabianopinto.goproxy.simplerest.service;

import fabianopinto.goproxy.simplerest.model.Contact;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ContactService {

    Contact create(Contact contact);

    Optional<Contact> read(String group, Long id);

    List<Contact> read(String group);

    List<Contact> read(String group, String lastName);

    List<Contact> read(String group, Timestamp version);

    void update(String group, Long id, Contact contact);

    void delete(String group, Long id);

}
