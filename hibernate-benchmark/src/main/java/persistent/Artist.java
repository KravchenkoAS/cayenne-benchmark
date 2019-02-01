package persistent;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Artist")
public class Artist {

    @Column(name = "DATE_OF_BIRTH")
//    @Basic( fetch = FetchType.LAZY )
    private Date dateOfBirth;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME")
//    @Basic( fetch = FetchType.LAZY )
    private String name;


    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
