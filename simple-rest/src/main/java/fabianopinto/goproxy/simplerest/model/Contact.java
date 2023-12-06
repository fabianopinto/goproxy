package fabianopinto.goproxy.simplerest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Version;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Timestamp version;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true)
    private String emailAddress;
    private String phoneNumber;
    @Column(columnDefinition = "TEXT")
    private String personalNotes;

    @Column(name = "group_name", nullable = false, length = 20)
    private String group;

}
