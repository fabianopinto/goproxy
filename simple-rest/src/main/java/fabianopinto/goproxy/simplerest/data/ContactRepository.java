package fabianopinto.goproxy.simplerest.data;

import fabianopinto.goproxy.simplerest.model.Contact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ContactRepository extends CrudRepository<Contact, Long> {

    Optional<Contact> findByGroupAndId(String group, Long id);

    Iterable<Contact> findByGroup(String group);

    Iterable<Contact> findByGroupAndLastName(String group, String lastName);

    List<Contact> findByGroupAndVersionAfterOrderByVersionAsc(String group, Timestamp version, Pageable pageable);

    boolean existsByGroupAndId(String group, Long id);

}
