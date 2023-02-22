package jnu.se.btp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@Data
@NoArgsConstructor

@Entity
@Table(name = "resourcebase")
public class ResourceFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    private int year;

    private String semester;
    private String field;

    private String description;

    private String url;
    private String hash;
    private String extension;

}
