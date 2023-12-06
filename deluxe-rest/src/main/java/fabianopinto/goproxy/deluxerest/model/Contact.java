package fabianopinto.goproxy.deluxerest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Contact {

    @Id
    private Long id;
    @Version
    private Long version;

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String personalNotes;

    @LastModifiedDate
    private Timestamp lastModified;

    @Column("group_name")
    private String group;

}
